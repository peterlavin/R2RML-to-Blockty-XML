package com.learn.LearnR2Rml;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.w3c.dom.Document;

// Imported from local m2 repo, Christophe's jar
import r2rml.engine.R2RML;
import r2rml.model.LogicalTable;
import r2rml.model.ObjectMap;
import r2rml.model.PredicateMap;
import r2rml.model.PredicateObjectMap;
import r2rml.model.R2RMLMapping;
import r2rml.model.R2RMLMappingFactory;
import r2rml.model.SubjectMap;
import r2rml.model.TriplesMap;
import r2rml.objects.ProcessLogicalTable;
import r2rml.objects.ProcessPrefixes;
import r2rml.objects.ProcessTriplesMaps;
import xmlutilities.EmptyXMLDocument;
import xmlutilities.PrettyPrintXML;



public class RunImportMapping {

	private static String CONSTRUCTSMAPS = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:subjectMap [ rr:constant ?y ]. } WHERE { ?x rr:subject ?y. }";
	private static String CONSTRUCTOMAPS = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:objectMap [ rr:constant ?y ]. } WHERE { ?x rr:object ?y. }";
	private static String CONSTRUCTPMAPS = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:predicateMap [ rr:constant ?y ]. } WHERE { ?x rr:predicate ?y. }";
	private static String CONSTRUCTGMAPS = "PREFIX rr: <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:graphMap [ rr:constant ?y ]. } WHERE { ?x rr:graph ?y. }";


	public static void main(String[] args) {
		

		
		String mappingFile = "";
		mappingFile = "/home/lavinpe/workspace/r2rml-editor/samples/example_2.ttl";
		mappingFile = "/home/lavinpe/workspace/r2rml-editor/samples/cso_has_sql.ttl";
		mappingFile = "/home/lavinpe/workspace/r2rml-editor/samples/2_tm_with_class_iri.ttl";
		mappingFile = "/home/lavinpe/workspace/r2rml-editor/samples/3_tms_class_iri_none.ttl";
		mappingFile = "/home/lavinpe/workspace/r2rml-editor/samples/4_tms_class_iri_blank_none.ttl";
		mappingFile = "/home/lavinpe/workspace/r2rml-editor/samples/various_testing.ttl";
		
		//mappingFile = "/home/lavinpe/Downloads/r2rml-use-cases/mapping6.ttl";
															
		
		
		/*
		 *  Using code in r2rml model project (by Christophe Debruyne)
		 *  to get a full data model for the input r2rml mapping
		 */
		R2RMLMapping map = R2RMLMappingFactory.createR2RMLMapping(mappingFile, null);
		
		System.out.println("\n========================================================\n\n");
		
		/*
		 * Get a map of all the triplesmaps in the model
		 */
		Map<Resource, TriplesMap> resMap = map.getTriplesMaps();
		/*
		 * Once we have *any* triplesmap, use its model to yield its prefix(s)
		 * Break after one as only one is need
		 */
		
		Map<String, String> pmap = null;
		
		for(Resource tm0 : resMap.keySet()){
			pmap = tm0.getModel().getNsPrefixMap();			
			break;
		}

		/*
		 * We have enough now to create the empty XML file and
		 * to put the predefined prefix(s) into it
		 */
		EmptyXMLDocument exd = new EmptyXMLDocument();
		
		Document xml = exd.createBlankDocument();
		
		/*
		 * Insert the prefix(s) into xml
		 */
		ProcessPrefixes pp = new ProcessPrefixes(xml);
		xml = pp.processPrefixes(pmap);
		
		/*
		 * Now, iterate again across *all* available triplesmap
		 * Counter vairable is for information only
		 */
		int triplesMapCount = 0;
		int totalTriplesMapCount = resMap.size();
		
		///////////////////////////////////////////// Sorting map tests,
		// Maps are often ordered. XML recreates reversed visuals in 
		// blockty - need to use <next blocks>
//		List<Resource> keys = new ArrayList<Resource>(resMap.keySet());
//		
//		System.out.println("Printing list.. " + keys.size() + "\n");
//		
//		for(Resource key: keys){
//			
//			System.out.println(key.getLocalName());
//			
//		}
//		
//		
//		System.out.println("Printing map.. " + keys.size() + "\n");
//		
//		for (Resource res : resMap.keySet()) {
//			System.out.println(res.getLocalName());
//		}
//		
//		System.exit(0);	
		//////////////////////////////////////////////
		
		ProcessTriplesMaps ptm = new ProcessTriplesMaps(xml);

		xml = ptm.processTriplesMaps(resMap);
		
		
		
		
		
		
/*
 * Un/comment for printing		
 */
	
		System.out.println("\nFINAL DOCUMENT...\n");
		PrettyPrintXML.printDocument(xml);

	
	
	
		System.exit(0);	
		
		
		
		
		
		
		
		
		
		for(Resource i : resMap.keySet()) {

			TriplesMap tm = resMap.get(i);
			
			/*
			 * Now, we have enough to create a TM element
			 * 
			 * Get the Name of the triplesmap
			 */
			System.out.println("TRIPLEMAPNAME in XML " + i.getLocalName());
			
			System.out.println();
			System.out.println();
			
			
			/*
			 * End of processing subject maps
			 * 
			 * Next, deal with Predicate Object Maps.
			 * First, get an array list of POMs
			 */
			List<PredicateObjectMap> pomArrList = tm.getPredicateObjectMaps();
			
			// May be more than one POM, 2nd and subsequent POMs are in <next> elements
			/*
			 * Iterate over this array list
			 */
			for(PredicateObjectMap pom: pomArrList){
				
				/*
				 * XML XML XML XML XML XML XML XML XML XML XML XML Make the POM emement/node here and populate
				 */
				
				
				/*
				 * First, process the predicate map for this pom
				 */
				List<PredicateMap> pmList = pom.getPredicateMaps();
				
				for(PredicateMap pm: pmList){
					
					
					
					
					
					
					
					
					/*
					 * As above, predicate object maps also may be of type 
					 * template, column or constant, all three are checked
					 * 
					 */
					// getting "http://xmlns.com/foaf/0.1/name", need "foaf:name"
					if(pm.isTemplateValuedTermMap()){
						// TODO, do stuff for template
						
					}
					else if(pm.isColumnValuedTermMap()){
						// TODO, do stuff for col
						
					}
					else if(pm.isConstantValuedTermMap()){
	
						
						
						System.out.println("Constant Valued Term Map found in predicate map");
						
						Resource rn = (Resource) pm.getConstant();
						
						String prefix = null;
						String suffix = null;
						
						for(String key : rn.getModel().getNsPrefixMap().keySet()) {
							String prefixuri = rn.getModel().getNsPrefixMap().get(key);
							if(rn.getURI().contains(prefixuri)) {
								prefix = key;
								suffix = rn.getURI().replace(prefixuri, "");
								break;
							}
						}
						
						System.out.println("TERMMAP type for predicate map is \t\tconstant");
						System.out.println("TERMMAPVALUE is\t\t\t" + prefix + ":" + suffix);

						
					}
					else{
						System.out.println("Something went wrong when determining TEMPLATE/COLUMN/ "
								+ "CONSTANT type for a predicate object map in " + i.getLocalName());
					}
					
	 			}
				
				/*
				 * Next, process the object map for this pom
				 */
				List<ObjectMap> omList = pom.getObjectMaps();
				
					System.out.println("\nNumber of Object Maps in omList is:  " + omList.size());

					for(ObjectMap om: omList){
						
						/*
						 * XML XML XML XML XML XML XML XML XML XML XML XML Make the OM emement/node here and populate
						 */
						
						System.out.println();
						
						/*
						 * As above, object maps also may be of type 
						 * template, column or constant, all three are checked
						 */
						
						//System.out.println(om.getTermType());
						
						if(om.isTemplateValuedTermMap()){
							System.out.println("TERMMAP type for object map is \t\ttemplate");
							// TODO, use this
							om.getTemplate();
						}
						else if(om.isColumnValuedTermMap()){
							
							System.out.println("TERMMAP type for object map is \t\tcolumn");
							System.out.println("TERMMAPVALUE\t\t\t\t" + om.getColumn());
						}
						else if(om.isConstantValuedTermMap()){
							System.out.println("TERMMAP type for object map is \t\tconstant");
							// TODO, use this
							om.getConstant();
						}
					    						
					}
					
				// TODO get graphMaps, not necessary yet
				pom.getGraphMaps();
				
				/*
				 * After this, the next triples map (if any is processed)
				 */
	
			}
			
			triplesMapCount++;
			System.out.println("\n\n---------------------------------------");
			System.out.println("Triplesmap number " + triplesMapCount + " of " + totalTriplesMapCount + " is processed.");
			System.out.println("---------------------------------------\n\n");

		}
		
		
		

	

		
		String mappingFile2 = "/home/lavinpe/workspace/r2rml-editor/samples/samplesexample_2.ttl";
		
		// We reason over the mapping to facilitate retrieval of the mappings
		Model data = FileManager.get().loadModel(mappingFile);
		
		
		
		
		// We construct triples to replace the shortcuts.
		data.add(QueryExecutionFactory.create(CONSTRUCTSMAPS, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTOMAPS, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTPMAPS, data).execConstruct());
		data.add(QueryExecutionFactory.create(CONSTRUCTGMAPS, data).execConstruct());

		Model schema = ModelFactory.createDefaultModel();
		schema.read(R2RMLMappingFactory.class.getResourceAsStream("/r2rml.rdf"), null);
		
		
		
		// Create a information model using the .ttl and the r2rml.rdf file
		// this contains everythign that is needed
		InfModel mappingmodel = ModelFactory.createRDFSModel(schema, data);
		
		
		
		// List the statements in the info model
		StmtIterator statements2 = mappingmodel.listStatements();
		
		// gets the parts for the prefix block, prints out the number of prefixes (TODO, for info only)
		Statement stm2 = statements2.nextStatement();
		Statement stm = null;
		System.out.println("Size of NsPrefixMap: " + stm.getSubject().getModel().getNsPrefixMap().size());
		
		// Prints all the prefixes in a map
		//System.out.println("\nStm get prefixes: " + statements.next().getSubject().getModel().getNsPrefixMap() + "\n");

		// Prints out all prefixes defined in the input R2RML
		Map<String, String> pmap2 = statements2.nextStatement().getSubject().getModel().getNsPrefixMap();
		
		// Prints all prefixed, TODO check with AC if the statement name attribute is always "mapping" here
		// if so, we have enough to create the first statement and its blocks (as it is kinda standard for all TODO ??? confirm this)
		// We are only interested in the 2nd one upwards
		System.out.println();
		for(Entry<String, String> value : pmap2.entrySet()){
			
			System.out.println("Prefix:  " + value);
			
		}
		System.out.println();
		
		// On to the triplesmap, 2nd statement/block within the main block.
		// Again, getting a list from the overall InfModel (mappingmodel)				
		ResIterator ri = mappingmodel.listSubjectsWithProperty(RDF.type, R2RML.TriplesMap);

		while(ri.hasNext()) {
			
			Resource triplesmap = ri.next(); // should only ever be one
			
			
			
			
			// create block for triples map TODO
			System.out.println("TRIPLEMAPNAME: " + triplesmap.getLocalName());
			System.out.println();
			// System.out.println("tm name spc : " + triplesmap.getNameSpace()); // not needed
			
			//At the same place as but using InfModel, same object???
			
			

			
			
			
			
			
			
									
			// find the logical table in this triplesmap
			StmtIterator stl = triplesmap.listProperties(R2RML.logicalTable); // find all logical tables
			
			int tm_count = 0;
			
			while(stl.hasNext()) {
				
				
				Resource logicaltable = (Resource) stl.next().getObject();
				// create block for logical table

				//ProcessLogicalTable plt = new ProcessLogicalTable(xml);
				
				//plt.processLogicalTable(logicaltable);
				
				tm_count++;			
				
				
				if(logicaltable.hasProperty(R2RML.tableName)) {
					// add tablename to block

					
					StmtIterator logTblList = logicaltable.listProperties(R2RML.tableName);
					
					int lt_count = 0;
						while(logTblList.hasNext()){
							
							Statement s = logTblList.next();
							
							lt_count++;
							System.out.println("<mutation sql=\"" + s.getObject() + "\"/>"); ////////////////////// useful
							
							// TODO, both of these return employees, which is correct?
							System.out.println("tl: " + s.getString()); ///////////////////////// uselful
												
							
							// System.out.println("pred: " + s.getPredicate()); ////////////////////// not needed
							
														
						}
						
						System.out.println("lt_count " + lt_count);
					
					
				} else if (logicaltable.hasProperty(R2RML.sqlQuery)) {
					// add sqlquery to block
					
					// TODO 2nd scenario, needs similar code
					// TODO 2nd scenario, needs similar code
					// TODO 2nd scenario, needs similar code
					// TODO 2nd scenario, needs similar code
					
				}
			}
			
			
			
			
			
			
			
			
			
			System.out.println("tm_count: " + tm_count);
			
			
			
			// Still in the same block, next find subjectmap
			StmtIterator subjectMapList = triplesmap.listProperties(R2RML.subjectMap);
			
			
			
			
			
			int subMapListCount = 0;
			while(subjectMapList.hasNext()){
				
				Statement subjectMap = subjectMapList.nextStatement();
				subMapListCount ++;
				
						
				// need to get TERMMAP... TEMPLATE
				// need to get TERMMAPVALUE... http://example.org/{id}
				System.out.println("Sub Obj Map " + subMapListCount + ": " + subjectMap.getSubject() + " " + subjectMap.getPredicate() + " " + subjectMap.getObject() );    //(R2RML.TermMap));
				
				StmtIterator blah = subjectMap.getObject().asResource().listProperties(R2RML.template);
				if(blah.hasNext()) {
					// we have a template valued subjectmap
					String template = blah.next().getObject().asLiteral().toString();
				}
				
				blah = subjectMap.getObject().asResource().listProperties(R2RML.constant);
				if(blah.hasNext()) {
					Resource constant = blah.next().getObject().asResource();
				}
				
				blah = subjectMap.getObject().asResource().listProperties(R2RML.column);
				if(blah.hasNext()) {
					// we have a column valued subjectmap
					String column = blah.next().getObject().asLiteral().toString();
				}
		
				
				System.out.println("Sub Obj Map " + subMapListCount + ": " + subjectMap.getBag().listProperties()   );    //(R2RML.TermMap));
				
			}
				
			System.out.println();
			
			// Still in the same block, find predicate object maps
			StmtIterator predObjMapList = triplesmap.listProperties(R2RML.predicateObjectMap);
			
			int predObMapCount = 0;
			while(predObjMapList.hasNext()){
				
				Statement predObjMap = predObjMapList.nextStatement();
				predObMapCount++;
										
				System.out.println("Pred Ob Map Count " + predObMapCount + ": ");
				
				List list2 = predObjMap.getBag().listProperties().toList();
				
				System.out.println("Size of list2... " + list2.size());
				for (int i = 0; i < list2.size(); i++) {
					
					System.out.println(list2.get(i));
				}
				
			}		
			
			
			
			
			// find predicate object reference maps
			// StmtIterator predObjRefMapList = triplesmap.listProperties(R2RML..);
		}
		
		
		
		System.exit(0);
		
		
		
		System.out.println("\n\n==================== ====================\n\n");
		

		// Write out everything in the mapping model created (and count them)
		
//		int s_count = 0;
//		
//		while(statements.hasNext()){
//			
//			Statement statement = statements.next();
//			System.out.print(statement.getSubject() + " ");
//			System.out.print(statement.getPredicate()+ " ");
//			System.out.print(statement.getObject()+ "\n\n");
//			s_count++;
//		}
//
//		System.out.println("\n\nstcount: " + s_count +"\n\n");
		
		System.exit(0);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		// Check to see if we have functions that we can retrieve over HTTP
//		List<RDFNode> functions = mappingmodel.listObjectsOfProperty(RRF.function).toList();
//		for(RDFNode n : functions) {
//			if(n.isURIResource()) {
//				String uri = n.asResource().getURI();
//				if(isValidURL(uri)) {
//					
//					System.out.println("Not valid URI");
//					
//					try {
//						Model m = ModelFactory.createDefaultModel();
//						m.read(uri);
//						mappingmodel.add(m);
//					} catch (Exception e) {
//						
//						System.out.println("Exp in isValidURL");
//
//					}
//				}
//
//			}
//		}
		
		
	    // Used at end of file
		R2RMLMapping mapping = new R2RMLMapping();
		
		
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
			
			System.out.println("mymap values : " + mymap.values());

			
		}	
		
		
		
		
		
		
		
		
		
		
		
		
		
		System.out.println("\nEnd\n");
	}

	
