package grupo5.project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.http.HttpResponse.BodyHandlers;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HTTPHandler {
	
	public static String get(String uri) throws Exception {
		java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
		java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
	          .uri(URI.create(uri))
	          .build();

		java.net.http.HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

	    return response.body();
	}
	
	public static void post(String uri, String id, String data) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost(uri);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("data", data));
		params.add(new BasicNameValuePair("id", id));

		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, "UTF-8");
		post.setEntity(ent);

		HttpClient client = new DefaultHttpClient();
		HttpResponse responsePOST = client.execute(post);
	}
	
}
