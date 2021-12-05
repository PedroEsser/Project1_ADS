package logic;

import java.io.FileNotFoundException;

public class App {

	public static void main(String[] args) throws FileNotFoundException {
		//CuratorHandler.subscribeCurator("test@mail.com", "joemama69");
		System.out.println(CuratorHandler.authenticateCurator("test@mail.com", "joemama69"));
		System.out.println(CuratorHandler.getCuratorMails());
//    	GitHandler gh = new GitHandler("C:\\Users\\amend\\GitHub\\Knowledge_Base\\.git");
//    	gh.test(gh.getNextBranchName("b"));
//    	OWLHandler handler = new OWLHandler("C:\\Users\\amend\\GitHub\\Knowledge_Base\\ontology.owl");
//    	System.out.println(handler.getIndividuals());
//    	JSONHandler.createTaxonomyJSON(handler.getTaxonomy());
//    	JSONHandler.createIndividualsJSON(handler.getIndividualsClasses(), handler.getIndividualsDataProperties(), handler.getIndividualsObjectProperties());
//    	JSONHandler.createDataPropertiesJSON(handler.getDataProperties());
//    	JSONHandler.createObjectPropertiesJSON(handler.getObjectProperties());
//    	JSONHandler.createBranchesJSON(gh.getRemoteBranches());
//		EmailHandler.sendGmail("username@gmail.com", "Mail Subject", "Mail Text");
	}
}