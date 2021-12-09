package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logic.CuratorHandler;
import logic.EmailHandler;
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
		String individualName = (String)request.getParameter("individual-name");
		String dpName = (String)request.getParameter("data-property-input");
		String value = (String)request.getParameter("data-property-value");
		String email = (String)request.getParameter("email-input");
		GitHandler git = GitHandler.getDefault();
		String branchName = git.getNextBranchName(email);
		git.checkoutBranch("master");
		git.createAndCheckoutBranch(branchName);
		OWLHandler owl = git.getOWLHandler();
		if(owl.hasDeclaredDataPropertyAssertion(individualName, dpName)) {
			owl.deleteDataPropertyOfIndividual(individualName, dpName);
		}
		if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
			owl.declareDataPropertyAssertion(individualName, dpName, value.equalsIgnoreCase("true"));
		else if(isInteger(value))
			owl.declareDataPropertyAssertion(individualName, dpName, Integer.parseInt(value));
		else if(isDouble(value))
			owl.declareDataPropertyAssertion(individualName, dpName, Double.parseDouble(value));
		else
			owl.declareDataPropertyAssertion(individualName, dpName, value);
		
		git.commitAndPush(email + " has created a new individual data property!", branchName);
		git.checkoutBranch("master");
		CuratorHandler.sendMailToCurators("New proposal received", "Dear Curator \n\nA proposal for a new individual data property by the name of '" + dpName + "' for the individual '" + individualName + "' has been submited.");
		EmailHandler.sendMail(email, "Proposal Received", "Dear user \n\nYour proposal has been received, thanks for the suggestion!");
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
