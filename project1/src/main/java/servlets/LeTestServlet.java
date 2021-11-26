package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Servlet implementation class LeTestServlet2
 */
public class LeTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LeTestServlet() {
        super();
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a = req.getParameter("uname");
		String b = req.getParameter("pwd");
		
		RequestDispatcher view = req.getRequestDispatcher("test.jsp");
        view.forward(req, resp);
        System.out.println("GET: " + a + ", " + b);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String a = (String)req.getParameter("uname");
		String b = (String)req.getParameter("psw");
		
//		resp.sendRedirect("test.jsp");
		RequestDispatcher view = req.getRequestDispatcher("test.jsp");
		view.forward(req, resp);
		System.out.println("POST: " + a + ", " + b);
	}

}
