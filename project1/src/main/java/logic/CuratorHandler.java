package logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.codec.digest.DigestUtils;

public class CuratorHandler {
	
	private static HashMap<String, String> getCuratorCredentials(){
		try {
			HashMap<String, String> credentials = new HashMap<String, String>();
			Scanner scan = new Scanner(new File("curators.txt"));
			while(scan.hasNext()){
				String[] mailHashTuple = scan.nextLine().split(",");
				assert mailHashTuple.length == 2 : "Error parsing curators.txt";
				credentials.put(mailHashTuple[0], mailHashTuple[1]);
			}
			return credentials;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean authenticateCurator(String email, String password) {
		HashMap<String, String> credentials = getCuratorCredentials();
		return credentials.containsKey(email) && credentials.get(email).equals(hash(password));
	}
	
	public static void subscribeCurator(String email, String password) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("curators.txt", true)));
			String mailHashTuple = email + "," + hash(password);
			writer.println(mailHashTuple);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getCuratorMails(){
		return new LinkedList<>(getCuratorCredentials().keySet());
	}
	
	public static void sendMailToCurators(String title, String text) {
		for(String curatorMail : getCuratorMails()) 
			EmailHandler.sendMail(curatorMail, title, text);
	}
	
	private static String hash(String password) {
		return DigestUtils.sha256Hex(password);
	}
	
}