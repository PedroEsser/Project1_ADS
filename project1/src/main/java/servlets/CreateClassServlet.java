package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.semanticweb.owlapi.model.EntityType;

import logic.OWLHandler;
import logic.CuratorHandler;
import logic.EmailHandler;
import logic.GitHandler;


/**
 * Servlet implementation class CreateClassServlet
 */
public class CreateClassServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateClassServlet() {
        super();
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
		String parentClass = (String)request.getParameter("super-class-input");
		String className = (String)request.getParameter("class-input");
		String email = (String)request.getParameter("email-input");
		GitHandler git = GitHandler.getDefault();
		String branchName = git.getNextBranchName(email);
		git.changeBranch("master");
		git.createAndChangeBranch(branchName);
		OWLHandler owl = git.getOWLHandler();
		owl.declareOWLEntity(EntityType.CLASS, className);
		if(!parentClass.isEmpty())
			owl.declareSubClassOf(parentClass, className);
		git.commitAndPush(email + " has created a new class!", branchName);
		git.changeBranch("master");
		//TODO send email to curator
		CuratorHandler.sendMailToCurators("New proposal received", "Dear Curator \n\nA proposal for a new class by the name of '" + className + "' has been submited.");
		EmailHandler.sendMail(email, "Proposal Received", "Dear user \n\nYour proposal has been received, thanks for the suggestion!");
		System.out.println(email);
	}
}
