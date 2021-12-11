package logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jgit.internal.storage.file.LockFile;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
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

	private LockFile owlFile;
	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private SQWRLQueryEngine queryEngine;
	private OWLDataFactory factory;
	private String defaultprefix;
	private String datatypePrefix;
	
	public OWLHandler(InputStream in, LockFile owlFile) {
		this.owlFile = owlFile;
		manager = OWLManager.createOWLOntologyManager();
		try {
			ontology =  manager.loadOntologyFromOntologyDocument(in);
			queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
			factory = ontology.getOWLOntologyManager().getOWLDataFactory();
			defaultprefix = manager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat().getDefaultPrefix();
			datatypePrefix = manager.getOntologyFormat(ontology).asPrefixOWLOntologyFormat().getPrefix("xsd:");
		} catch (OWLOntologyCreationException e) {
	    	System.err.println("Error creating OWL ontology: " + e.getMessage());
	    	System.exit(-1);
		}
	}
	
	//---------------------------------------READ---------------------------------------

	//gets all the classes (Declaration(Class))
	public Set<OWLClass> getClasses() {
		return ontology.getClassesInSignature();
	}
	
	//gets all the individuals (Declaration(Individual))
	public Set<OWLNamedIndividual> getIndividuals() {
		return ontology.getIndividualsInSignature();
	}
	
	//gets all the data properties (Declaration(DataProperty))
	public Set<OWLDataProperty> getDataProperties() {
		return ontology.getDataPropertiesInSignature();
	}
	
	//gets all the object properties (Declaration(ObjectProperty))
	public Set<OWLObjectProperty> getObjectProperties() {
		return ontology.getObjectPropertiesInSignature();
	}
	
	//gets all the classes and their individuals
	public HashMap<OWLClass, Set<OWLClassAssertionAxiom>> getClassesIndividuals() {
		HashMap<OWLClass, Set<OWLClassAssertionAxiom>> classesIndividuals = new HashMap<>();
		for(OWLClass c: ontology.getClassesInSignature())
			classesIndividuals.put(c, ontology.getClassAssertionAxioms(c));
		return classesIndividuals;
	}
	
	//gets all the individuals and their classes
	public HashMap<OWLNamedIndividual, Set<OWLClassAssertionAxiom>> getIndividualsClasses() {
		HashMap<OWLNamedIndividual, Set<OWLClassAssertionAxiom>> individualsClasses = new HashMap<>();
		for(OWLNamedIndividual individual: getIndividuals())
			individualsClasses.put(individual, ontology.getClassAssertionAxioms(individual));
		return individualsClasses;
	}
	
	//gets all the individuals and their data properties
	public HashMap<OWLNamedIndividual, Set<OWLDataPropertyAssertionAxiom>> getIndividualsDataProperties() {
		HashMap<OWLNamedIndividual, Set<OWLDataPropertyAssertionAxiom>> individualsProperties = new HashMap<>();
		for(OWLNamedIndividual individual : getIndividuals())
			individualsProperties.put(individual, ontology.getDataPropertyAssertionAxioms(individual));
		return individualsProperties;
	}
	
	//gets all the individuals and their object properties
	public HashMap<OWLNamedIndividual, Set<OWLObjectPropertyAssertionAxiom>> getIndividualsObjectProperties() {
		HashMap<OWLNamedIndividual, Set<OWLObjectPropertyAssertionAxiom>> individualsProperties = new HashMap<>();
		for(OWLNamedIndividual individual: getIndividuals())
			individualsProperties.put(individual, ontology.getObjectPropertyAssertionAxioms(individual));
		return individualsProperties;
	}
	
	//gets all the object properties and their axioms
	public HashMap<OWLObjectProperty, Set<OWLObjectPropertyAxiom>> getObjectPropertiesAxioms() {
		HashMap<OWLObjectProperty, Set<OWLObjectPropertyAxiom>> objectPropertiesAxioms = new HashMap<>();
		for(OWLObjectProperty obj : getObjectProperties())
			objectPropertiesAxioms.put(obj, ontology.getAxioms(obj, Imports.EXCLUDED));
		return objectPropertiesAxioms;
	}
	
	//gets all the object properties and their characteristics
	public HashMap<OWLObjectProperty, Set<String>> getObjectPropertiesCharacteristics() {
		HashMap<OWLObjectProperty, Set<String>> objectPropertiesCharacteritics = new HashMap<>();
		for(Entry<OWLObjectProperty, Set<OWLObjectPropertyAxiom>> entry : getObjectPropertiesAxioms().entrySet()) {
			objectPropertiesCharacteritics.put(entry.getKey(), entry.getValue().stream().map(a -> a.getAxiomType().getName().replace("ObjectProperty", "")).collect(Collectors.toSet()));
		}
		return objectPropertiesCharacteritics;
	}
	
	//gets the class taxonomy
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
	
	public void declareSubClassOf(String superClassString, String subClassString) {
		OWLClass superclass = factory.getOWLClass(IRI.create(defaultprefix, superClassString));
		OWLClass subclass = factory.getOWLClass(IRI.create(defaultprefix, subClassString));
		OWLAxiom axiom = factory.getOWLSubClassOfAxiom(subclass, superclass);
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

	private boolean hasDeclaredDataPropertyAssertion(OWLNamedIndividual ind, OWLDataProperty data) {
		for(OWLDataPropertyAssertionAxiom ax: getIndividualsDataProperties().get(ind)) {
			if(ax.getDataPropertiesInSignature().contains(data)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasDeclaredDataPropertyAssertion(String ind, String data) {
		return hasDeclaredDataPropertyAssertion(factory.getOWLNamedIndividual(IRI.create(defaultprefix, ind)), factory.getOWLDataProperty(IRI.create(defaultprefix, data))); 
	}
	
	private boolean hasDeclaredObjectPropertyAssertion(OWLNamedIndividual ind, OWLObjectProperty obj) {
		for(OWLObjectPropertyAssertionAxiom ax: getIndividualsObjectProperties().get(ind)) {
			if(ax.getDataPropertiesInSignature().contains((Object) obj)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasDeclaredObjectPropertyAssertion(String ind, String data) {
		return hasDeclaredObjectPropertyAssertion(factory.getOWLNamedIndividual(IRI.create(defaultprefix, ind)), factory.getOWLObjectProperty(IRI.create(defaultprefix, data))); 
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
	
	public void declareObjectPropertyAxiom(String objectProperty, String characteristic) {
		OWLObjectProperty obj = factory.getOWLObjectProperty(IRI.create(defaultprefix, objectProperty));
		OWLAxiom axiom;
		if(getObjectProperties().contains(obj)) {
			switch (characteristic) {
				case "functional":
					axiom = factory.getOWLFunctionalObjectPropertyAxiom(obj);
					break;
				case "inverse-functional":
					axiom = factory.getOWLInverseFunctionalObjectPropertyAxiom(obj);
					break;
				case "transitive":
					axiom = factory.getOWLTransitiveObjectPropertyAxiom(obj);
					break;
				case "symmetric":
					axiom = factory.getOWLSymmetricObjectPropertyAxiom(obj);
					break;
				case "asymmetric":
					axiom = factory.getOWLAsymmetricObjectPropertyAxiom(obj);
					break;
				case "reflexive":
					axiom = factory.getOWLReflexiveObjectPropertyAxiom(obj);
					break;
				case "irreflexive":
					axiom = factory.getOWLIrreflexiveObjectPropertyAxiom(obj);
					break;
				default:
					return;
			}
			manager.addAxiom(ontology, axiom);
			saveOntology();
		}
	}
	
	//---------------------------------------DELETE---------------------------------------
	
	public void deleteObjectProperty(String name) {
		OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(defaultprefix, name));
		deleteObjectPropertyAxioms(name);
		for(OWLNamedIndividual individual: getIndividuals()) 
			for(OWLObjectPropertyAssertionAxiom op: ontology.getObjectPropertyAssertionAxioms(individual))
				if(op.getProperty().equals(objectProperty))
					manager.removeAxiom(ontology, op);
		for(OWLDeclarationAxiom item: ontology.getDeclarationAxioms(objectProperty))
			manager.removeAxiom(ontology, item);
		saveOntology();	
	}
	
	public void deleteObjectPropertyOfIndividuals(String objectPropertyName, String individualName1, String individualName2) {
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
	
	public void deleteIndividual(String name) {
		OWLNamedIndividual namedIndividual = factory.getOWLNamedIndividual(IRI.create(defaultprefix,name));
		for(OWLClassAssertionAxiom item: ontology.getClassAssertionAxioms(namedIndividual))
			manager.removeAxiom(ontology, item);
		for(OWLDataPropertyAssertionAxiom item: ontology.getDataPropertyAssertionAxioms(namedIndividual))
			manager.removeAxiom(ontology, item);
		for(OWLDeclarationAxiom item: ontology.getDeclarationAxioms(namedIndividual))
			manager.removeAxiom(ontology, item);
		for(OWLObjectPropertyAssertionAxiom item: ontology.getObjectPropertyAssertionAxioms(namedIndividual))
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
		if(isName) {
			owlClass = factory.getOWLClass(IRI.create(defaultprefix, name));
		} else {
			owlClass = factory.getOWLClass(IRI.create(iri));
		}
		for(OWLSubClassOfAxiom item: ontology.getSubClassAxiomsForSuperClass(owlClass))//delete all subclasses
			deleteClass("", item.getSubClass().toString().replaceAll("(<|>)", ""), false);
		HashMap<OWLClass, Set<OWLClassAssertionAxiom>> individuals = getClassesIndividuals();
		if(individuals.get(owlClass) != null)
			for(OWLClassAssertionAxiom item: individuals.get(owlClass))//delete all individuals from the owlClass
				item.getIndividual().getIndividualsInSignature().forEach(ind -> deleteIndividual(ind.getIRI().getShortForm()));
		for(OWLSubClassOfAxiom item: ontology.getSubClassAxiomsForSubClass(owlClass))//delete all axioms from the owlClass
			manager.removeAxiom(ontology, item);
		for(OWLDeclarationAxiom item: ontology.getDeclarationAxioms(owlClass))//deletes owlClass declaration
			manager.removeAxiom(ontology, item);
		saveOntology();
	}
	
	public void deleteObjectPropertyAxioms(String objectProperty) {
		OWLObjectProperty obj = factory.getOWLObjectProperty(IRI.create(defaultprefix, objectProperty));
		HashMap<OWLObjectProperty, Set<OWLObjectPropertyAxiom>> aux = getObjectPropertiesAxioms();
		if(aux.containsKey(obj)) {
			manager.removeAxioms(ontology, aux.get(obj));
			saveOntology();
		}
	}
	
	//---------------------------------------UPDATE---------------------------------------
	
	public void changeClass(String oldName, String newName) {
		Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager, Collections.singleton(ontology));
	    OWLClass c = factory.getOWLClass(IRI.create(defaultprefix, oldName));

	    for(OWLClass toRename: ontology.getClassesInSignature()) {
			if(toRename.equals(c)) {
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
	    OWLObjectProperty op = factory.getOWLObjectProperty(IRI.create(defaultprefix, oldName));

	    for(OWLObjectProperty toRename: ontology.getObjectPropertiesInSignature()) {
			if(toRename.equals(op)) {
	        	entity2IRIMap.put(toRename, IRI.create(defaultprefix, newName));
	    	}
		}

	    manager.applyChanges(renamer.changeIRI(entity2IRIMap));
	    saveOntology();
	}
	
	public void changeDataProperty(String oldName, String newName) {
		Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager, Collections.singleton(ontology));
	    OWLDataProperty dp = factory.getOWLDataProperty(IRI.create(defaultprefix, oldName));

	    for(OWLDataProperty toRename: ontology.getDataPropertiesInSignature()) {
			if(toRename.equals(dp)) {
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
		if(getIndividuals().contains(ind) && getDataProperties().contains(dproperty) && getIndividualsDataProperties().get(ind).contains(owlOldAxiom)) {
			deleteDataPropertyOfIndividual(individual, dataProperty);
			declareDataPropertyAssertion(individual, dataProperty, newValue);
		}
	}
	
	public void changeDataPropertyAssertion(String individual, String dataProperty, boolean oldValue, boolean newValue) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLDataProperty dproperty = factory.getOWLDataProperty(IRI.create(defaultprefix, dataProperty));
		OWLAxiom owlOldAxiom = factory.getOWLDataPropertyAssertionAxiom(dproperty, ind, oldValue);
		if(getIndividuals().contains(ind) && getDataProperties().contains(dproperty) && getIndividualsDataProperties().get(ind).contains(owlOldAxiom)) {
			deleteDataPropertyOfIndividual(individual, dataProperty);
			declareDataPropertyAssertion(individual, dataProperty, newValue);
		}
	}
	
	public void changeDataPropertyAssertion(String individual, String dataProperty, int oldValue, int newValue) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLDataProperty dproperty = factory.getOWLDataProperty(IRI.create(defaultprefix, dataProperty));
		OWLAxiom owlOldAxiom = factory.getOWLDataPropertyAssertionAxiom(dproperty, ind, oldValue);
		if(getIndividuals().contains(ind) && getDataProperties().contains(dproperty) && getIndividualsDataProperties().get(ind).contains(owlOldAxiom)) {
			deleteDataPropertyOfIndividual(individual, dataProperty);
			declareDataPropertyAssertion(individual, dataProperty, newValue);
		}
	}
	
	public void changeDataPropertyAssertion(String individual, String dataProperty, double oldValue, double newValue) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLDataProperty dproperty = factory.getOWLDataProperty(IRI.create(defaultprefix, dataProperty));
		OWLAxiom owlOldAxiom = factory.getOWLDataPropertyAssertionAxiom(dproperty, ind, oldValue);
		if(getIndividuals().contains(ind) && getDataProperties().contains(dproperty) && getIndividualsDataProperties().get(ind).contains(owlOldAxiom)) {
			deleteDataPropertyOfIndividual(individual, dataProperty);
			declareDataPropertyAssertion(individual, dataProperty, newValue);
		}
	}
	
	public void changeDataPropertyAssertion(String individual, String dataProperty, float oldValue, float newValue) {
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(defaultprefix, individual));
		OWLDataProperty dproperty = factory.getOWLDataProperty(IRI.create(defaultprefix, dataProperty));
		OWLAxiom owlOldAxiom = factory.getOWLDataPropertyAssertionAxiom(dproperty, ind, oldValue);
		if(getIndividuals().contains(ind) && getDataProperties().contains(dproperty) && getIndividualsDataProperties().get(ind).contains(owlOldAxiom)) {
			deleteDataPropertyOfIndividual(individual, dataProperty);
			declareDataPropertyAssertion(individual, dataProperty, newValue);
		}
	}
	
	//---------------------------------------QUERY---------------------------------------
	
	public String runSQWRLQuery(String query) {
		try {
			SQWRLResult result = queryEngine.runSQWRLQuery("query", query);
			return result.toString();
		} catch (SQWRLException | SWRLParseException e) {
			return e.toString();
		}
	}
	
	//---------------------------------------AUXILIARY FUNCTIONS---------------------------------------
	
	private void saveOntology() {
		try {
			if(owlFile.lock()) {
				OutputStream out = owlFile.getOutputStream();
				manager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), out);
				out.close();
				if(!owlFile.commit())
					System.out.println("Error comitting");
			}
		} catch (OWLOntologyStorageException | IOException e) {
			e.printStackTrace();
		} finally {
			owlFile.unlock();
		}
	}
	
}
