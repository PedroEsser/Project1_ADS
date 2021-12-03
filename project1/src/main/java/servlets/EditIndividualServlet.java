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
 * Servlet implementation class EditIndividualServlet
 */
public class EditIndividualServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditIndividualServlet() {
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
		String oldIndividualName = (String)request.getParameter("old-individual-input");
		String newIndividualName = (String)request.getParameter("new-individual-input");
		String email = (String)request.getParameter("email-input");
		GitHandler git = new GitHandler("C:\\Users\\pedro\\git\\Knowledge_Base\\.git");
		OWLHandler owl = git.getOWLHandler();
		String branchName = git.getNextBranchName(email);
		git.changeBranch("master");
		git.createAndChangeBranch(branchName);
		owl.changeNamedIndividual(oldIndividualName, newIndividualName);
		git.commitAndPush(email + " has edited an individual!", branchName);
		git.changeBranch("master");
		//TODO send email to curator
		RequestDispatcher view = request.getRequestDispatcher("taxonomy.jsp");
        view.forward(request, response);
	}

}
