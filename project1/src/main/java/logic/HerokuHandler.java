package logic;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

public class HerokuHandler {

	private static final int DOCKER_CLIENTS = 5;
	private static final String HEROKU_URI = "https://ads-tunnel.herokuapp.com";
	private static final String LOCAL_URI = "http://localhost:8080";
	
	public static void startServing() {
		for(int i = 0 ; i < DOCKER_CLIENTS ; i++)
			listen();
	}
	
	public static void listen() {
    	new Thread(() -> {
			while(true) {
	    		try {
	    			JSONObject req = HTTPHandler.get(HEROKU_URI + "/docker_hello");
	    			if(!req.getString("data").equals("Timed out"))
	    				System.out.println("Got client request: " + req);
	    			handleRequest(req);
	    		} catch (Exception e) {}
			}
    	}).start();
    }
    
    public static void handleRequest(final JSONObject req) {
    	new Thread(() -> {
			try {
				JSONObject obj = JSONHandler.convertStringToJSON(req.getString("data"));
				
				String path = obj.getString("path");
				JSONObject body = obj.getJSONObject("body");
				
				int id = (int) obj.get("client_id"); //heroku client id
				JSONObject response = generateResponse(path, null, body);
    			respond(id, response);
    		} catch (Exception e) {}
    	}).start();
    }
    
    public static void respond(int id, JSONObject response) throws ClientProtocolException, IOException {
    	HTTPHandler.post(HEROKU_URI + "/docker_post", id, response);
    }
    
    //fetch html file from local web server
    public static JSONObject generateResponse(String path, JSONObject headers, JSONObject body) throws Exception {
    	if(body.isEmpty())
    		return HTTPHandler.get(LOCAL_URI + path, headers);
    	
    	return HTTPHandler.post(LOCAL_URI + path, body, headers);
    }
    
}
