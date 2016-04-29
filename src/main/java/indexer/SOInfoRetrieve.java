package Project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.commons.io.*;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;	


public class SOInfoRetrieve {

	public static void main(String[] args) throws Exception
	{
		String url = args[0];
		System.out.println(args[0]);
		
		SOInfoRetrieve so = new SOInfoRetrieve(); 
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
               // String result= convertStreamToString(instream);
                String result = IOUtils.toString(instream, "UTF-8");
                System.out.println(result);
                returned =new JSONObject(result);                   
                instream.close();
            }
        } 
        catch (Exception e) { e.printStackTrace();} 

        return returned;
    }
	  
	  
//	  public void getGsonObj(String json)
//	  {
//		  	Gson gson = new GsonBuilder().create();
//			Address address=gson.fromJson(json, Address.class);
//			System.out.println(json);
//			System.out.println(address.toString());
//	  }
//	  
	  
}

