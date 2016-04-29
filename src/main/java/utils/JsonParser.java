package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;


public class JsonParser {

  public static void main(String[] args) throws Exception
  {
    String url = args[0];
    System.out.println(args[0]);

    JsonParser so = new JsonParser(); 
    so.getJSONObj(url);


  }

  //	private static String readUrl(String urlString) throws Exception {
  //	    BufferedReader reader = null;
  //	    try {
  //	        URL url = new URL(urlString);
  //	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
  //	        StringBuffer buffer = new StringBuffer();
  //	        int read;
  //	        char[] chars = new char[1024];
  //	        while ((read = reader.read(chars)) != -1)
  //	            buffer.append(chars, 0, read); 
  //
  //	        return buffer.toString();
  //	    } finally {
  //	        if (reader != null)
  //	            reader.close();
  //	    }
  //	}
  //	

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  //returns the json object given the URL
  protected JSONObject getJSONObj(String url) {

    HttpClient httpclient = new DefaultHttpClient();
    HttpGet httpget = new HttpGet(url);
    httpget.addHeader("accept", "application/json");
    JSONObject returned = new JSONObject();
    HttpResponse response;

    try {
      response = httpclient.execute(httpget);
      HttpEntity entity = response.getEntity();

      if (entity != null) {
        InputStream instream = entity.getContent();
        BufferedReader rd = new BufferedReader(
            new InputStreamReader(new GZIPInputStream(instream), 
                Charset.forName("UTF-8")));
        String jsonText = readAll(rd);

        returned = new JSONObject(jsonText);
        System.out.println(returned);
        instream.close();
      }
    } 
    catch (Exception e) { e.printStackTrace();} 

    return returned;
  }
}

