package indexer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
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

public class Retriever {
  public static final int MAX_LIMIT = 100;
  public static Map<String, String> search(String indexPath, String[] q) 
      throws IOException, org.apache.lucene.queryparser.classic.ParseException {
    
    Path path = FileSystems.getDefault().getPath(indexPath);
    Directory dir = FSDirectory.open(path);

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher is = new IndexSearcher(reader);
    QueryParser parser = new QueryParser(PostField.TITLE.toString(), new StandardAnalyzer());
    
    String queryStr = "";
    
    for(String s : q) {
      queryStr += s; 
    }
    
    Query query = parser.parse(queryStr);
    
    long start = System.currentTimeMillis();
    
    TopDocs hits = is.search(query, MAX_LIMIT);
    
    long end = System.currentTimeMillis();
    
    System.out.println("Found " + hits.totalHits + " document(s) (in "
        + (end - start) + " milliseconds) that matched query '" + queryStr + "':");

    Map<String, String> result = new HashMap<String, String>();

    for (int i = 0; i < hits.scoreDocs.length; i++) {
      ScoreDoc scoreDoc = hits.scoreDocs[i];
      
      Document doc = is.doc(scoreDoc.doc);
      
      String answerId = doc.get(PostField.ACCEPTEDANSWERID.toString());
      result.put(doc.get(PostField.ID.toString()), answerId);

    }
    return result;
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

    Map<String, String> result = search(indexPath, query);
    System.out.println(result);
  }
}