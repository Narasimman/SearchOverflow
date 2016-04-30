package indexer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONException;
import org.json.JSONObject;

import utils.JsonParser;

public class Retriever {

  public static final int MAX_LIMIT = 100;
  private Map<Integer, Post> postsMap;

  public Map<String, String> search(String indexPath, String[] q)
      throws IOException, org.apache.lucene.queryparser.classic.ParseException {
    Path path = FileSystems.getDefault().getPath(indexPath);
    Directory dir = FSDirectory.open(path);

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher is = new IndexSearcher(reader);
    QueryParser parser = new QueryParser(PostField.TITLE.toString(),
        new StandardAnalyzer());

    String queryStr = "";

    for (String s : q) {
      queryStr += s + " ";
    }

    Query query = parser.parse(queryStr);

    long start = System.currentTimeMillis();

    TopDocs hits = is.search(query, MAX_LIMIT);

    long end = System.currentTimeMillis();

    System.out.println("Found " + hits.totalHits + " document(s) (in "
        + (end - start) + " milliseconds) that matched query '" + queryStr
        + "':");

    Map<String, String> result = new HashMap<String, String>();
    // contains the list of Post objects with its ids with it's populated values
    postsMap = new HashMap<Integer, Post>();
    List<String> ansList = new ArrayList<String>();
    for (int i = 0; i < hits.scoreDocs.length; i++) {
      ScoreDoc scoreDoc = hits.scoreDocs[i];

      Document doc = is.doc(scoreDoc.doc);

      String answerId = doc.get(PostField.ACCEPTEDANSWERID.toString());
      result.put(doc.get(PostField.ID.toString()), answerId);
      ansList.add(answerId);

      // checking is anslist is working
      postsMap.put(Integer.parseInt((doc.get(PostField.ID.toString()))),
          constructPost(doc));
      List<JSONObject> ansListJSON = getAnswers(ansList);
      attachAnstoPost(ansListJSON);

    }

    return result;
  }

  public static String retrieve(String indexPath, String query) {
    return "Hello";
  }

  public List<JSONObject> getAnswers(List<String> ansList) {
    return JsonParser.getAnswers(ansList);
  }

  public void attachAnstoPost(List<JSONObject> ansList) {
    for (JSONObject answer : ansList) {
      try {
        int parentId = answer.getInt("question_id");
        System.out.println(parentId);

        Post parentPost = postsMap.get(parentId);

        System.out.println(parentPost);
        Answer ans = new Answer(answer.getInt("answer_id"));
        ans.setScore(answer.getInt("score"));
        parentPost.setAnsObj(ans);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  public static Post constructPost(Document doc) {
    int id = 0;
    int acceptedAnsId = 0;
    int score = 0;
    int viewCount = 0;
    int favCount = 0;

    if (doc.get(PostField.ID.toString()) != null) {
      id = Integer.parseInt(doc.get(PostField.ID.toString()));
    }

    String title = doc.get(PostField.TITLE.toString());
    String body = doc.get(PostField.BODY.toString());

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

    if (doc.get(PostField.VIEWCOUNT.toString()) != null) {
      favCount = Integer.parseInt(doc.get(PostField.FAVORITECOUNT.toString()));
    }

    return new Post(id, title, body, acceptedAnsId, score, viewCount, favCount);
  }

  public static void main(String[] args) throws Exception {
    String usage = "Usage: " + Retriever.class.getName()
        + " [-index INDEX_PATH] [-q query terms]\n\n"
        + "This requires a path to the index file created by lucene"
        + " and query terms to search for";

    if (args.length < 2) {
      throw new Exception(usage);
    }
    CommandLine cmd = null;

    // set options
    Options options = new Options();
    options.addOption("index", "index", true, "Index Path");
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
    String[] query = cmd.getOptionValues("q");
    Retriever ret = new Retriever();
    Map<String, String> result = ret.search(indexPath, query);

    System.out.println(result);
  }
}
