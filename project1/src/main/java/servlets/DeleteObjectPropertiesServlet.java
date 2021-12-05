package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logic.GitHandler;
import logic.OWLHandler;

/**
 * Servlet implementation class DeleteObjectPropertiesServlet
 */
public class DeleteObjectPropertiesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteObjectPropertiesServlet() {
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
		owl.deleteObjectProperty(opName);
		git.commitAndPush(email + " has deletede an object property!", branchName);
		git.changeBranch("master");
		//TODO send email to curator
	}

}