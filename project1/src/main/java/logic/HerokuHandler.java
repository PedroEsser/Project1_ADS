package logic;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

public class HerokuHandler {

	private static final int DOCKER_CLIENTS = 5;
	private static final String HEROKU_URI = "https://ads-tunnel.herokuapp.com";//"http://localhost:5000";//
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
	    		} catch (Exception e) {
	    			//e.printStackTrace();
	    		}
			}
    	}).start();
    }
    
    public static void handleRequest(final JSONObject req) {
    	new Thread(() -> {
			try {
				JSONObject obj = JSONHandler.convertStringToJSON(req.getString("data"));
				
				int id = (int)obj.get("client_id");
				
				JSONObject headers = obj.getJSONObject("headers");
				headers = null;
				
//				if(headers.has("referer")) 
//					headers.put("referer", headers.getString("referer").replace(HEROKU_URI, LOCAL_URI));
//				if(headers.has("origin")) 
//					headers.put("origin", LOCAL_URI);
//				if(headers.has("host")) 
//					headers.put("host", LOCAL_URI);
				
				String path = obj.getString("path");
				JSONObject body = obj.getJSONObject("body");
				JSONObject response = generateResponse(path, headers, body);
				
    			respond(id, response);
    		} catch (Exception e) {
    			//e.printStackTrace();
    		}
    	}).start();
    }
    
    public static void respond(int id, JSONObject response) throws ClientProtocolException, IOException {
    	HTTPHandler.post(HEROKU_URI + "/docker_post", id, response);
    }
    
    public static JSONObject generateResponse(String path, JSONObject headers, JSONObject body) throws Exception {		//fetch html file from local web server
    	if(body.isEmpty())
    		return HTTPHandler.get(LOCAL_URI + path, headers);
    	
    	return HTTPHandler.post(LOCAL_URI + path, body, headers);
    }
    
}
