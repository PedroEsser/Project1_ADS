package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logic.OWLHandler;
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
		OWLHandler owl = new OWLHandler("C:\\Users\\Utilizador\\Documents\\GitHub\\Knowledge_Base\\ontology.owl");
		GitHandler git = new GitHandler("C:\\Users\\Utilizador\\Documents\\GitHub\\Knowledge_Base\\.git");
		String branchName = git.getNextBranchName(email);
		git.createBranch(branchName);
		git.changeBranch(branchName);
		//owl.declareOWLEntity(EntityType.CLASS, className);
		git.commit(email + " has created a new class!");
		git.push("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");
		//TODO send email to curator
		git.changeBranch("master");
		response.sendRedirect("taxonomy.jsp");
		System.out.println(parentClass + ", " + className + ", " + email);
	}

}
