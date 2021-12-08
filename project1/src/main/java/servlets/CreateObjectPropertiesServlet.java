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
 * Servlet implementation class CreateObjectPropertiesServlet
 */
public class CreateObjectPropertiesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateObjectPropertiesServlet() {
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
		String opName = (String)request.getParameter("object-property-input");
		String email = (String)request.getParameter("email-input");
		String functional = (String)request.getParameter("functional");
		GitHandler git = GitHandler.getDefault();
		String branchName = git.getNextBranchName(email);
		git.checkoutBranch("master");
		git.createAndCheckoutBranch(branchName);
		OWLHandler owl = git.getOWLHandler();
		owl.declareOWLEntity(EntityType.OBJECT_PROPERTY, opName);
		if(request.getParameter("functional") != null)
			owl.declareObjectPropertyAxiom(opName, "functional");
		if(request.getParameter("inverse-functional") != null)
			owl.declareObjectPropertyAxiom(opName, "inverse-functional");
		if(request.getParameter("transitive")!= null)
			owl.declareObjectPropertyAxiom(opName, "transitive");
		if(request.getParameter("symmetric") != null)	
			owl.declareObjectPropertyAxiom(opName, "symmetric");
		if(request.getParameter("asymmetric") != null)
			owl.declareObjectPropertyAxiom(opName, "asymmetric");
		if(request.getParameter("reflexive")!= null)
			owl.declareObjectPropertyAxiom(opName, "reflexive");
		if(request.getParameter("irreflexive") != null)
			owl.declareObjectPropertyAxiom(opName, "irreflexive");
		git.commitAndPush(email + " has created a new object property!", branchName);
		git.checkoutBranch("master");
		CuratorHandler.sendMailToCurators("New proposal received", "Dear Curator \n\nA proposal for a new data property by the name of '" + opName + "' has been submited.");
		EmailHandler.sendMail(email, "Proposal Received", "Dear user \n\nYour proposal has been received, thanks for the suggestion!");
	}

}
