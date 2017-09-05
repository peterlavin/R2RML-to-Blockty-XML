package r2rml.objects;

import java.util.Map;

import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.constants.CONST;
import r2rml.model.TriplesMap;

public class ProcessTriplesMaps {

	private Document xml;

	public ProcessTriplesMaps(Document xml) {

		this.xml = xml;

	}

	public Document processTriplesMaps(Map<Resource, TriplesMap> resMap) {
		
		for (Resource res : resMap.keySet()) {

			System.out.println(" +++ Processing " + res.getLocalName() + " +++\n");

			TriplesMap tmap = resMap.get(res);

			/*
			 * Create an empty XML element for this triplesmap Get the local
			 * name (within its namespace) for the triplesmap, insert to field
			 * in the triples map Block Element
			 */
			String triplesMapLocalName = res.getLocalName();
			Element tripmapStatementElem = createEmptyTripMapElement(triplesMapLocalName);

			/*
			 * Next process the Logical Table of this triplesmap
			 */
			ProcessLogicalTable plt = new ProcessLogicalTable(xml);

			xml = plt.processLogicalTable(tmap, tripmapStatementElem);

			/*
			 * Next up... Subject Map
			 */
			ProcessSubjectMap psm = new ProcessSubjectMap(xml);

			psm.processSubjectMap(tmap, tripmapStatementElem);
			
			/*
			 * And then, Predicate Object Maps
			 */

			ProcessPredicateObjectMaps ppom = new ProcessPredicateObjectMaps(xml);

			xml = ppom.processPredicateObjectMaps(tmap, tripmapStatementElem);

			/*
			 * Finally, get the root block element of the document and attach
			 * the triplesmap statement element to it
			 */
			Element ns = xml.getDocumentElement();
			Element rootBlockElement = (Element) ns.getFirstChild();

			rootBlockElement.appendChild(tripmapStatementElem);

		}

		return xml;

	}

	private Element createEmptyTripMapElement(String triplesMapLocalName) {

		Element tripmapStatementElem = xml.createElement(CONST.STATEMENT);
		tripmapStatementElem.setAttribute(CONST.NAME, CONST.TRIPLESMAP);

		/*
		 * Create a block element of type triplesmap and append to
		 * tripmapStatementElem Logical table, subj map and pred-obj map for
		 * this triplesmap all go in this element
		 */
		Element tripmapBlockElem = xml.createElement(CONST.BLOCK);
		tripmapBlockElem.setAttribute(CONST.TYPE, CONST.TRIPLESMAP);
		tripmapStatementElem.appendChild(tripmapBlockElem);

		/*
		 * Create a field element with attr name=TRIPLEMAPNAME, then append a
		 * text node to it containing the local name text argument
		 */
		Element tmapBlockFieldElm = xml.createElement(CONST.FIELD);
		tmapBlockFieldElm.setAttribute(CONST.NAME, CONST.TRIPLEMAPNAME_UC);
		tmapBlockFieldElm.appendChild(xml.createTextNode(triplesMapLocalName));
		tripmapBlockElem.appendChild(tmapBlockFieldElm);

		return tripmapStatementElem;
	}

}