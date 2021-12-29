package logic;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.swrlapi.sqwrl.values.SQWRLResultValue;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONHandler {
	
	public static void updateJSONs() {
		GitHandler git = GitHandler.getDefault();
		OWLHandler owl = git.getOWLHandler();
		JSONHandler.createBranchesJSON(git.getAllBranchesCommitDiff());
    	JSONHandler.createIndividualsJSON(owl.getIndividualsClasses(), owl.getIndividualsDataProperties(), owl.getIndividualsObjectProperties());
    	JSONHandler.createDataPropertiesJSON(owl.getDataProperties());
    	JSONHandler.createObjectPropertiesJSON(owl.getObjectPropertiesCharacteristics());
    	JSONHandler.createTaxonomyJSON(owl.getTaxonomy());
	}
	
	public static String convertJSONToString(String file) {
		try {
			return new String(Files.readAllBytes(Paths.get(file)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static JSONObject convertStringToJSON(String json) {
		return new JSONObject(json);
	}
	
	//creates json with all the branches and their respective diff
	public static void createBranchesJSON(HashMap<String, String> branchesCommitDiff) {
		try {
			FileWriter file = new FileWriter("src/main/webapp/resources/branches.json");
			JSONArray array = new JSONArray();
			branchesCommitDiff.forEach((branch, diff) -> {
				JSONObject object = new JSONObject();
				setOrderedJSONOBject(object);
				object.put("branch", branch);
				object.put("diff", diff);
				array.put(object);
			});
			file.write(array.toString(1));
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//creates json with all the individuals and their respective class, data properties, and object properties
	public static void createIndividualsJSON(HashMap<OWLNamedIndividual, Set<OWLClassAssertionAxiom>> individualsClasses, 
			HashMap<OWLNamedIndividual, Set<OWLDataPropertyAssertionAxiom>> individualsDataProperties, 
			HashMap<OWLNamedIndividual, Set<OWLObjectPropertyAssertionAxiom>> individualsObjectProperties) {
		try {
			FileWriter file = new FileWriter("src/main/webapp/resources/individuals.json");
			JSONArray array = new JSONArray();
			Set<OWLNamedIndividual> individuals = individualsClasses.keySet();
			for(OWLNamedIndividual individual : individuals) {
				JSONObject object = new JSONObject();
				setOrderedJSONOBject(object);
				object.put("individual", individual.getIRI().getShortForm());
				for(OWLClassAssertionAxiom classAssertion : individualsClasses.get(individual))
					for(OWLClass c : classAssertion.getClassesInSignature())
						object.put("class", c.getIRI().getShortForm());
				JSONArray dataProperties = new JSONArray();
				for(OWLDataPropertyAssertionAxiom dataPropertyAssertion : individualsDataProperties.get(individual)) {
					JSONArray aux = new JSONArray();
					aux.put(dataPropertyAssertion.getProperty().asOWLDataProperty().getIRI().getShortForm());
					aux.put(dataPropertyAssertion.getObject().getLiteral());
					dataProperties.put(aux);
				}
				object.put("data properties", dataProperties);
				JSONArray objectProperties = new JSONArray();
				for(OWLObjectPropertyAssertionAxiom objectPropertyAssertion : individualsObjectProperties.get(individual)) {
					JSONArray aux = new JSONArray();
					aux.put(objectPropertyAssertion.getProperty().asOWLObjectProperty().getIRI().getShortForm());
					aux.put(objectPropertyAssertion.getObject().asOWLNamedIndividual().getIRI().getShortForm());
					objectProperties.put(aux);
				}
				object.put("object properties", objectProperties);
				array.put(object);
			}
			file.write(array.toString(1));
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//creates json with all the data properties
	public static void createDataPropertiesJSON(Set<OWLDataProperty> dataProperties) {
		try {
			FileWriter file = new FileWriter("src/main/webapp/resources/data_properties.json");
			JSONArray array = new JSONArray();
			for(OWLDataProperty property: dataProperties) {
				JSONObject object = new JSONObject();
				setOrderedJSONOBject(object);
				object.put("data property", property.getIRI().getShortForm());
				array.put(object);
			}
			file.write(array.toString(1));
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//creates json with all the object properties and their respective characteristics
	public static void createObjectPropertiesJSON(HashMap<OWLObjectProperty, Set<String>> objectPropertiesCharacteristics) {
		try {
			FileWriter file = new FileWriter("src/main/webapp/resources/object_properties.json");
			JSONArray array = new JSONArray();
			for(OWLObjectProperty property: objectPropertiesCharacteristics.keySet()) {
				JSONObject object = new JSONObject();
				setOrderedJSONOBject(object);
				object.put("object property", property.getIRI().getShortForm());
				JSONArray axioms = new JSONArray();
				for(String characteristic: objectPropertiesCharacteristics.get(property)) {
					axioms.put(characteristic);
				}
				object.put("characteristics", axioms);
				array.put(object);
			}
			file.write(array.toString(1));
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//creates json with all the classes and their respective subclasses (taxonomy)
	public static void createTaxonomyJSON(LinkedHashMap<OWLClass, ArrayList<OWLClass>> map) {
		try {
			FileWriter file = new FileWriter("src/main/webapp/resources/taxonomy.json");
			JSONArray array = new JSONArray();
			for(Entry<OWLClass, ArrayList<OWLClass>> entry : map.entrySet()) {
				JSONObject object = new JSONObject();
				setOrderedJSONOBject(object);
				object.put("class", entry.getKey().getIRI().getShortForm());
				if(!entry.getValue().isEmpty()) {
					JSONArray children = new JSONArray();
					for(OWLClass subclass : entry.getValue())
						children.put(new JSONObject().put("class", subclass.getIRI().getShortForm()));
					object.put("_children", children);
				}
				array.put(object);
			}
			fixJSONArrayComposition(array);
			file.write(array.toString(1));
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static JSONArray createQueryResultJSON(List<List<String>> results, List<String> labels) {
		JSONArray array = new JSONArray();
		for(List<String> data : results) {
			JSONObject object = new JSONObject();
			setOrderedJSONOBject(object);
			if(!data.isEmpty()) {
				int i = 0;
				for(String value : data)
					object.put(labels.get(i++), value);
			}
			array.put(object);
		}
		return array;

	}
	
	private static void fixJSONArrayComposition(JSONArray array) {
		ArrayList<Integer> index = new ArrayList<>();
		for(int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			for(int j = 0; j < array.length(); j++) {
				if(findAndReplaceJSONObject(array, object, j)) {
					index.add(i);
					break;
				}
			}
		}
		index.sort(Comparator.reverseOrder());
		for(int i : index) {
			array.remove(i);
		}
	}
	
	private static boolean findAndReplaceJSONObject(JSONArray array, JSONObject o1, int i) {
		JSONObject o2 = array.getJSONObject(i);
		if(o1.getString("class").equals(o2.getString("class")) && !o1.equals(o2)) {
			array.remove(i);
			array.put(o1);
			return true;
		} else if(o2.has("_children")) {
			JSONArray c = o2.getJSONArray("_children");
			for(int j = 0; j < c.length(); j++) {
				if(findAndReplaceJSONObject(o2.getJSONArray("_children"), o1, j)) 
					return true;
			}
		}
		return false;
	}
	
	private static void setOrderedJSONOBject(JSONObject object) {
		try {
			Field changeMap = object.getClass().getDeclaredField("map");
			changeMap.setAccessible(true);
			changeMap.set(object, new LinkedHashMap<>());
			changeMap.setAccessible(false);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
}
