package search;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONException;
import org.json.JSONObject;

import parser.JSONParser;
import stackoverflow.Answer;
import stackoverflow.Post;
import stackoverflow.PostField;
import db.Database;

/**
 * Retriver that takes the index Path and the query and searches the index for
 * matches.
 * 
 * @author Narasimman
 * 
 */
public class Retriever {
  public static final int MAX_LIMIT = 100;
  private static final String NOT_FOUND = "Best Answer Not Found";
  private static final String ANSWER_QUERY = "Select ID, BODY, SCORE, PARENTID from Posts where PostTypeId='2' ";
  private Map<Integer, Post> postsMap;
  private final Database connection;
  private Ranker ranker;

  public Retriever(String dbPath) throws ClassNotFoundException, SQLException {
    postsMap = new HashMap<Integer, Post>();
    connection = new Database(dbPath);
    ranker = new Ranker(postsMap);
  }

  /**
   * Method that initiates the search functionality
   * @param indexPath
   * @param q
   * @return
   * @throws IOException
   * @throws org.apache.lucene.queryparser.classic.ParseException
   * @throws SQLException
   */
  private Post search(String indexPath, String[] q) throws IOException,
  org.apache.lucene.queryparser.classic.ParseException, SQLException {
    Path path = FileSystems.getDefault().getPath(indexPath);
    Directory dir = FSDirectory.open(path);

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher indexSearcher = new IndexSearcher(reader);

    Analyzer analyzer = new StandardAnalyzer();
    MultiFieldQueryParser parser = new MultiFieldQueryParser(
        new String[] {
            PostField.TITLE.toString(), 
            PostField.BODY.toString() 
        }, analyzer);

    String queryStr = "";
    for (String s : q) {
      queryStr += s + " ";
    }

    Query query = parser.parse(queryStr);

    long start = System.currentTimeMillis();

    //sort the index based on the score. 
    CustomScoreQuery customQuery = new MyOwnScoreQuery(query);
    TopDocs hits = indexSearcher.search(customQuery, MAX_LIMIT);

    long end = System.currentTimeMillis();

    System.out.println("Found " + hits.totalHits + " document(s) (in "
        + (end - start) + " milliseconds) that matched query '" + queryStr + "'");

    List<String> ansList = new ArrayList<String>();

    for (int i = 0; i < hits.scoreDocs.length; i++) {
      ScoreDoc scoreDoc = hits.scoreDocs[i];
      Document doc = indexSearcher.doc(scoreDoc.doc);
      double luceneScore = scoreDoc.score;
      System.out.println(doc.get(PostField.ID.toString()) + " --> " + luceneScore);
      String answerId = doc.get(PostField.ACCEPTEDANSWERID.toString());
      if (answerId != null) {
        ansList.add(answerId);
        postsMap.put(Integer.parseInt((doc.get(PostField.ID.toString()))),
            buildPost(doc, luceneScore));
      }      
    }

    populateAnswers(ansList, true);

    ranker.computePostRanks();
    Post result = ranker.getTopPost();
    //  System.out.println("BEST Post " + result);
    return result;
  }

  private void populateAnswers(List<String> ansList, boolean isLocal)
      throws IOException, SQLException {
    if(ansList == null || ansList.size() == 0) {
      System.out.println(NOT_FOUND);
      return;
    }

    if (isLocal) {
      String q = ANSWER_QUERY + " and ID in (";
      for (String id : ansList) {
        q += id + ",";
      }

      q = q.substring(0, q.length() - 1) + ")";
      // System.out.println(q);
      ResultSet rs = connection.executeQuery(q);

      while (rs.next()) {
        int parentId = rs.getInt(PostField.PARENTID.toString());
        int answerId = rs.getInt(PostField.ID.toString());
        int score = rs.getInt(PostField.SCORE.toString());
        String body = rs.getString(PostField.BODY.toString());
        Answer answer = new Answer(answerId, score, body);
        addToPost(parentId, answer);
      }

    } else {
      List<JSONObject> ansListJSON = JSONParser.getAnswers(ansList);
      addAnswer(ansListJSON);
    }
  }

