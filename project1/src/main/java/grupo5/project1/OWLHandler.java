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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
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

	public HashMap<OWLClass, Set<OWLSubClassOfAxiom>> getClasses() {
        Set<OWLClass> classes = ontology.getClassesInSignature();
        HashMap<OWLClass, Set<OWLSubClassOfAxiom>> subclasses = new HashMap<>();
        for(OWLClass c : classes) {
        	subclasses.put(c, ontology.getSubClassAxiomsForSuperClass(c));
        }
        return subclasses;
	}

//	Exemplo: handler.declareOWLEntity(EntityType.CLASS,"Pessoas");
	public <E> void declareOWLEntity(EntityType<?> type, String name) {
		OWLEntity entity = factory.getOWLEntity(type, IRI.create(prefix, name));
		OWLAxiom axiom = factory.getOWLDeclarationAxiom(entity);
		try {
			manager.addAxiom(ontology, axiom);
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
