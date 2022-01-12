package servlets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.SQWRLResultValue;

import logic.GitHandler;
import logic.OWLHandler;
// Algorithm(?alg) ^ maxObjectivesAlgorithmIsAbleToDealWith(?alg,?max) ^ swrlb:greaterThanOrEqual(?max,2) minObjectivesAlgorithmIsAbleToDealWith(?alg,?min) ^ swrlb:lessThanOrEqual(?min,2) -> sqwrl:select(?alg, ?max, ?min) ^ sqwrl:orderBy(?alg)
/**
 * Servlet implementation class ResultVisualizationServlet
 */
public class ResultVisualizationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResultVisualizationServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = (String)request.getParameter("result");
		GitHandler git = GitHandler.getDefault();
		OWLHandler owl = git.getOWLHandler();
		File file = new File("src/main/webapp/resources/temp.owl");
		writePrefixs(owl.getPrefixs(), owl.getDefaultPrefix());
		try {
			OWLHandler tempOwl = new OWLHandler(file);
			writePropertiesDeclarations(owl, tempOwl, query);
			writeQueryResults(query, owl, tempOwl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finished");
	}

	private void writePrefixs(HashMap<String, String> prefixs, String defaultPrefix) {
		try {
			FileWriter writer = new FileWriter("src/main/webapp/resources/temp.owl");
			for(String name: prefixs.keySet()) {
				writer.write("Prefix(" + name + "=<" + prefixs.get(name) + ">)\n");
			}
			writer.write("\n\nOntology(<" + defaultPrefix + ">\n\n)");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeQueryResults(String query, OWLHandler owl, OWLHandler tempOwl) {
		SQWRLResult results = owl.getQueryResult(query);
		try {
			ArrayList<String> types = new ArrayList<>();
			ArrayList<String> columnNames = new ArrayList<>();
			HashMap<Integer, String> dpNames = new HashMap<>();
			HashMap<Integer, Integer> dpIndividualIndex = new HashMap<>();
			HashMap<String, ArrayList<Integer>> objectProperties = new HashMap<>();
			for(String s: results.getColumnNames())
				columnNames.add(s);
			objectProperties = getObjectPropertiesIndex(owl, query, columnNames);
			if(!objectProperties.keySet().isEmpty()) {
				for(String opName: objectProperties.keySet()) {
					tempOwl.declareOWLEntity(EntityType.OBJECT_PROPERTY, opName);
				}
			}
			results.next();
			for(SQWRLResultValue value: results.getRow()) 
				types.add(getValueType(value));
			ArrayList<Integer> order = getIndexOrder(types);
			do{
				for(int index: order) {
					if(!writeEntity(results.getRow().get(index).toString(), types.get(index), tempOwl)) {
						if(!dpNames.containsKey(index)) {
							dpNames.put(index, getDPName(query, columnNames.get(index)));
							String individualColumnName = getDPIndividual(query, dpNames.get(index), columnNames.get(index));
							System.out.println(individualColumnName);
							dpIndividualIndex.put(index, columnNames.indexOf(individualColumnName.replace("?", "")));
						}
						declareDPAssertion(tempOwl, dpNames.get(index), results.getRow().get(dpIndividualIndex.get(index)).toString(), results.getRow().get(index).toString());
					}
					if(!objectProperties.keySet().isEmpty()) 
						for(String opName: objectProperties.keySet()) {
							String ind1 = results.getRow().get( objectProperties.get(opName).get(0)).toString();
							String ind2 = results.getRow().get( objectProperties.get(opName).get(1)).toString();
							tempOwl.declareObjectPropertyAssertion(opName, ind1, ind2);
						}
				}
			}while(results.next()); 
		} catch (SQWRLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean writeEntity(String value, String type, OWLHandler tempOwl ) {
		if(type.equals("Class")) {
			tempOwl.declareOWLEntity(EntityType.CLASS, value);
		} else if(type.equals("Individual")) {
			tempOwl.declareOWLEntity(EntityType.NAMED_INDIVIDUAL, value);
		}else if(type.equals("DataProperty")) {
			tempOwl.declareOWLEntity(EntityType.DATA_PROPERTY, value);
		}else if(type.equals("ObjectProperty")) {
			tempOwl.declareOWLEntity(EntityType.OBJECT_PROPERTY, value);
		}else {
			return false;
		}
		return true;
	}
	
	private String getValueType(SQWRLResultValue value) {
		if(value.isClass()) {
			return "Class";
		} else if(value.isIndividual()) {
			return "Individual";
		}else if(value.isDataProperty()) {
			return "DataProperty";
		}else if(value.isObjectProperty()) {
			return "ObjectProperty";
		}
		return "";
	}
	
	private ArrayList<Integer> getIndexOrder(ArrayList<String> list) {
		ArrayList<Integer> indexList = new ArrayList<>();
		String[] array = {"Class", "Individual", "DataProperty","ObjectProperty", ""};
		for(String type: array)
			for(int i=0 ; i < list.size() ; i++)
				if(type.equals(list.get(i)))
					indexList.add(i);
		return indexList;
	}
	
	private ArrayList<String> getQueryRules(String query) {
		String[] querieParts = query.split("->");
		String ands = querieParts[0];
		ArrayList<String> literals = new ArrayList<>();
		for(String s: ands.split(" . ")) 
			if(!s.contains("swrlb"))
				literals.add(s.trim());
		return literals;
	}
	
	private ArrayList<String> getQuerySelects(String query) {
		String[] querieParts = query.split("->");
		String select = querieParts[1];
		ArrayList<String> selects = new ArrayList<>();
		for(String s: select.split(" . ")) 
			if(s.contains("select")) 
				selects = getArguments(s);
		return selects;
	}

	private ArrayList<String> getArguments(String string) {
		ArrayList<String> args = new ArrayList<>();
		String aux = string.substring(string.indexOf("(")+1);
		for(String s1: aux.substring(0,aux.indexOf(")")).split(","))
			args.add(s1.trim());
		return args;
	}
	
	private String getName(String string) {
		return string.substring(0,string.indexOf("("));
	}
	
	private void writePropertiesDeclarations(OWLHandler owl, OWLHandler tempOWL, String query) {
		ArrayList<String> rules = getQueryRules(query);
		ArrayList<String> select = getQuerySelects(query);
		createDPDeclaration(owl, tempOWL, rules, select);
		createOPDeclaration(owl, tempOWL, rules, select);
	}
	
	private void createDPDeclaration(OWLHandler owl, OWLHandler tempOWL, ArrayList<String> querys, ArrayList<String> select) {
		for(String literals: querys) 
			if(containsAll(literals, select))
				for(OWLDataProperty property: owl.getDataProperties()) 
					if(getName(literals).equals(property.getIRI().getShortForm())) 
						tempOWL.declareOWLEntity(EntityType.DATA_PROPERTY, property.getIRI().getShortForm());
	}
	
	private void createOPDeclaration(OWLHandler owl, OWLHandler tempOWL, ArrayList<String> querys, ArrayList<String> select) {
		for(String literals: querys) 
			if(containsAll(literals, select))
				for(OWLObjectProperty property: owl.getObjectProperties()) 
					if(getName(literals).equals(property.getIRI().getShortForm())) 
						tempOWL.declareOWLEntity(EntityType.OBJECT_PROPERTY, property.getIRI().getShortForm());
	}
	
	private boolean containsAll(String string, ArrayList<String> select) {
		for(String args: getArguments(string))
			if(!select.contains(args))
				return false;
		return true;
	}
	
	private String getDPName(String query, String name) {
		ArrayList<String> rules = getQueryRules(query);
		for(String rule: rules)
			for(String arg: getArguments(rule))
				if(arg.equals("?"+name))
					return getName(rule);
		return "";
	}
	
	private String getDPIndividual(String query, String dataPname, String argName) {
		ArrayList<String> rules = getQueryRules(query);
		for(String rule: rules)
			if(getName(rule).equals(dataPname))
				for(String arg: getArguments(rule))
					if(!arg.equals(argName))
						return arg;
		return "";
	}
	
	private void declareDPAssertion(OWLHandler tempOwl, String dpName, String individualName, String value) {
		String[] values = value.replaceFirst("\"","").split("\"");
		value = values[0];
		if(values[1].contains("integer"))
			tempOwl.declareDataPropertyAssertion(individualName, dpName, Integer.parseInt(value));
		else if(values[1].contains("float"))
			tempOwl.declareDataPropertyAssertion(individualName, dpName, Float.parseFloat(value));
		else if(values[1].contains("boolean"))
			tempOwl.declareDataPropertyAssertion(individualName, dpName, value.equals("true"));
		else if(values[1].contains("string"))
			tempOwl.declareDataPropertyAssertion(individualName, dpName, value);
	}
	
	private HashMap<String, ArrayList<Integer>> getObjectPropertiesIndex(OWLHandler owl, String query, ArrayList<String> columnNames) {
		HashMap<String, ArrayList<Integer>> op = new HashMap<>();
		ArrayList<String> args = getQuerySelects(query);
		for(String rule: getQueryRules(query))
			if(containsAll(rule, args))
				for(OWLObjectProperty property: owl.getObjectProperties()) 
					if(getName(rule).equals(property.getIRI().getShortForm())) {
						ArrayList<Integer> indexs = new ArrayList<>();
						for(String arg: getArguments(rule))
							indexs.add(columnNames.indexOf(arg.replace("?", "")));
						op.put(getName(rule), indexs);
					}
		return op;
	}
}