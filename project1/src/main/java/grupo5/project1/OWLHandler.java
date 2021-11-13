package grupo5.project1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

public class OWLHandler {

	private File owlFile;
	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private OWLDataFactory factory;
	private String prefix;

	public OWLHandler(String file) {
		owlFile = new File(file);
		manager = OWLManager.createOWLOntologyManager();
		try {
			ontology =  manager.loadOntologyFromOntologyDocument(owlFile);
			factory = ontology.getOWLOntologyManager().getOWLDataFactory();
			prefix = manager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat().getDefaultPrefix();
		} catch (OWLOntologyCreationException e) {
	    	System.err.println("Error creating OWL ontology: " + e.getMessage());
	    	System.exit(-1);
		}
	}
	//---------------------------------------READ---------------------------------------
	public HashMap<OWLClass, Set<OWLSubClassOfAxiom>> getTaxonomy() {
        Set<OWLClass> classes = ontology.getClassesInSignature();
        HashMap<OWLClass, Set<OWLSubClassOfAxiom>> taxonomy = new HashMap<>();
        for(OWLClass c : classes) {
        	taxonomy.put(c, ontology.getSubClassAxiomsForSuperClass(c));
        	System.out.println(c + " - " + taxonomy.get(c));
        }
        return taxonomy;
	}
	
	//gets individuals Declaration(Individuals)
		public Set<OWLClass> getClasses() {
			return ontology.getClassesInSignature();
		}
	//gets individuals Declaration(Individuals)
	public Set<OWLNamedIndividual> getIndividuals() {
		return ontology.getIndividualsInSignature();
	}
	//gets a list of individuals of a class
	public HashMap<OWLClass, Set<OWLClassAssertionAxiom>> getClassesAndTheirIndividuals() {
		HashMap<OWLClass, Set<OWLClassAssertionAxiom>> classesIndividuals = new HashMap<>();
		for(OWLClass c: ontology.getClassesInSignature()) {
			classesIndividuals.put(c, ontology.getClassAssertionAxioms(c));
		}
		return classesIndividuals;
	}
	//gets a map with the individuals and their dataproperties values dataPropertyAssertions(...)
	public HashMap<OWLNamedIndividual, Set<OWLDataPropertyAssertionAxiom>> getindividualsProperties() {
		HashMap<OWLNamedIndividual, Set<OWLDataPropertyAssertionAxiom>> individualsProperties = new HashMap<>();
		for(OWLNamedIndividual individual : getIndividuals()) {
			individualsProperties.put(individual, ontology.getDataPropertyAssertionAxioms(individual));
		}
		return individualsProperties;
	}
	//get declaration of data properties Declaration(dataProperty)
	public Set<OWLDataProperty> getDataProperties() {
		return ontology.getDataPropertiesInSignature();
	}
	
	//get declaration of data properties Declaration(dataProperty)
	public Set<OWLObjectProperty> getObjectProperties() {
		return ontology.getObjectPropertiesInSignature();
	}
	//---------------------------------------CREATE---------------------------------------
	//Exemplo: handler.declareOWLEntity(EntityType.CLASS,"Pessoas");
	public <E> void declareOWLEntity(EntityType<?> type, String name) {
		OWLEntity entity = factory.getOWLEntity(type, IRI.create(prefix, name));
		OWLAxiom axiom = factory.getOWLDeclarationAxiom(entity);
		manager.addAxiom(ontology, axiom);
		saveOntology();
	}
	
	public void declareClassAssertion(String classname, String individual) {
		OWLClass entity = factory.getOWLClass(IRI.create(prefix, classname));
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(prefix, individual));
		OWLAxiom axiom = factory.getOWLClassAssertionAxiom(entity, ind);
		if(ontology.getClassesInSignature().contains(entity) && ontology.getIndividualsInSignature().contains(ind)) {
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	}
	
	public void declareSubClassOf(String sc1, String sc2) {
		OWLClass superclass = factory.getOWLClass(IRI.create(prefix, sc1));
		OWLClass subclass = factory.getOWLClass(IRI.create(prefix, sc2));
		OWLAxiom axiom = factory.getOWLSubClassOfAxiom(superclass, subclass);
		Set<OWLClass> allClasses = getClasses();
		if(allClasses.contains(superclass) && allClasses.contains(subclass)) {
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	}

	public void declareDataPropertyAssertion(String individual, String dataProperty, String value) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(prefix, individual));
		OWLDataProperty data = factory.getOWLDataProperty(IRI.create(prefix, dataProperty));
		OWLAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(data, ind, value);
		if(getDataProperties().contains(data) && getIndividuals().contains(ind) && !hasDeclaredDataPropertyAssertion(ind, data)) {
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	}
	
