package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

public class HerokuHandler {

	private static final String HEROKU_URI = "http://localhost:5000/";//"https://ads-tunnel.herokuapp.com/";//
	private static final String LOCAL_URI = "http://localhost:8080/project1/";
	
	public static void main(String[] args) {
		listen();
	}
	
	public static void listen() {
    	new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
		    		try {
		    			String req = HTTPHandler.get(HEROKU_URI + "docker_hello");
		    			System.out.println("Got client request: " + req);
		    			handle(req);
		    		} catch (Exception e) {
		    			//e.printStackTrace();
		    		}
		    	}
			}
    	}).start();
    }
    
    public static void handle(final String req) {
    	new Thread(new Runnable() {

			@Override
			public void run() {
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
			}
			
    	}).start();
    }
    
    public static void respond(String id, String response) throws ClientProtocolException, IOException {
    	HTTPHandler.post(HEROKU_URI + "docker_post", id, response);
    }
    
    public static String generateResponse(String url, JSONObject post) throws Exception {		//fetch html file from local web server
    	System.out.println(post.isEmpty());
    	if(!post.isEmpty())
    		return HTTPHandler.get(LOCAL_URI + url);
    	
    	return HTTPHandler.get(LOCAL_URI + url);		//post
//    	
//    	if(urlTail.equals("/"))
//			return "Le empty page";
//		if(urlTail.equals("/mandelbrot"))
//			return getHTMLAsString("C:/Users/pedro/Desktop/mandelbrot.html");
//		if(urlTail.equals("/test"))
//			return getHTMLAsString("C:/Users/pedro/Desktop/rick_roll.html");
//		if(urlTail.equals("/testfile"))
//			return getHTMLAsString("src/main/java/grupo5/project1/testFile.html");
    }
    
    private static String getHTMLAsString(String path) {
    	Scanner s;
		try {
			s = new Scanner(new File(path));
			String html = "";
	    	while(s.hasNextLine())
	    		html += s.nextLine() + "\n";
	    	return html;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	return "";
    }
    
    
    
}
