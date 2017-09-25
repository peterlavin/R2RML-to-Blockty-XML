package r2rml.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.constants.CONST;
import r2rml.model.TriplesMap;
import xmlutilities.PrettyPrintXML;

public class ProcessTriplesMaps {

	private Document xml;

	public ProcessTriplesMaps(Document xml) {

		this.xml = xml;

	}

	public void processTriplesMaps(Map<Resource, TriplesMap> resMap) {

		/*
		 * In order to have a 'number' (i) value during iteration, it is
		 * necessary to iterate over the map using a for(;;) loop
		 */

		Set<Resource> set = resMap.keySet();

		List<Resource> resList = new ArrayList<Resource>(set);

		Element basicTripmapBlockElem = null;
		Element savedTripmapBlockElem = null;

		/*
		 * Need to iterate in reverse over this list in order to preserve the
		 * order of the TMs for the output XML
		 */
		for (int i = (resList.size() - 1); i >= 0; i--) {

			Resource res = resList.get(i);

			TriplesMap tmap = resMap.get(res);
			String triplesMapLocalName = res.getLocalName();

			// NB, iterating in reverse
			if (i > 0) {
				/*
				 * One of multiple triplesmaps but not the last one
				 */

				basicTripmapBlockElem = createBasicTripMapBlock(tmap, triplesMapLocalName);

				if (savedTripmapBlockElem != null) {
					basicTripmapBlockElem.appendChild(savedTripmapBlockElem);
				}

				basicTripmapBlockElem = putBlockInNext(basicTripmapBlockElem);

				savedTripmapBlockElem = basicTripmapBlockElem;

				if (i == 0) {
					System.out.println("0... " + triplesMapLocalName);
					PrettyPrintXML.printElement(basicTripmapBlockElem);
				}

				// NB, iterating in reverse
			} else if (i == 0) {
				/*
				 * Only triplesmap, or the last of multiple triplesmaps
				 */

				basicTripmapBlockElem = createBasicTripMapBlock(tmap, triplesMapLocalName);

				/*
				 * If this is the last of many, append previously created inner
				 * triplesmaps. NB, no <is added here>
				 */
				if (savedTripmapBlockElem != null) {

					basicTripmapBlockElem.appendChild(savedTripmapBlockElem);

				}

				System.out.println("last... " + triplesMapLocalName);
				PrettyPrintXML.printElement(basicTripmapBlockElem);

			}

			/*
			 * Put all the triplesmaps in a <statement> element
			 */
			Element tripmapStatementElem = xml.createElement(CONST.STATEMENT);
			tripmapStatementElem.setAttribute(CONST.NAME, CONST.TRIPLESMAP);

			if (basicTripmapBlockElem != null) {

				tripmapStatementElem.appendChild(basicTripmapBlockElem);

			} else {

				System.out.println("Triplesmap block is empty, something is wrong");

			}

			/*
			 * Finally, get the root block element of the document and attach
			 * the triplesmap statement element to it
			 */
			Element ns = xml.getDocumentElement();
			Element rootBlockElement = (Element) ns.getFirstChild();

			/*
			 * Sanity check to ensure the root block element is being used
			 */
			if (rootBlockElement.hasAttribute(CONST.DELETABLE) && rootBlockElement.hasAttribute(CONST.TYPE)) {

				rootBlockElement.appendChild(tripmapStatementElem);

			} else {

				System.out.println("Something wrong, appending triplesmaps to root block element went wrong");

			}

		}

	}

	/**
	 * Create an empty TriplesMap element
	 * 
	 * @param triplesMapLocalName
	 * @return
	 */
	private Element createBasicTripMapBlock(TriplesMap tmap, String triplesMapLocalName) {

		/*
		 * First, create the (inner) field element with attr name=TRIPLEMAPNAME,
		 * then append a text node to it containing the local name text argument
		 */
		Element tripMapFieldElm = xml.createElement(CONST.FIELD);
		tripMapFieldElm.setAttribute(CONST.NAME, CONST.TRIPLEMAPNAME_UC);
		tripMapFieldElm.appendChild(xml.createTextNode(triplesMapLocalName));

		/*
		 * Next, surround this with a <block> element
		 */
		Element tripmapBlockElem = xml.createElement(CONST.BLOCK);
		tripmapBlockElem.setAttribute(CONST.TYPE, CONST.TRIPLESMAP);

		tripmapBlockElem.appendChild(tripMapFieldElm);

		////
		/*
		 * Next process the Logical Table of this triplesmap
		 */
		ProcessLogicalTable plt = new ProcessLogicalTable(xml);

		plt.processLogicalTable(tmap, tripmapBlockElem);

		/*
		 * Next up... Subject Map. There can only be one Subj Map per
		 * TriplesMap.
		 */
		ProcessSubjectMap psm = new ProcessSubjectMap(xml);
		psm.processSubjectMap(tmap, tripmapBlockElem);

		/*
		 * On to Predicate Object Map(s). For a given TriplesMap, there may be
		 * more than one Predicate Object Map and these must be nested in <next>
		 * elements in the XML output.
		 */
		ProcessPredicateObjectMaps ppom = new ProcessPredicateObjectMaps(xml);
		ppom.processPredicateObjectMaps(tmap, tripmapBlockElem);

		return tripmapBlockElem;
	}

	/*
	 * Takes any block element and puts it in a <next></next> element
	 */
	private Element putBlockInNext(Element blockElm) {

		Element nextElement = xml.createElement(CONST.NEXT);

		nextElement.appendChild(blockElm);

		return nextElement;

	}

}