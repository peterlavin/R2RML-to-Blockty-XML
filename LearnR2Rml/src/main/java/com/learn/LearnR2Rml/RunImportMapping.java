package com.learn.LearnR2Rml;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Query;
//import java.net.MalformedURLException;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.util.List;
//
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;

// Imported from local m2 repo, Christophe's jar
import r2rml.engine.R2RML;
import r2rml.engine.RRF;
import r2rml.model.R2RMLMapping;
import r2rml.model.R2RMLMappingFactory;
import r2rml.model.TriplesMap;



public class RunImportMapping {

	private static String CONSTRUCTSMAPS_1 = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:subjectMap [ rr:constant ?y ]. } WHERE { ?x rr:subject ?y. }";
	private static String CONSTRUCTOMAPS_2 = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:objectMap [ rr:constant ?y ]. } WHERE { ?x rr:object ?y. }";
	private static String CONSTRUCTPMAPS_3 = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:predicateMap [ rr:constant ?y ]. } WHERE { ?x rr:predicate ?y. }";
	private static String CONSTRUCTGMAPS_4 = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:graphMap [ rr:constant ?y ]. } WHERE { ?x rr:graph ?y. }";


	public static void main(String[] args) {
		
		
	
		R2RMLMapping mapping = new R2RMLMapping();
		
		String mappingFile = "/home/lavinpe/workspace/r2rml-editor/example_2.ttl";
		
		// We reason over the mapping to facilitate retrieval of the mappings
		Model data = FileManager.get().loadModel(mappingFile);
		
		
		
		
		// We construct triples to replace the shortcuts.
		data.add(QueryExecutionFactory.create(CONSTRUCTSMAPS_1, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTOMAPS_2, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTPMAPS_3, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTGMAPS_4, data).execConstruct());

		Model schema = ModelFactory.createDefaultModel();
		schema.read(R2RMLMappingFactory.class.getResourceAsStream("/r2rml.rdf"), null);
		
		// Create a model
		InfModel mappingmodel = ModelFactory.createRDFSModel(schema, data);
		
		
		// Check to see if we have functions that we can retrieve over HTTP
		List<RDFNode> functions = mappingmodel.listObjectsOfProperty(RRF.function).toList();
		for(RDFNode n : functions) {
			if(n.isURIResource()) {
				String uri = n.asResource().getURI();
				if(isValidURL(uri)) {
					
					System.out.println("Not valid URI");
					
					try {
						Model m = ModelFactory.createDefaultModel();
						m.read(uri);
						mappingmodel.add(m);
					} catch (Exception e) {
						
						System.out.println("Exp in isValidURL");

					}
				}

			}
		}
		
		
		
		
		// Look for the TriplesMaps
		List<Resource> list = mappingmodel.listSubjectsWithProperty(RDF.type, R2RML.TriplesMap).toList();

		if(list.isEmpty()) {

			System.out.println("R2RML Mapping File has no TriplesMaps.");
	
		}

		for(Resource tm : list) {
			
//			TriplesMap triplesMap = new TriplesMap(tm, baseIRI);
			TriplesMap triplesMap = new TriplesMap(tm, null);
			
			if(!triplesMap.preProcessAndValidate()) {

				System.out.println("Failed in preProcessAndValidate");
				
			}
			mapping.addTriplesMap(tm, triplesMap);
			
			Map<Resource, TriplesMap> mymap = mapping.getTriplesMaps();
			
			System.out.println("\n\nmymap size: " + mymap.size());
			
			System.out.println("mymap str : " + mymap.toString());
			
				
			
			
			
		}	
		
		
		
		
		
		
		
		
		
		
		
		
		
		System.out.println("\nEnd\n");
	}


	/**
	 * Small utility function  to test URL based on 
	 * http://stackoverflow.com/questions/1600291/validating-url-in-java
	 * 
	 * @param uri
	 * @return
	 */
	private static boolean isValidURL(String uri) {
		try {  
			URL u = new URL(uri);  
			u.toURI();
		} catch (MalformedURLException e) {  
			return false;  
		} catch (URISyntaxException e) {
			return false;
		}
		return true; 
	}

}
