package logic;

import java.io.FileNotFoundException;

public class App {
	
    public static void main( String[] args ) throws FileNotFoundException {
    	GitHandler gh = new GitHandler("C:\\Users\\Simão Correia\\git\\Knowledge_Base_ADS\\.git");
//    	gh.test(gh.getNextBranchName("b"));
//    	OWLHandler handler = new OWLHandler("C:\\Users\\Utilizador\\Documents\\GitHub\\Knowledge_Base\\ontology.owl");
//    	System.out.println(handler.getIndividuals());
//    	JSONHandler.createTaxonomyJSON(handler.getTaxonomy());
//    	JSONHandler.createIndividualsJSON(handler.getIndividualsClasses(), handler.getIndividualsDataProperties(), handler.getIndividualsObjectProperties());
//    	JSONHandler.createDataPropertiesJSON(handler.getDataProperties());
//    	JSONHandler.createObjectPropertiesJSON(handler.getObjectProperties());
    	JSONHandler.createBranchesJSON(gh.getRemoteBranches());
    }
    
}