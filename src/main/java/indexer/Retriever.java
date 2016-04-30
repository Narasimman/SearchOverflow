package indexer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
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

public class Retriever {

	public static final int MAX_LIMIT = 100;
	private Map<Integer, Post> postObjs;
	
  public Map<String, String> search(String indexPath, String[] q) 
      throws IOException, org.apache.lucene.queryparser.classic.ParseException {
//    
//    Path path = FileSystems.getDefault().getPath(indexPath);
//    Directory dir = FSDirectory.open(path);
//
//    IndexReader reader = DirectoryReader.open(dir);
//    IndexSearcher is = new IndexSearcher(reader);
//    QueryParser parser = new QueryParser(PostField.TITLE.toString(), new StandardAnalyzer());
//    
//    String queryStr = "";
//    
//    for(String s : q) {
//      queryStr += s + " "; 
//    }
//    
//    Query query = parser.parse(queryStr);
//    
//    long start = System.currentTimeMillis();
//    
//    TopDocs hits = is.search(query, MAX_LIMIT);
//    
//    long end = System.currentTimeMillis();
//    
//    System.out.println("Found " + hits.totalHits + " document(s) (in "
//        + (end - start) + " milliseconds) that matched query '" + queryStr + "':");
//
   Map<String, String> result = new HashMap<String, String>();
//    //contains the list of Post objects with its ids with it's populated values
    postObjs = new HashMap<Integer, Post>();
    List<String> ansList = new ArrayList<String>();
//    for (int i = 0; i < hits.scoreDocs.length; i++) {
//      ScoreDoc scoreDoc = hits.scoreDocs[i];
//      
//      Document doc = is.doc(scoreDoc.doc);
//      
//      String answerId = doc.get(PostField.ACCEPTEDANSWERID.toString());
      //result.put(doc.get(PostField.ID.toString()), answerId);
   		result.put("2405","2444");
   		result.put("2348","2393");
   		result.put("1908","2492");
   //      //ansList.add(answerId);
   		ansList.add("2444");
        ansList.add("2492");
        ansList.add("2393");
        
        
      //checking is anslist is working
      //postObjs.put(Integer.parseInt((doc.get(PostField.ID.toString()))), constructPost(doc));
        postObjs.put(Integer.parseInt("2405"), constructfakePost(2405));
        postObjs.put(Integer.parseInt("2348"), constructfakePost(2348));
        postObjs.put(Integer.parseInt("1908"), constructfakePost(1908));
        JSONObject ansListJSON = getAnsJSON(ansList);
        attachAnstoPost(ansListJSON);
        
        System.out.println("post objects shoul be populated now " + (postObjs.get(1908)).getAnsObj());
        
//    }
  
   // System.out.println("postidand obj map " + postObjs);
    return result;
  }
  
  public static String retrieve(String indexPath, String query) {
    return "Hello";
  }

  public Post constructfakePost(int id)
  {
	  
	  int acceptedAnsId = 0;
	  int score =0 ;
	  int viewCount =0 ;
	  int favCount= 0 ;
	  String title = "";
	  String body = "";
			  
			  
	  return new Post(id, title, body, acceptedAnsId, score, viewCount, favCount);
  }
  
  public JSONObject getAnsJSON(List<String> ansList)
  {
	  utils.JsonParser JP = new utils.JsonParser();
	  JSONObject ansListJSON = JP.getAnsJSON(ansList);
	  return ansListJSON;
  }
  
  
  public void attachAnstoPost(JSONObject ansList)
  {
	  
	  System.out.println("inside anslist parse");
	  for(Iterator iterator = ansList.keys(); iterator.hasNext();) {
		    String key = (String) iterator.next();
		    try
		    {
		    	//if ( ansList.get(key) instanceof JSONObject ) {
		    	
		    	System.out.println(ansList.get(key));
		        JSONObject answer = new JSONObject(ansList.get(key).toString());
		        
		        int parentId = answer.getInt("question_id");
		        System.out.println(parentId);
		        
		        Post parentPost = postObjs.get(parentId);
		        
		        System.out.println(parentPost);
		        Answer ans = new Answer(answer.getInt("answer_id"));
		        ans.setScore(answer.getInt("score"));
		        parentPost.setAnsObj(ans);
		        
		    //}
		    	System.out.println(ansList.get(key));		    	
		    }
		    
		    catch(JSONException e) {e.printStackTrace();}
		    
		}
  }
  
  public static Post constructPost(Document doc)
  {
	  
	  int id = 0;
	  int acceptedAnsId = 0;
	  int score =0 ;
	  int viewCount =0 ;
	  int favCount= 0 ;
	  
	  if(doc.get(PostField.ID.toString()) != null )
	  {
		  id = Integer.parseInt(doc.get(PostField.ID.toString()));
	  }
	  String title = doc.get(PostField.TITLE.toString());
	  String body = doc.get(PostField.BODY.toString());
	  
	  if(doc.get(PostField.ACCEPTEDANSWERID.toString()) != null )
	  {
		  acceptedAnsId = Integer.parseInt(doc.get(PostField.ACCEPTEDANSWERID.toString()));
	  }
	  if(doc.get(PostField.SCORE.toString()) != null )
	  {
		  score = Integer.parseInt(doc.get(PostField.SCORE.toString()));
	  }
	  
	  if(doc.get(PostField.VIEWCOUNT.toString()) != null )
	  {
		  viewCount = Integer.parseInt(doc.get(PostField.VIEWCOUNT.toString()));
	  }
	  if(doc.get(PostField.VIEWCOUNT.toString()) != null )
	  {
		  favCount = Integer.parseInt(doc.get(PostField.FAVORITECOUNT.toString()));
	  }
	  
	  
	  
	  return new Post(id, title, body, acceptedAnsId, score, viewCount, favCount); 
  }
  
  public static void main(String[] args) throws Exception {
//    String usage = "Usage: " + Retriever.class.getName()
//        + " [-index INDEX_PATH] [-q query terms]\n\n"
//        + "This requires a path to the index file created by lucene"
//        + " and query terms to search for";
//
//    if (args.length < 2) {
//      throw new Exception(usage);
//    }
//    CommandLine cmd = null;
//
//    // set options
//    Options options = new Options();
//    options.addOption("index", "index", true, "Index Path");
//    Option qOption = new Option("q", "q", true, "Query");
//    qOption.setArgs(Option.UNLIMITED_VALUES);
//
//    options.addOption(qOption);
//
//    CommandLineParser parser = new DefaultParser();
//
//    try {
//      cmd = parser.parse(options, args);
//    } catch (ParseException e) {
//      e.printStackTrace();
//    }

//    String indexPath = cmd.getOptionValue("index");
//    String[] query = cmd.getOptionValues("q");
    Retriever ret = new Retriever();
   // Map<String, String> result = ret.search(indexPath, query);
	  Map<String, String> result = ret.search("", null);
    //System.out.println("post and answer map " +result);
   
//    String queryStr = "";

  //  for(String s : query) {
    //  queryStr += s + " "; 
    //}
    //Map<String, String> result = search(indexPath, queryStr);
    System.out.println(result);
  }
}
    