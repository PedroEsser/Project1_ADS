package logic;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

public class HerokuHandler {

	private static final int DOCKER_CLIENTS = 5;
	private static final String HEROKU_URI = "http://localhost:5000";//"https://ads-tunnel.herokuapp.com";//
	private static final String LOCAL_URI = "http://localhost:8080";
	
	public static void main(String[] args) {
		startServing();
	}
	
	public static void startServing() {
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
				
//				JSONObject headers = (JSONObject) obj.get("headers");
//				if(headers.has("referer")) 
//					headers.put("referer", headers.getString("referer").replace(HEROKU_URI, LOCAL_URI));
//				
//				headers.put("host", LOCAL_URI);
				JSONObject headers = null;
				String path = obj.getString("path");
				JSONObject body = (JSONObject)obj.get("body");
				String response = generateResponse(path, headers, body);
    			respond(id + "", response);
    		} catch (Exception e) {
    			//e.printStackTrace();
    		}
    	}).start();
    }
    
    public static void respond(String id, String response) throws ClientProtocolException, IOException {
    	HTTPHandler.post(HEROKU_URI + "/docker_post", id, response);
    }
    
    public static String generateResponse(String path, JSONObject headers, JSONObject body) throws Exception {		//fetch html file from local web server
    	if(body.isEmpty())
    		return HTTPHandler.get(LOCAL_URI + path, headers);
    	
    	return HTTPHandler.post(LOCAL_URI + path, body, headers);		
    }
    
}
