package grupo5.project1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;

public class HerokuHandler {

	private static final String HEROKU_URI = "https://ads-tunnel.herokuapp.com/";//"http://localhost:5000/";//
	
	public static void listen() {
    	new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
		    		try {
		    			String res = HTTPHandler.get(HEROKU_URI + "docker_connect");
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
					String[] split = query.split("/");
					String id = split[0];
					Integer.parseInt(id);
					String q = query.substring(query.indexOf('/') + 1);
					String response = generateResponse(q);
	    			respond(id, response);
	    		} catch (Exception e) {
	    			//e.printStackTrace();
	    		}
			}
			
    	}).start();
    }
    
    public static void respond(String id, String response) throws ClientProtocolException, IOException {
    	HTTPHandler.post(HEROKU_URI + "docker_post", id, response);
    }
    
    public static String generateResponse(String query) {		//fetch html file from local web server
    	System.out.println("Answering to: " + query);
    	if(query.isEmpty())
			return "Le empty page";
		if(query.equals("mandelbrot"))
			return getHTMLAsString("C:/Users/pedro/Desktop/mandelbrot.html");
		
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
