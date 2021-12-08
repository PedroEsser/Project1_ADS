package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.semanticweb.owlapi.model.EntityType;

import logic.CuratorHandler;
import logic.EmailHandler;
import logic.GitHandler;
import logic.OWLHandler;

/**
 * Servlet implementation class CreateIndividualServlet
 */
public class CreateIndividualServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateIndividualServlet() {
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
		String individualName = (String)request.getParameter("individual-input");
		String className = (String)request.getParameter("class-input");
		String email = (String)request.getParameter("email-input");
		GitHandler git = GitHandler.getDefault();
		String branchName = git.getNextBranchName(email);
		git.checkoutBranch("master");
		git.createAndCheckoutBranch(branchName);
		OWLHandler owl = git.getOWLHandler();
		owl.declareOWLEntity(EntityType.NAMED_INDIVIDUAL, individualName);
		owl.declareClassAssertion(className, individualName);
		git.commitAndPush(email + " has created a new Individual!", branchName);
		git.checkoutBranch("master");
		CuratorHandler.sendMailToCurators("New proposal received", "Dear Curator \n\nA proposal for a new individual by the name of '" + individualName + "' of the class '" + className + "' has been submited.");
		EmailHandler.sendMail(email, "Proposal Received", "Dear user \n\nYour proposal has been received, thanks for the suggestion!");
	}

}
