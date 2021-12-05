package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.semanticweb.owlapi.model.EntityType;

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
		GitHandler git = new GitHandler("C:\\Users\\Utilizador\\Documents\\GitHub\\Knowledge_Base\\.git");
		String branchName = git.getNextBranchName(email);
		git.changeBranch("master");
		git.createAndChangeBranch(branchName);
		OWLHandler owl = git.getOWLHandler();
		owl.declareOWLEntity(EntityType.OBJECT_PROPERTY, opName);
		git.commitAndPush(email + " has created a new object property!", branchName);
		git.changeBranch("master");
		//TODO send email to curator
	}

}
