package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.semanticweb.owlapi.model.EntityType;

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
		OWLHandler owl = new OWLHandler("C:\\Users\\Utilizador\\Documents\\GitHub\\Knowledge_Base\\ontology.owl");
		GitHandler git = new GitHandler("C:\\Users\\Utilizador\\Documents\\GitHub\\Knowledge_Base\\.git");
		String branchName = git.getNextBranchName(email);
		git.changeBranch("master");
		git.createAndChangeBranch(branchName);
		owl.declareOWLEntity(EntityType.NAMED_INDIVIDUAL, individualName);
		owl.declareClassAssertion(className, individualName);
		git.commitAndPush(email + " has created a new Individual!", branchName);
		git.changeBranch("master");
		//TODO send email to curator
		RequestDispatcher view = request.getRequestDispatcher("taxonomy.jsp");
        view.forward(request, response);
	}

}