  private void addToPost(int postId, Answer answer) {
    Post parentPost = postsMap.get(postId);
    if(parentPost != null && answer != null) {
      parentPost.setAnswer(answer);
    }
  }

  private void addAnswer(List<JSONObject> ansList) {
    for (JSONObject answer : ansList) {
      try {
        int parentId = answer.getInt("question_id");
        int answerId = answer.getInt("answer_id");
        int score = answer.getInt("score");
        String body = answer.getString("body");
        JSONObject user = answer.getJSONObject("owner");

        Answer ans = new Answer(answerId, score, body);
        ans.setUserScore(user.getInt("reputation"));

        addToPost(parentId, ans);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * build a post given a Lucene document and its associated lucene score
   * @param doc
   * @param luceneScore
   * @return
   */
  private Post buildPost(Document doc, double luceneScore) {
    int id = 0;
    int acceptedAnsId = 0;
    int score = 0;
    int viewCount = 0;
    int favCount = 0;

    if (doc.get(PostField.ID.toString()) != null) {
      id = Integer.parseInt(doc.get(PostField.ID.toString()));
    }

    if (doc.get(PostField.ACCEPTEDANSWERID.toString()) != null) {
      acceptedAnsId = Integer.parseInt(doc.get(PostField.ACCEPTEDANSWERID
          .toString()));
    }

    if (doc.get(PostField.SCORE.toString()) != null) {
      score = Integer.parseInt(doc.get(PostField.SCORE.toString()));
    }

    if (doc.get(PostField.VIEWCOUNT.toString()) != null) {
      viewCount = Integer.parseInt(doc.get(PostField.VIEWCOUNT.toString()));
    }

    if (doc.get(PostField.FAVORITECOUNT.toString()) != null) {
      favCount = Integer.parseInt(doc.get(PostField.FAVORITECOUNT.toString()));
    }

    Post post = new Post.PostBuilder(id)
    .acceptedAnswerId(acceptedAnsId)
    .score(score)
    .viewCount(viewCount)
    .favoriteCount(favCount)
    .luceneScore(luceneScore)
    .build();

    return post;
  }

  /**
   * retrieves answer body from the given post
   * @param post
   * @return
   */
  private String retrieveAnswer(Post post) {    
    if(post != null) {
      Answer answer = post.getAnswer();
      if(answer != null && !answer.getBody().isEmpty()) {
        String bestAnswer = answer.getBody();
        return bestAnswer;
      }
    }
    return NOT_FOUND;
  }

  /**
   * Given the path to the index file and a query, returns the best answer
   * based on our rank algorithm
   * @param indexPath
   * @param query
   * @return
   * @throws IOException
   * @throws org.apache.lucene.queryparser.classic.ParseException
   * @throws SQLException
   */
  public String retrieve(String indexPath, String query) throws IOException,
  org.apache.lucene.queryparser.classic.ParseException, SQLException {
    Post bestPost = search(indexPath, query.split(" "));
    if(bestPost != null) {
      return retrieveAnswer(bestPost);
    }
    return NOT_FOUND;
  }

  public static void main(String[] args) throws Exception {
    String usage = "Usage: " + Retriever.class.getName()
        + " [-index INDEX_PATH] [-q query terms]\n\n"
        + "This requires a path to the index file created by lucene"
        + " and query terms to search for";

    if (args.length < 3) {
      throw new Exception(usage);
    }
    CommandLine cmd = null;

    // set options
    Options options = new Options();
    options.addOption("index", "index", true, "Index Path");
    options.addOption("db", "db", true, "DB Path");
    Option qOption = new Option("q", "q", true, "Query");
    qOption.setArgs(Option.UNLIMITED_VALUES);

    options.addOption(qOption);

    CommandLineParser parser = new DefaultParser();

    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    String indexPath = cmd.getOptionValue("index");
    String dbPath = cmd.getOptionValue("db");
    String[] query = cmd.getOptionValues("q");

    Retriever ret = new Retriever(dbPath);

    String queryStr = "";
    for (String s : query) {
      queryStr += s + " ";
    }
    String result = ret.retrieve(indexPath, queryStr);

    //System.out.println(result);
  }
}
