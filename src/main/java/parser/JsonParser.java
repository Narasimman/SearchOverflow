package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
  private static final String BASE_URL = "https://api.stackexchange.com/2.2/";

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  private static List<JSONObject> getJson(String url) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(url);
    request.addHeader("accept", "application/json");

    HttpResponse response = null;
    List<JSONObject> result = new ArrayList<JSONObject>();

    InputStream instream = null;

    try {
      response = client.execute(request);
      HttpEntity entity = response.getEntity();

      if (entity != null) {
        instream = entity.getContent();
        // BufferedReader rd = new BufferedReader(new InputStreamReader(
        // new GZIPInputStream(instream), Charset.forName("UTF-8")));

        BufferedReader rd = new BufferedReader(new InputStreamReader(instream));
        String jsonText = readAll(rd);

        JSONObject json = new JSONObject(jsonText);

        JSONArray jsonItems = json.getJSONArray("items");

        for (int i = 0; i < jsonItems.length(); i++) {
          JSONObject jsonobject = jsonItems.getJSONObject(i);
          result.add(jsonobject);
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    } finally {
      instream.close();
    }

    return result;
  }

  public static List<JSONObject> getAnswers(List<String> ansList)
      throws IOException {
    String url = BASE_URL + "answers/";

    for (int i = 0; i < ansList.size(); i++) {
      url = url + ansList.get(i);
      if (i != ansList.size() - 1) {
        url += ";";
      }
    }
    url = url
        + "?order=desc&sort=activity&site=stackoverflow&filter=!9YdnSMKKT";

    System.out.println("Calling API with url: " + url);
    List<JSONObject> answers = getJson(url);

    return answers;
  }

}
