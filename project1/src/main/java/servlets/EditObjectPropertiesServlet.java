package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logic.GitHandler;
import logic.OWLHandler;

/**
 * Servlet implementation class EditObjectPropertiesServlet
 */
public class EditObjectPropertiesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditObjectPropertiesServlet() {
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
		String opName = (String)request.getParameter("old-object-property-input");
		String newOPName = (String)request.getParameter("new-object-property-input");
		String email = (String)request.getParameter("email-input");
		GitHandler git = GitHandler.getDefault();
		String branchName = git.getNextBranchName(email);
		git.changeBranch("master");
		git.createAndChangeBranch(branchName);
		OWLHandler owl = git.getOWLHandler();
		if(!newOPName.equals(""))
			owl.changeObjectProperty(opName, newOPName);
		else
			newOPName = opName;
		owl.deleteObjectPropertyAxioms(newOPName);
		if(request.getParameter("functional") != null)
			owl.declareObjectPropertyAxiom(newOPName, "functional");
		if(request.getParameter("inverse-functional") != null)
			owl.declareObjectPropertyAxiom(newOPName, "inverse-functional");
		if(request.getParameter("transitive")!= null)
			owl.declareObjectPropertyAxiom(newOPName, "transitive");
		if(request.getParameter("symmetric") != null)	
			owl.declareObjectPropertyAxiom(newOPName, "symmetric");
		if(request.getParameter("asymmetric") != null)
			owl.declareObjectPropertyAxiom(newOPName, "asymmetric");
		if(request.getParameter("reflexive")!= null)
			owl.declareObjectPropertyAxiom(newOPName, "reflexive");
		if(request.getParameter("irreflexive") != null)
			owl.declareObjectPropertyAxiom(newOPName, "irreflexive");
		git.commitAndPush(email + " has edited an object property!", branchName);
		git.changeBranch("master");
		//TODO send email to curator
	}

}
