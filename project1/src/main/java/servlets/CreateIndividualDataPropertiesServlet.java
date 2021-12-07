package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logic.GitHandler;
import logic.OWLHandler;

/**
 * Servlet implementation class CreateIndividualDataPropertiesServlet
 */
public class CreateIndividualDataPropertiesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateIndividualDataPropertiesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String individualName = "";
		String dpName = (String)request.getParameter("object-property-input");
		String value = (String)request.getParameter("data-property-value");
		String email = (String)request.getParameter("email-input");
		GitHandler git = GitHandler.getDefault();
		String branchName = git.getNextBranchName(email);
		git.changeBranch("master");
		git.createAndChangeBranch(branchName);
		OWLHandler owl = git.getOWLHandler();
		if(value.equals("true") || value.equals("false"))
			owl.declareDataPropertyAssertion(individualName, dpName, value.equals("true"));
		else if(isInteger(value))
			owl.declareDataPropertyAssertion(individualName, dpName, Integer.parseInt(value));
		else if(isDouble(value))
			owl.declareDataPropertyAssertion(individualName, dpName, Double.parseDouble(value));
		else
			owl.declareDataPropertyAssertion(individualName, dpName, value);
		git.commitAndPush(email + " has created a new object property!", branchName);
		git.changeBranch("master");
	}

	private static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
	
	private static boolean isDouble(String s) {
	    try { 
	        Double.parseDouble(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
}
