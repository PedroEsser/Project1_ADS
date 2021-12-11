package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jgit.lib.Ref;

import logic.EmailHandler;
import logic.GitHandler;

/**
 * Servlet implementation class CuratorDecisionServlet
 */
public class CuratorDecisionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CuratorDecisionServlet() {
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
		GitHandler git = GitHandler.getDefault();
		String branchName = request.getParameter("branch");
		String decision = request.getParameter("decision");
		String email = branchName.substring(0, branchName.lastIndexOf("_"));
		String emailTitle = null;
		if("Accept".equals(decision)) {
			git.checkoutBranch("master");
			git.mergeBranch(branchName);
			git.publishBranch("master");
			git.deleteBranch(branchName);
			emailTitle = "Proposal Accepted";
		}else if("Decline".equals(decision)) {
			git.deleteBranch(branchName);
			emailTitle = "Proposal Declined";
		}
		String comment = request.getParameter("comment");
		EmailHandler.sendMail(email, emailTitle, comment);
		RequestDispatcher view = request.getRequestDispatcher("curator.jsp");
		view.forward(request, response);
	}

}