//	/*
//	 * Create a InfModel to retrieve the prefix(s) for the mapping
//	 * 
//	 */
//	private static InfModel createLocalInfModel(String mappingFile){
//		
//		
//		// We reason over the mapping to facilitate retrieval of the mappings
//		Model data = FileManager.get().loadModel(mappingFile);
//				
//		// We construct triples to replace the shortcuts.
//		data.add(QueryExecutionFactory.create(CONSTRUCTSMAPS, data).execConstruct());
//		data.add(QueryExecutionFactory.create(CONSTRUCTOMAPS, data).execConstruct());
//		data.add(QueryExecutionFactory.create(CONSTRUCTPMAPS, data).execConstruct());
//		data.add(QueryExecutionFactory.create(CONSTRUCTGMAPS, data).execConstruct());
//
//		Model schema = ModelFactory.createDefaultModel();
//		schema.read(R2RMLMappingFactory.class.getResourceAsStream("/r2rml.rdf"), null);
//		
//		// Create a information model using the .ttl and the r2rml.rdf file
//		// this contains everythign that is needed
//		InfModel infModel = ModelFactory.createRDFSModel(schema, data);
//		
//		///// TODO, add verification/validation code here from R2RMLMappingFactory class
//				
//		return infModel;
//	}
//	
//	
//	
//	
//
//	/**
//	 * Small utility function  to test URL based on 
//	 * http://stackoverflow.com/questions/1600291/validating-url-in-java
//	 * 
//	 * @param uri
//	 * @return
//	 */
//	private static boolean isValidURL(String uri) {
//		try {  
//			URL u = new URL(uri);  
//			u.toURI();
//		} catch (MalformedURLException e) {  
//			return false;  
//		} catch (URISyntaxException e) {
//			return false;
//		}
//		return true; 
//	}

}
