package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logic.CuratorHandler;
import logic.EmailHandler;
import logic.GitHandler;

/**
 * Servlet implementation class CuratorDecisionServlet
 */
public class CuratorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CuratorServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("login.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(CuratorHandler.authenticateCurator(request.getParameter("email"), request.getParameter("password"))) {
			if(request.getParameter("decision") == null && request.getServletPath().equals("/curator")) {
				RequestDispatcher view = request.getRequestDispatcher("curator.jsp");
				view.forward(request, response);
			} else if(request.getServletPath().equals("/curator")) {
				GitHandler handler = GitHandler.getDefault();
				String branchName = request.getParameter("branch");
				String decision = request.getParameter("decision");
				String email = branchName.substring(0, branchName.lastIndexOf("_"));
				String emailTitle = null;
				String result = null;
				if(decision.equals("Accept")) {
					handler.checkoutBranch("master");
					result = handler.mergeBranch(branchName);
					if(result != null) {
						handler.add();
						handler.commit("Conflict when merging branches: " + result);
					}
					handler.publishBranch("master");
					handler.deleteBranch(branchName);
					emailTitle = "Proposal Accepted";
				} else if(decision.equals("Decline")) {
					handler.deleteBranch(branchName);
					emailTitle = "Proposal Declined";
				}
				String comment = request.getParameter("comment");
				EmailHandler.sendMail(email, emailTitle, comment);
				if(result != null) {
					RequestDispatcher view = request.getRequestDispatcher("conflict.jsp");
					view.forward(request, response);
				} else {
					RequestDispatcher view = request.getRequestDispatcher("curator.jsp");
					view.forward(request, response);
				}
			} else if(request.getServletPath().equals("/conflict")) {
				String file = request.getParameter("file");
				GitHandler handler = GitHandler.getDefault();
				handler.writeOntologyFile(file);
				handler.commitAndPush("Conflict resolved!", "master");
				RequestDispatcher view = request.getRequestDispatcher("curator.jsp");
				view.forward(request, response);
			}
		} else {
			response.sendRedirect("login.jsp");
		}
	}

}
