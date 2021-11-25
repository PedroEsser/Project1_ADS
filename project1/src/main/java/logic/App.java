package logic;

import java.io.FileNotFoundException;

public class App {
	
    public static void main( String[] args ) throws FileNotFoundException {
//    	GitHandler gh = new GitHandler("C:\\Users\\pedro\\git\\Knowledge_Base\\.git");
//    	gh.test("Test_Branch");

    	OWLHandler handler = new OWLHandler("ADS.owl");
    	JSONHandler.createTaxonomyJSON(handler.getTaxonomy());
    	JSONHandler.createIndividualsJSON(handler.getIndividualsClasses(), handler.getIndividualsDataProperties(), handler.getIndividualsObjectProperties());
    	JSONHandler.createDataPropertiesJSON(handler.getDataProperties());
    	JSONHandler.createObjectPropertiesJSON(handler.getObjectProperties());
    }
    
}