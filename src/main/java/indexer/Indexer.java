package indexer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Indexer class that is build on Lucene
 * 
 * @author Narasimman
 */
public class Indexer {
  private final IndexWriter writer;
  private static final String ERR_INVALID_PATH = "Invalid index path";

  /**
   * Expects a path where the indexed files will be stored
   * 
   * @param indexPath
   * @throws IOException
   */
  public Indexer(String indexPath) throws IOException {
    if (indexPath == null) {
      throw new IllegalArgumentException(ERR_INVALID_PATH);
    }

    Path path = FileSystems.getDefault().getPath(indexPath);
    Directory dir = FSDirectory.open(path);
    writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));
  }

  /**
   * call this to close the indexer instance
   * 
   * @throws IOException
   */
  public void close() throws IOException {
    writer.close();
  }

  /**
   * It takes a Post object and adds the index to document
   * 
   * @param Post
   *          post
   * @return
   * @throws IOException
   */
  public boolean index(Post post) throws IOException {
    if (post == null) {
      return false;
    }
    writer.addDocument(getDocument(post));
    return true;
  }

  /**
   * Number of documents Indexed
   * 
   * @return num
   */
  public int getNumberOfIndexedDocuments() {
    return writer.numDocs();
  }

  /**
   * Adds the following properties of a post into a lucene document id title
   * body acceptedAnswerId
   * 
   * @param post
   * @return Lucene Document
   */
  private Document getDocument(Post post) {
    if (post == null) {
      return null;
    }

    Document doc = new Document();
    doc.add(new IntField(PostField.ID.toString(), post.getId(), Field.Store.YES));
    
    if(post.getTitle() != null) {
      doc.add(new StringField(PostField.TITLE.toString(), post.getTitle(), Field.Store.YES));
    }
    
    if(post.getBody() != null) {
      doc.add(new StringField(PostField.BODY.toString(), post.getBody(), Field.Store.YES));
    }
    
    if(post.getAcceptedAnswerId() != 0) {
      doc.add(new IntField(PostField.ANSWERID.toString(), post.getAcceptedAnswerId(),
          Field.Store.YES));
    }
    return doc;
  }
}
