package com.learn.R2rmlToXML;

import java.util.Map;
import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;

import r2rml.model.R2RMLMapping;
import r2rml.model.R2RMLMappingFactory;
import r2rml.model.TriplesMap;
import r2rml.objects.ProcessPrefixes;
import r2rml.objects.ProcessTriplesMaps;
import xmlutilities.EmptyXMLDocument;
import xmlutilities.PrettyPrintXML;

public class RunImportMapping {

	// private static String CONSTRUCTSMAPS = "PREFIX rr:
	// <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:subjectMap [ rr:constant
	// ?y ]. } WHERE { ?x rr:subject ?y. }";
	// private static String CONSTRUCTOMAPS = "PREFIX rr:
	// <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:objectMap [ rr:constant
	// ?y ]. } WHERE { ?x rr:object ?y. }";
	// private static String CONSTRUCTPMAPS = "PREFIX rr:
	// <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:predicateMap [
	// rr:constant ?y ]. } WHERE { ?x rr:predicate ?y. }";
	// private static String CONSTRUCTGMAPS = "PREFIX rr:
	// <http://www.w3.org/ns/r2rml#> CONSTRUCT { ?x rr:graphMap [ rr:constant ?y
	// ]. } WHERE { ?x rr:graph ?y. }";

	public static void main(String[] args) {

		String mappingFile = "";
		mappingFile = "samples/example_2.ttl";
		mappingFile = "samples/cso_has_sql.ttl";
		mappingFile = "samples/2_tm_with_class_iri.ttl";
		mappingFile = "samples/3_tms_class_iri_none.ttl";
		mappingFile = "samples/4_tms_class_iri_blank_none.ttl";
		mappingFile = "samples/various_testing.ttl";
		mappingFile = "samples/various_testing.ttl";

		
		/*
		 * Using code in r2rml model project (by Christophe Debruyne) to get a
		 * full data model for the input r2rml mapping
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

		for (Resource tm0 : resMap.keySet()) {
			pmap = tm0.getModel().getNsPrefixMap();
			break;
		}

		/*
		 * We have enough now to create the empty XML file and to put the
		 * predefined prefix(s) into it
		 */
		EmptyXMLDocument exd = new EmptyXMLDocument();

		Document xml = exd.createBlankDocument();

		/*
		 * Insert the prefix(s) into xml
		 */
		ProcessPrefixes pp = new ProcessPrefixes(xml);
		pp.processPrefixes(pmap);

		ProcessTriplesMaps ptm = new ProcessTriplesMaps(xml);
		

		ptm.processTriplesMaps(resMap);

		/*
		 * Un/comment for printing final output
		 * 
		 * For real world use, write this output to file on disk
		 */
		System.out.println("\nFINAL DOCUMENT...\n");
		PrettyPrintXML.printDocument(xml);

	}

}
