package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonParser {

  // public static void main(String[] args) throws Exception
  // {
  // String url = args[0];
  // System.out.println(args[0]);
  //
  // JsonParser so = new JsonParser();
  // so.getJSONObj(url);
  //
  // }

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  // returns the json object given the URL
  protected List<JSONObject> getJSONObj(String url) {

    HttpClient httpclient = new DefaultHttpClient();
    HttpGet httpget = new HttpGet(url);
    httpget.addHeader("accept", "application/json");
    JSONObject json = new JSONObject();
    HttpResponse response;
    List<JSONObject> result = new ArrayList<JSONObject>();

    try {
      response = httpclient.execute(httpget);
      HttpEntity entity = response.getEntity();

      if (entity != null) {
        InputStream instream = entity.getContent();
        BufferedReader rd = new BufferedReader(new InputStreamReader(
            new GZIPInputStream(instream), Charset.forName("UTF-8")));
        String jsonText = readAll(rd);

        json = new JSONObject(jsonText);

        JSONArray jsonItems = json.getJSONArray("items");

        for (int i = 0; i < jsonItems.length(); i++) {
          JSONObject jsonobject = jsonItems.getJSONObject(i);
          result.add(jsonobject);
        }

        instream.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  public static List<JSONObject> getAnswers(List<String> ansList) {

    String url = "https://api.stackexchange.com/2.2/answers/";
    for (int i = 0; i < ansList.size(); i++) {
      url = url + ansList.get(i);
      if (i != ansList.size() - 1) {
        url += ";";
      }
    }
    url = url + "?order=desc&sort=activity&site=stackoverflow";

    System.out.println("COnstructed ans id url is " + url);
    JsonParser jp = new JsonParser();
    List<JSONObject> anslistJSON = jp.getJSONObj(url);
    // System.out.println("after obj construction");
    return anslistJSON;
  }

}
