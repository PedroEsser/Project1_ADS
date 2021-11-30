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
 * Servlet implementation class EditClassServlet
 */
public class EditClassServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditClassServlet() {
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
		String oldClass = (String)request.getParameter("old-class-input");
		String newClass = (String)request.getParameter("new-class-input");
		String email = (String)request.getParameter("email-input");
		OWLHandler owl = new OWLHandler("C:\\Users\\Utilizador\\Documents\\GitHub\\Knowledge_Base\\ontology.owl");
		GitHandler git = new GitHandler("C:\\Users\\Utilizador\\Documents\\GitHub\\Knowledge_Base\\.git");
		String branchName = git.getNextBranchName(email);
		git.createAndChangeBranch(branchName);
		owl.changeClass(oldClass, newClass);
		git.commitAndPush(email + " has change the class name!", "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");
		//TODO send email to curator
		response.sendRedirect("taxonomy.jsp");
	}

}
