package grupo5.project1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.vocabulary.OWL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplBoolean;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplDouble;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplFloat;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplInteger;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString;

public class OWLHandler {

	private File owlFile;
	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private OWLDataFactory factory;
	private String defaultprefix;
	private String datatypePrefix;

	public OWLHandler(String file) {
		owlFile = new File(file);
		manager = OWLManager.createOWLOntologyManager();
		try {
			ontology =  manager.loadOntologyFromOntologyDocument(owlFile);
			factory = ontology.getOWLOntologyManager().getOWLDataFactory();
			defaultprefix = manager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat().getDefaultPrefix();
			datatypePrefix = manager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat().getPrefix("xsd:");
		} catch (OWLOntologyCreationException e) {
	    	System.err.println("Error creating OWL ontology: " + e.getMessage());
	    	System.exit(-1);
		}
	}
	//---------------------------------------READ---------------------------------------

	//gets individuals Declaration(Individuals)
	public Set<OWLClass> getClasses() {
		return ontology.getClassesInSignature();
	}
	//gets individuals Declaration(Individuals)
	public Set<OWLNamedIndividual> getIndividuals() {
		return ontology.getIndividualsInSignature();
	}
	//get declaration of data properties Declaration(dataProperty)
	public Set<OWLDataProperty> getDataProperties() {
		return ontology.getDataPropertiesInSignature();
	}	
	//get declaration of data properties Declaration(dataProperty)
	public Set<OWLObjectProperty> getObjectProperties() {
		return ontology.getObjectPropertiesInSignature();
	}
	public LinkedHashMap<OWLClass, ArrayList<OWLClass>> getTaxonomy() {
        Set<OWLClass> classes = ontology.getClassesInSignature();
        LinkedHashMap<OWLClass, ArrayList<OWLClass>> taxonomy = new LinkedHashMap<>();
        for(OWLClass c : classes) {
        	taxonomy.put(c, new ArrayList<OWLClass>());
        	for(OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSuperClass(c))
        		taxonomy.get(c).add(axiom.getSubClass().asOWLClass());
        }
        return taxonomy;
	}
	//gets a list of individuals of a class
	public HashMap<OWLClass, Set<OWLClassAssertionAxiom>> getClassesAndTheirIndividuals() {
		HashMap<OWLClass, Set<OWLClassAssertionAxiom>> classesIndividuals = new HashMap<>();
		for(OWLClass c: ontology.getClassesInSignature())
			classesIndividuals.put(c, ontology.getClassAssertionAxioms(c));
		return classesIndividuals;
	}
	//gets a map with the individuals and their dataproperties values dataPropertyAssertions(...)
	public HashMap<OWLNamedIndividual, Set<OWLDataPropertyAssertionAxiom>> getindividualsDataProperties() {
		HashMap<OWLNamedIndividual, Set<OWLDataPropertyAssertionAxiom>> individualsProperties = new HashMap<>();
		for(OWLNamedIndividual individual : getIndividuals())
			individualsProperties.put(individual, ontology.getDataPropertyAssertionAxioms(individual));
		return individualsProperties;
	}
	//gets a map with the individuals and their objectproperties connections objectPropertyAssertions(...)
	public HashMap<OWLNamedIndividual, Set<OWLObjectPropertyAssertionAxiom>> getIndividualsObjectProperties() {
		HashMap<OWLNamedIndividual, Set<OWLObjectPropertyAssertionAxiom>> individualsProperties = new HashMap<>();
		for(OWLNamedIndividual individual: getIndividuals())
			individualsProperties.put(individual, ontology.getObjectPropertyAssertionAxioms(individual));
		return individualsProperties;
	}
	//---------------------------------------CREATE---------------------------------------
	//Exemplo: handler.declareOWLEntity(EntityType.CLASS,"Pessoas");
	public void declareOWLEntity(EntityType<?> type, String name) {
		OWLEntity entity = factory.getOWLEntity(type, IRI.create(defaultprefix, name));
		OWLAxiom axiom = factory.getOWLDeclarationAxiom(entity);
		manager.addAxiom(ontology, axiom);
		saveOntology();
	}
	
