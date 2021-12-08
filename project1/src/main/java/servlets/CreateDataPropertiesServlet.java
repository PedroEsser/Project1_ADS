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
 * Servlet implementation class CreateDataPropertiesServlet
 */
public class CreateDataPropertiesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateDataPropertiesServlet() {
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
		String dpName = (String)request.getParameter("data-property-input");
		String email = (String)request.getParameter("email-input");
		GitHandler git = GitHandler.getDefault();
		String branchName = git.getNextBranchName(email);
		git.checkoutBranch("master");
		git.createAndCheckoutBranch(branchName);
		OWLHandler owl = git.getOWLHandler();
		owl.declareOWLEntity(EntityType.DATA_PROPERTY, dpName);
		git.commitAndPush(email + " has created a new data property!", branchName);
		git.checkoutBranch("master");
		CuratorHandler.sendMailToCurators("New proposal received", "Dear Curator \n\nA proposal for a new data property by the name of '" + dpName + "' has been submited.");
		EmailHandler.sendMail(email, "Proposal Received", "Dear user \n\nYour proposal has been received, thanks for the suggestion!");
	}

}
