package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jgit.lib.Ref;

import logic.GitHandler;
import logic.JSONHandler;

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
		String branchName = request.getParameter("branch");
		String email = branchName.substring(0, branchName.lastIndexOf("_"));
		String comment = request.getParameter("comment");
		String decision = request.getParameter("decision");
		
		GitHandler git = GitHandler.getDefault();
		String emailTitle = null;
		if("Accept".equals(decision)) {
			for(Ref b: git.getAllBranches()) {
				git.checkoutBranch(b.getName().replace("refs/remotes/origin/", ""));
				git.mergeBranch(branchName);
				git.publishBranch(b.getName().replace("refs/remotes/origin/", ""));
			}
			git.checkoutBranch("master");
			git.deleteBranch(branchName);
			emailTitle = "Proposal Accepted";
		}else if("Decline".equals(decision)) {
			git.deleteBranch(branchName);
			emailTitle = "Proposal Declined";
		}
		
//		EmailHandler.sendMail(email, emailTitle, comment);
		
		RequestDispatcher view = request.getRequestDispatcher("curator.jsp");
		view.forward(request, response);
	}

}
