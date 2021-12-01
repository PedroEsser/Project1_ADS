package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class HTTPHandler {
	
	public static String get(String uri) throws Exception {
		HttpGet post = new HttpGet (uri);
		HttpClient client = new DefaultHttpClient();
		HttpResponse responseGET = client.execute(post);
		
	    return EntityUtils.toString(responseGET.getEntity());
	}
	
	public static String post(String uri, List<NameValuePair> params) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost(uri);

		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, "UTF-8");
		post.setEntity(ent);

		HttpClient client = new DefaultHttpClient();
		HttpResponse responsePOST = client.execute(post);
		String response = EntityUtils.toString(responsePOST.getEntity());
		System.out.println("POST to " + uri + " with response = " + response);
		return response;
	}
	
	public static String post(String uri, String id, String data) throws ClientProtocolException, IOException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("data", data));
		params.add(new BasicNameValuePair("id", id));
		return post(uri, params);
	}
	
	public static String post(String uri, JSONObject post) throws ClientProtocolException, IOException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		for(String key : post.keySet()) 
			params.add(new BasicNameValuePair(key, post.getString(key)));
		
		return post(uri, params);
	}
	
}
