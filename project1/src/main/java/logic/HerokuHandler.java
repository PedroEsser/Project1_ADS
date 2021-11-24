package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;

public class HerokuHandler {

	private static final String HEROKU_URI = "https://ads-tunnel.herokuapp.com/";//"http://localhost:5000/";//
	
	public static void main(String[] args) {
		listen();
	}
	
	public static void listen() {
    	new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
		    		try {
		    			String res = HTTPHandler.get(HEROKU_URI + "docker_hello");
		    			System.out.println("Got query: " + res);
		    			handle(res);
		    		} catch (Exception e) {
		    			//e.printStackTrace();
		    		}
		    	}
			}
    		
    	}).start();
    }
    
    public static void handle(final String query) {
    	new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String[] split = query.split("#");		//client id # url tail # post
					
					int id = Integer.parseInt(split[0]);
					String urlTail = split[1];
					String post = "";
					if(split.length == 3)
						post = split[2];
					
					String response = generateResponse(urlTail, post);
					System.out.println("id: " + id + ", Answering to:" + urlTail);
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
    
    public static String generateResponse(String urlTail, String post) {		//fetch html file from local web server
    	System.out.println("Answering to: " + urlTail);
    	
    	if(!post.isEmpty())
    		System.out.println("With post data = "  + post);
    	
    	if(urlTail.equals("/"))
			return "Le empty page";
		if(urlTail.equals("/mandelbrot"))
			return getHTMLAsString("C:/Users/pedro/Desktop/mandelbrot.html");
		if(urlTail.equals("/test"))
			return getHTMLAsString("C:/Users/pedro/Desktop/rick_roll.html");
		if(urlTail.equals("/testfile"))
			return getHTMLAsString("src/main/java/grupo5/project1/testFile.html");
		
		return "";
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
