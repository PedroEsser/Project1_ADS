package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class HTTPHandler {
	
	public static String get(String uri, JSONObject headers) throws Exception {
		HttpGet get = new HttpGet(uri);
		if(headers != null)
			for(String name : headers.keySet())
				get.setHeader(name, headers.getString(name));
		
		HttpClient client = new DefaultHttpClient();
		HttpResponse responseGET = client.execute(get);
		
	    return EntityUtils.toString(responseGET.getEntity());
	}
	
	public static String get(String uri) throws Exception {
	    return get(uri, null);
	}
	
	public static String post(String uri, List<NameValuePair> body, JSONObject headers) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost(uri);
		
		if(headers != null)
			for(String name : headers.keySet()) 
				post.setHeader(name, headers.getString(name));
				
		
		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(body, "UTF-8");
		post.setEntity(ent);

		HttpClient client = new DefaultHttpClient();
		HttpResponse responsePOST = client.execute(post);
		String response = EntityUtils.toString(responsePOST.getEntity());
		//System.out.println("POST to " + uri + " with response = " + response);
		return response;
	}
	
	public static String post(String uri, String id, String data) throws ClientProtocolException, IOException {
		List<NameValuePair> body = new ArrayList<NameValuePair>();
		body.add(new BasicNameValuePair("data", data));
		body.add(new BasicNameValuePair("id", id));
		return post(uri, body, null);
	}
	
	public static String post(String uri, JSONObject body, JSONObject headers) throws ClientProtocolException, IOException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		for(String key : body.keySet()) 
			params.add(new BasicNameValuePair(key, body.getString(key)));
		
		return post(uri, params, headers);
	}
	
}
