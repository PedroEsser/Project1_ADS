package logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class App {
	
    public static void main( String[] args ) throws FileNotFoundException {
//    	GitHandler gh = new GitHandler("C:\\Users\\pedro\\git\\Knowledge_Base\\.git");
//    	gh.test("Test_Branch");
    	
    	File initialFile = new File("./src/main/webapp/taxonomy.json");
        InputStream targetStream = new FileInputStream(initialFile);
    	
    	OWLHandler handler = new OWLHandler("ADS.owl");
    	JSONHandler.createTaxonomyJSON(handler.getTaxonomy());
    	JSONHandler.createIndividualsJSON(handler.getClassesAndTheirIndividuals(), handler.getIndividuals());
    	JSONHandler.createDataPropertiesJSON(handler.getDataProperties());
    	JSONHandler.createObjectPropertiesJSON(handler.getObjectProperties());
    	System.out.println(JSONHandler.convertJSONToString(targetStream));
    }
    
}