package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

public class HerokuHandler {

	private static final int DOCKER_CLIENTS = 5;
	private static final String HEROKU_URI = "https://ads-tunnel.herokuapp.com";//"http://localhost:5000";//
	private static final String LOCAL_URI = "http://localhost:8080";
	
	public static void main(String[] args) {
		startServing();
	}
	
	private static void startServing() {
		for(int i = 0 ; i < DOCKER_CLIENTS ; i++)
			listen();
	}
	
	public static void listen() {
    	new Thread(() -> {
			while(true) {
	    		try {
	    			String req = HTTPHandler.get(HEROKU_URI + "/docker_hello");
	    			if(!req.equals("Timed out"))
	    				System.out.println("Got client request: " + req);
	    			handleRequest(req);
	    		} catch (Exception e) {
	    			//e.printStackTrace();
	    		}
			}
    	}).start();
    }
    
    public static void handleRequest(final String req) {
    	new Thread(() -> {
			try {
				JSONObject obj = JSONHandler.convertStringToJSON(req);
				
				int id = (int)obj.get("client_id");
				String url = (String)obj.get("url");
				JSONObject post = (JSONObject)obj.get("data");
				String response = generateResponse(url, post);
    			respond(id + "", response);
    		} catch (Exception e) {
    			//e.printStackTrace();
    		}
    	}).start();
    }
    
    public static void respond(String id, String response) throws ClientProtocolException, IOException {
    	HTTPHandler.post(HEROKU_URI + "/docker_post", id, response);
    }
    
    public static String generateResponse(String url, JSONObject post) throws Exception {		//fetch html file from local web server
    	if(post.isEmpty())
    		return HTTPHandler.get(LOCAL_URI + url);
    	
    	return HTTPHandler.post(LOCAL_URI + url, post);		
    }
    
}