	public void declareClassAssertion(String classname, String individual) {
		OWLClass entity = factory.getOWLClass(IRI.create(defaultprefix, classname));
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLAxiom axiom = factory.getOWLClassAssertionAxiom(entity, ind);
		if(ontology.getClassesInSignature().contains(entity) && ontology.getIndividualsInSignature().contains(ind)) {
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	}
	
	public void declareSubClassOf(String sc1, String sc2) {
		OWLClass superclass = factory.getOWLClass(IRI.create(defaultprefix, sc1));
		OWLClass subclass = factory.getOWLClass(IRI.create(defaultprefix, sc2));
		OWLAxiom axiom = factory.getOWLSubClassOfAxiom(superclass, subclass);
		Set<OWLClass> allClasses = getClasses();
		if(allClasses.contains(superclass) && allClasses.contains(subclass)) {
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	}

	private void declareMainDataPropertyAssertion(String individual, String dataProperty, OWLLiteral literal) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLDataProperty data = factory.getOWLDataProperty(IRI.create(defaultprefix, dataProperty));
		OWLAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(data, ind, literal);
		if(getDataProperties().contains(data) && getIndividuals().contains(ind) && !hasDeclaredDataPropertyAssertion(ind, data)) {
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	}
	
	public void declareDataPropertyAssertion(String individual, String dataProperty, String value) {
		OWLLiteralImplString literal = new OWLLiteralImplString(value);
		declareMainDataPropertyAssertion(individual, dataProperty, literal);
	}
	
	public void declareDataPropertyAssertion(String individual, String dataProperty, boolean value) {
		
		OWLDatatype datatype = factory.getOWLDatatype(IRI.create(datatypePrefix, "boolean"));
		OWLLiteralImplBoolean literal = new OWLLiteralImplBoolean(value, datatype);
		declareMainDataPropertyAssertion(individual, dataProperty, literal);
	}
	
	public void declareDataPropertyAssertion(String individual, String dataProperty, int value) {
		OWLDatatype datatype = factory.getOWLDatatype(IRI.create(datatypePrefix, "integer"));
		OWLLiteralImplInteger  literal = new OWLLiteralImplInteger(value, datatype);
		declareMainDataPropertyAssertion(individual, dataProperty, literal);
	
	}
	
	public void declareDataPropertyAssertion(String individual, String dataProperty, double value) {
		OWLDatatype datatype = factory.getOWLDatatype(IRI.create(datatypePrefix, "double"));
		OWLLiteralImplDouble  literal = new OWLLiteralImplDouble(value, datatype);
		declareMainDataPropertyAssertion(individual, dataProperty, literal);
	}
	
	public void declareDataPropertyAssertion(String individual, String dataProperty, float value) {
		OWLDatatype datatype = factory.getOWLDatatype(IRI.create(datatypePrefix, "float"));
		OWLLiteralImplFloat  literal = new OWLLiteralImplFloat(value, datatype);
		declareMainDataPropertyAssertion(individual, dataProperty, literal);
	}

	public boolean hasDeclaredDataPropertyAssertion(OWLNamedIndividual ind, OWLDataProperty data) {
		for(OWLDataPropertyAssertionAxiom ax: getindividualsDataProperties().get(ind)) {
			if(ax.getDataPropertiesInSignature().contains(data)) {
				return true;
			}
		}
		return false;
	}
	
	public void declareObjectPropertyAssertion(String op, String ind1, String ind2) {
		OWLObjectPropertyExpression objectProperty = factory.getOWLObjectProperty(IRI.create(defaultprefix, op));
		OWLNamedIndividual individual1 = factory.getOWLNamedIndividual(IRI.create(defaultprefix, ind1));
		OWLNamedIndividual individual2 = factory.getOWLNamedIndividual(IRI.create(defaultprefix, ind2));
		OWLAxiom axiom1 = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, individual1, individual2);
		OWLAxiom axiom2 = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, individual2, individual1);
		Set<OWLNamedIndividual> allIndividuals = getIndividuals();
		if(!ind1.equals(ind2) && getObjectProperties().contains(objectProperty) && allIndividuals.contains(individual1) && allIndividuals.contains(individual2)) {
			manager.addAxiom(ontology, axiom1);
			manager.addAxiom(ontology, axiom2);
			saveOntology();
		}
	}
	//---------------------------------------DELETE---------------------------------------
	public void deleteObjectProperty(String name) {
		OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(defaultprefix, name));
		for(OWLNamedIndividual individual: getIndividuals()) 
			for(OWLObjectPropertyAssertionAxiom op: ontology.getObjectPropertyAssertionAxioms(individual))
				if(op.getProperty().equals(objectProperty))
					manager.removeAxiom(ontology, op);
		for(OWLDeclarationAxiom item: ontology.getDeclarationAxioms(objectProperty))
			manager.removeAxiom(ontology, item);
		saveOntology();	
	}
	
	public void deleteObjectPropertyofIndividuals(String objectPropertyName, String individualName1, String individualName2) {
		OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(defaultprefix, objectPropertyName));
		OWLNamedIndividual individual1 = factory.getOWLNamedIndividual(IRI.create(defaultprefix,individualName1));
		OWLNamedIndividual individual2 = factory.getOWLNamedIndividual(IRI.create(defaultprefix,individualName2));
		Set<OWLObjectPropertyAssertionAxiom> assertions = ontology.getObjectPropertyAssertionAxioms(individual1);
		assertions.addAll(ontology.getObjectPropertyAssertionAxioms(individual2));
		for(OWLObjectPropertyAssertionAxiom op: assertions) 
			if(op.getProperty().equals(objectProperty) && op.getIndividualsInSignature().contains(individual1) && op.getIndividualsInSignature().contains(individual2))
				manager.removeAxiom(ontology, op);
		saveOntology();
	}
	
	public void deleteDataProperty(String name){
		OWLDataProperty dataProperty = factory.getOWLDataProperty(IRI.create(defaultprefix, name));
		for(OWLNamedIndividual individual: getIndividuals()) 
			for(OWLDataPropertyAssertionAxiom dp: ontology.getDataPropertyAssertionAxioms(individual))
				if(dp.getProperty().equals(dataProperty))
					manager.removeAxiom(ontology, dp);
		for(OWLDeclarationAxiom item: ontology.getDeclarationAxioms(dataProperty))
			manager.removeAxiom(ontology, item);
		saveOntology();	
	}
	
	public void deleteDataPropertyOfIndividual(String individualName, String dataPropertyName) {
		OWLDataProperty dataProperty = factory.getOWLDataProperty(IRI.create(defaultprefix, dataPropertyName));
		OWLNamedIndividual individual = factory.getOWLNamedIndividual(IRI.create(defaultprefix,individualName));
		for(OWLDataPropertyAssertionAxiom dp: ontology.getDataPropertyAssertionAxioms(individual))
			if(dp.getProperty().equals(dataProperty))
				manager.removeAxiom(ontology, dp);
		saveOntology();
	}
	
	public void deleteIndividual(String name) {//TODO remove his dataProperties ?
		OWLNamedIndividual namedIndividual = factory.getOWLNamedIndividual(IRI.create(defaultprefix,name));
		for(OWLClassAssertionAxiom item: ontology.getClassAssertionAxioms(namedIndividual))
			manager.removeAxiom(ontology, item);
		for(OWLDataPropertyAssertionAxiom item: ontology.getDataPropertyAssertionAxioms(namedIndividual))
			manager.removeAxiom(ontology, item);
		for(OWLDeclarationAxiom item: ontology.getDeclarationAxioms(namedIndividual))
			manager.removeAxiom(ontology, item);
		saveOntology();
	}
	
	public void deleteClassFromIndividual(String individualName, String className) {
		OWLClass owlClass = factory.getOWLClass(IRI.create(defaultprefix, className));
		
		OWLNamedIndividual individual = factory.getOWLNamedIndividual(IRI.create(defaultprefix,individualName));
		for(OWLClassAssertionAxiom dp: ontology.getClassAssertionAxioms(individual))
			if(dp.getClassExpression().equals(owlClass))
				manager.removeAxiom(ontology, dp);
		saveOntology();
	}
	
	public void deleteClass(String name, String iri, Boolean isName) {//TODO delete individuals from the class
		OWLClass owlClass = null;
		if(isName){
			owlClass = factory.getOWLClass(IRI.create(defaultprefix, name));
		}else{
			owlClass = factory.getOWLClass(IRI.create(iri));
		}
		for(OWLSubClassOfAxiom item: ontology.getSubClassAxiomsForSuperClass(owlClass))//delete all subclasses
			deleteClass("", item.getSubClass().toString().replaceAll("(<|>)", ""), false);
		HashMap<OWLClass, Set<OWLClassAssertionAxiom>> individuals = getClassesAndTheirIndividuals();
		for(OWLClassAssertionAxiom item: individuals.get(owlClass))//delete all individuals from the owlClass
			manager.removeAxiom(ontology, item);
		for(OWLSubClassOfAxiom item: ontology.getSubClassAxiomsForSubClass(owlClass))//delete all axioms from the owlClass
			manager.removeAxiom(ontology, item);
		for(OWLDeclarationAxiom item: ontology.getDeclarationAxioms(owlClass))//deletes owlClass declaration
			manager.removeAxiom(ontology, item);
		saveOntology();
	}
	//---------------------------------------UPDATE---------------------------------------
	public void changeClass(String oldName, String newName) {
		Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager, Collections.singleton(ontology));
	    OWLClass ni = factory.getOWLClass(IRI.create(defaultprefix, oldName));

	    for(OWLClass toRename: ontology.getClassesInSignature()) {
			if(toRename.equals(ni)) {
	        	entity2IRIMap.put(toRename, IRI.create(defaultprefix, newName));
	    	}
		}

	    manager.applyChanges(renamer.changeIRI(entity2IRIMap));
	    saveOntology();
	}
	
	public void changeNamedIndividual(String oldName, String newName) {
		Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager, Collections.singleton(ontology));
	    OWLNamedIndividual ni = factory.getOWLNamedIndividual(IRI.create(defaultprefix, oldName));

	    for(OWLNamedIndividual toRename: ontology.getIndividualsInSignature()) {
			if(toRename.equals(ni)) {
	        	entity2IRIMap.put(toRename, IRI.create(defaultprefix, newName));
	    	}
		}

	    manager.applyChanges(renamer.changeIRI(entity2IRIMap));
	    saveOntology();
	}
	
	public void changeObjectProperty(String oldName, String newName) {
		Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager, Collections.singleton(ontology));
	    OWLObjectProperty ni = factory.getOWLObjectProperty(IRI.create(defaultprefix, oldName));

	    for(OWLObjectProperty toRename: ontology.getObjectPropertiesInSignature()) {
			if(toRename.equals(ni)) {
	        	entity2IRIMap.put(toRename, IRI.create(defaultprefix, newName));
	    	}
		}

	    manager.applyChanges(renamer.changeIRI(entity2IRIMap));
	    saveOntology();
	}
	
	public void changeDataProperty(String oldName, String newName) {
		Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager, Collections.singleton(ontology));
	    OWLDataProperty ni = factory.getOWLDataProperty(IRI.create(defaultprefix, oldName));

	    for(OWLDataProperty toRename: ontology.getDataPropertiesInSignature()) {
			if(toRename.equals(ni)) {
	        	entity2IRIMap.put(toRename, IRI.create(defaultprefix, newName));
	    	}
		}

	    manager.applyChanges(renamer.changeIRI(entity2IRIMap));
	    saveOntology();
	}
	
	public void changeDataPropertyAssertion(String individual, String dataProperty, String oldValue, String newValue) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLDataProperty dproperty = factory.getOWLDataProperty(IRI.create(defaultprefix, dataProperty));
		OWLAxiom owlOldAxiom = factory.getOWLDataPropertyAssertionAxiom(dproperty, ind, oldValue);
		if(getIndividuals().contains(ind) && getDataProperties().contains(dproperty) && getindividualsDataProperties().get(ind).contains(owlOldAxiom)) {
			deleteDataPropertyOfIndividual(individual, dataProperty);
			declareDataPropertyAssertion(individual, dataProperty, newValue);
		}
	}
	
	public void changeDataPropertyAssertion(String individual, String dataProperty, boolean oldValue, boolean newValue) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLDataProperty dproperty = factory.getOWLDataProperty(IRI.create(defaultprefix, dataProperty));
		OWLAxiom owlOldAxiom = factory.getOWLDataPropertyAssertionAxiom(dproperty, ind, oldValue);
		if(getIndividuals().contains(ind) && getDataProperties().contains(dproperty) && getindividualsDataProperties().get(ind).contains(owlOldAxiom)) {
			deleteDataPropertyOfIndividual(individual, dataProperty);
			declareDataPropertyAssertion(individual, dataProperty, newValue);
		}
	}
	
	public void changeDataPropertyAssertion(String individual, String dataProperty, int oldValue, int newValue) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLDataProperty dproperty = factory.getOWLDataProperty(IRI.create(defaultprefix, dataProperty));
		OWLAxiom owlOldAxiom = factory.getOWLDataPropertyAssertionAxiom(dproperty, ind, oldValue);
		if(getIndividuals().contains(ind) && getDataProperties().contains(dproperty) && getindividualsDataProperties().get(ind).contains(owlOldAxiom)) {
			deleteDataPropertyOfIndividual(individual, dataProperty);
			declareDataPropertyAssertion(individual, dataProperty, newValue);
		}
	}
	
	public void changeDataPropertyAssertion(String individual, String dataProperty, double oldValue, double newValue) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLDataProperty dproperty = factory.getOWLDataProperty(IRI.create(defaultprefix, dataProperty));
		OWLAxiom owlOldAxiom = factory.getOWLDataPropertyAssertionAxiom(dproperty, ind, oldValue);
		if(getIndividuals().contains(ind) && getDataProperties().contains(dproperty) && getindividualsDataProperties().get(ind).contains(owlOldAxiom)) {
			deleteDataPropertyOfIndividual(individual, dataProperty);
			declareDataPropertyAssertion(individual, dataProperty, newValue);
		}
	}
	
	public void changeDataPropertyAssertion(String individual, String dataProperty, float oldValue, float newValue) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLDataProperty dproperty = factory.getOWLDataProperty(IRI.create(defaultprefix, dataProperty));
		OWLAxiom owlOldAxiom = factory.getOWLDataPropertyAssertionAxiom(dproperty, ind, oldValue);
		if(getIndividuals().contains(ind) && getDataProperties().contains(dproperty) && getindividualsDataProperties().get(ind).contains(owlOldAxiom)) {
			deleteDataPropertyOfIndividual(individual, dataProperty);
			declareDataPropertyAssertion(individual, dataProperty, newValue);
		}
	}
	//---------------------------------------AUXILIARY FUNCTIONS---------------------------------------
	private void saveOntology() {
		try {
			manager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(owlFile));
		} catch (OWLOntologyStorageException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	

//	public void leOWLTest() {
//    	
//        try {
//          // Loading an OWL ontology using the OWLAPI
//          OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
//          OWLOntology ontology =  ontologyManager.loadOntologyFromOntologyDocument(owlFile);
//        	  
//          // Create SQWRL query engine using the SWRLAPI
//          SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
//          // Create and execute a SQWRL query using the SWRLAPI
//          // Os algoritmos que funcionam bem para problemas com 2,3,4,etc. objetivos são diferentes
//          String numberOfObjectives = "2";
//          String query = "Algorithm(?alg) ^ "   
//        	+ "minObjectivesAlgorithmIsAbleToDealWith(?alg,?min) ^ swrlb:lessThanOrEqual(?min,"+numberOfObjectives+")"
//        	+ "maxObjectivesAlgorithmIsAbleToDealWith(?alg,?max) ^ swrlb:greaterThanOrEqual(?max,"+numberOfObjectives+")"
//        	+ " -> sqwrl:select(?alg) ^ sqwrl:orderBy(?alg)";
//          
//          SQWRLResult result = queryEngine.runSQWRLQuery("q1", query);
//          
//          // Process the SQWRL result
//    	  System.out.println("Query: \n" + query + "\n");
//    	  System.out.println("Result: ");
//          while (result.next()) {
//        	  System.out.println(result.getNamedIndividual("alg").getShortName());
//          }
//          
//        } catch (OWLOntologyCreationException e) {
//          System.err.println("Error creating OWL ontology: " + e.getMessage());
//          System.exit(-1);
//        } catch (SWRLParseException e) {
//          System.err.println("Error parsing SWRL rule or SQWRL query: " + e.getMessage());
//          System.exit(-1);
//        } catch (SQWRLException e) {
//          System.err.println("Error running SWRL rule or SQWRL query: " + e.getMessage());
//          System.exit(-1);
//        } catch (Exception e) {
//        	e.printStackTrace();
//          System.err.println("Error starting application: " + e.getMessage());
//          System.exit(-1);
//        }
//        
//    }
	
}
