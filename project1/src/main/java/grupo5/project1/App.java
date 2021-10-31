package grupo5.project1;

public class App 
{
    public static void main( String[] args )
    {
    	GitHandler gh = new GitHandler("C:\\Users\\pedro\\git\\SID\\.git");
    	
    	gh.test("Test_Branch");
    	//OWLUtils.leOWLTest();
    }
    
}