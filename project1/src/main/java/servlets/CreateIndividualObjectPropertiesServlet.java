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
 * Servlet implementation class CreateIndividualObjectPropertiesServlet
 */
public class CreateIndividualObjectPropertiesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateIndividualObjectPropertiesServlet() {
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
		String firstIndividualName = (String)request.getParameter("individual-name");
		String opName = (String)request.getParameter("object-property-input");
		String secondIndividualName = (String)request.getParameter("object-property-value");
		String email = (String)request.getParameter("email-input");
		GitHandler git = GitHandler.getDefault();
		String branchName = git.getNextBranchName(email);
		git.changeBranch("master");
		git.createAndChangeBranch(branchName);
		OWLHandler owl = git.getOWLHandler();
		System.out.println(opName + " " + firstIndividualName + " " +  secondIndividualName);
		owl.declareObjectPropertyAssertion(opName, firstIndividualName, secondIndividualName);
		git.commitAndPush(email + " has created a new object property!", branchName);
		git.changeBranch("master");
	}

}
