package logic;

public class App 
{
    public static void main( String[] args )
    {
//    	GitHandler gh = new GitHandler("C:\\Users\\pedro\\git\\Knowledge_Base\\.git");
//    	
//    	gh.test("Test_Branch");
    	
    	OWLHandler handler = new OWLHandler("animal.owl");
    	JSONHandler.createTaxonomyJSON(handler.getTaxonomy());
    }
    
}