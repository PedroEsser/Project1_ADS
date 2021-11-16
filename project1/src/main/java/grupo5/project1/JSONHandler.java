package grupo5.project1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLClass;

import java.lang.reflect.Field;

public class JSONHandler {

	public static void createTaxonomyJSON(LinkedHashMap<OWLClass, ArrayList<OWLClass>> map) {
		try {
			FileWriter file = new FileWriter("./taxonomy.json");
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
