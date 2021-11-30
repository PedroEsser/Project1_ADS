package logic;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {
    
	public static void main(String[] args) {
		try {
			Server server = new Server(8080);
			WebAppContext webAppContext = new WebAppContext("src/main/webapp", "/");
			org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
		    classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
		    classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");
			server.setHandler(webAppContext);
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}