	public void declareDataPropertyAssertion(String individual, String dataProperty, boolean value) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(prefix, individual));
		OWLDataProperty data = factory.getOWLDataProperty(IRI.create(prefix, dataProperty));
		OWLAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(data, ind, value);
		if(getDataProperties().contains(data) && getIndividuals().contains(ind) && !hasDeclaredDataPropertyAssertion(ind, data)) {
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	}
	
	public void declareDataPropertyAssertion(String individual, String dataProperty, int value) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(prefix, individual));
		OWLDataProperty data = factory.getOWLDataProperty(IRI.create(prefix, dataProperty));
		OWLAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(data, ind, value);
		if(getDataProperties().contains(data) && getIndividuals().contains(ind) && !hasDeclaredDataPropertyAssertion(ind, data)) {
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	
	}
	
	public void declareDataPropertyAssertion(String individual, String dataProperty, double value) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(prefix, individual));
		OWLDataProperty data = factory.getOWLDataProperty(IRI.create(prefix, dataProperty));
		OWLAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(data, ind, value);
		if(getDataProperties().contains(data) && getIndividuals().contains(ind) && !hasDeclaredDataPropertyAssertion(ind, data)) {
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	}
	
	public boolean hasDeclaredDataPropertyAssertion(OWLNamedIndividual ind, OWLDataProperty data) {
		for(OWLDataPropertyAssertionAxiom ax: getindividualsProperties().get(ind)) {
			if(ax.getDataPropertiesInSignature().contains(data)) {
				return true;
			}
		}
		return false;
	}
	
	public void declareObjectPropertyAssertion(String op, String ind1, String ind2) {
		OWLObjectPropertyExpression objectProperty = factory.getOWLObjectProperty(IRI.create(prefix, op));
		OWLNamedIndividual individual1 = factory.getOWLNamedIndividual(IRI.create(prefix, ind1));
		OWLNamedIndividual individual2 = factory.getOWLNamedIndividual(IRI.create(prefix, ind2));
		OWLAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, individual1, individual2);
		Set<OWLNamedIndividual> allIndividuals = getIndividuals();
		if(!ind1.equals(ind2) && getObjectProperties().contains(objectProperty) && allIndividuals.contains(individual1) && allIndividuals.contains(individual2)) {
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	}
	//---------------------------------------DELETE---------------------------------------
	public void deleteObjectProperty(String name) {
		OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(prefix, name));
		for(OWLNamedIndividual individual: getIndividuals()) 
			for(OWLObjectPropertyAssertionAxiom op: ontology.getObjectPropertyAssertionAxioms(individual))
				if(op.getProperty().equals(objectProperty))
					manager.removeAxiom(ontology, op);
		for(OWLDeclarationAxiom item: ontology.getDeclarationAxioms(objectProperty))
			manager.removeAxiom(ontology, item);
		saveOntology();	
	}
	
	public void deleteObjectPropertyofIndividuals(String objectPropertyName, String individualName1, String individualName2) {
		OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(prefix, objectPropertyName));
		OWLNamedIndividual individual1 = factory.getOWLNamedIndividual(IRI.create(prefix,individualName1));
		OWLNamedIndividual individual2 = factory.getOWLNamedIndividual(IRI.create(prefix,individualName2));
		Set<OWLObjectPropertyAssertionAxiom> assertions = ontology.getObjectPropertyAssertionAxioms(individual1);
		assertions.addAll(ontology.getObjectPropertyAssertionAxioms(individual2));
		for(OWLObjectPropertyAssertionAxiom op: assertions) 
			if(op.getProperty().equals(objectProperty) && op.getIndividualsInSignature().contains(individual1) && op.getIndividualsInSignature().contains(individual2))
				manager.removeAxiom(ontology, op);
		saveOntology();
	}
	
	public void deleteDataProperty(String name){
		OWLDataProperty dataProperty = factory.getOWLDataProperty(IRI.create(prefix, name));
		for(OWLNamedIndividual individual: getIndividuals()) 
			for(OWLDataPropertyAssertionAxiom dp: ontology.getDataPropertyAssertionAxioms(individual))
				if(dp.getProperty().equals(dataProperty))
					manager.removeAxiom(ontology, dp);
		for(OWLDeclarationAxiom item: ontology.getDeclarationAxioms(dataProperty))
			manager.removeAxiom(ontology, item);
		saveOntology();	
	}
	
	public void deleteDataPropertyOfIndividual(String individualName, String dataPropertyName) {
		OWLDataProperty dataProperty = factory.getOWLDataProperty(IRI.create(prefix, dataPropertyName));
		OWLNamedIndividual individual = factory.getOWLNamedIndividual(IRI.create(prefix,individualName));
		for(OWLDataPropertyAssertionAxiom dp: ontology.getDataPropertyAssertionAxioms(individual))
			if(dp.getProperty().equals(dataProperty))
				manager.removeAxiom(ontology, dp);
		saveOntology();
	}
	
	public void deleteIndividual(String name) {//TODO remove his dataProperties ?
		OWLNamedIndividual namedIndividual = factory.getOWLNamedIndividual(IRI.create(prefix,name));
		for(OWLClassAssertionAxiom item: ontology.getClassAssertionAxioms(namedIndividual))
			manager.removeAxiom(ontology, item);
		for(OWLDataPropertyAssertionAxiom item: ontology.getDataPropertyAssertionAxioms(namedIndividual))
			manager.removeAxiom(ontology, item);
		for(OWLDeclarationAxiom item: ontology.getDeclarationAxioms(namedIndividual))
			manager.removeAxiom(ontology, item);
		saveOntology();
	}
	
	public void deleteClassFromIndividual(String individualName, String className) {
		OWLClass owlClass = factory.getOWLClass(IRI.create(prefix, className));
		OWLNamedIndividual individual = factory.getOWLNamedIndividual(IRI.create(prefix,individualName));
		for(OWLClassAssertionAxiom dp: ontology.getClassAssertionAxioms(individual))
			if(dp.getClassExpression().equals(owlClass))
				manager.removeAxiom(ontology, dp);
		saveOntology();
	}
	
	public void deleteClass(String name, String iri, Boolean isName) {//TODO delete individuals from the class
		OWLClass owlClass = null;
		if(isName){
			owlClass = factory.getOWLClass(IRI.create(prefix, name));
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
