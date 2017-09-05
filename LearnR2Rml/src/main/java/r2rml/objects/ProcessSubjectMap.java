package r2rml.objects;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import r2rml.constants.CONST;
import r2rml.model.SubjectMap;
import r2rml.model.TriplesMap;
import xmlutilities.PrettyPrintXML;

public class ProcessSubjectMap {

	private Document xml;

	public ProcessSubjectMap(Document xml) {

		this.xml = xml;

	}

	public void processSubjectMap(TriplesMap tmap, Element tripmapStatementElem) {

		SubjectMap subjmap = tmap.getSubjectMap();

		String termMapTypeStr = "";
		String termMapVariableStr = "";

		/*
		 * The subject map may be of type template, column or constant, all
		 * three are checked and the requred details found
		 */
		if (subjmap.isTemplateValuedTermMap()) {

			termMapTypeStr = CONST.TEMPLATE_UC;
			termMapVariableStr = subjmap.getTemplate().toString();

		} else if (subjmap.isColumnValuedTermMap()) {

			termMapTypeStr = CONST.COLUMN_UC;
			termMapVariableStr = subjmap.getColumn();

		} else if (subjmap.isConstantValuedTermMap()) {

			termMapTypeStr = CONST.CONSTANT_UC;
			termMapVariableStr = subjmap.getConstant().toString();

		} else {
			System.out.println(
					"Something went wrong when determining TEMPLATE/COLUMN/ " + "CONSTANT type for a subject map");
		}

		/*
		 * Now that all variables are determined, create the subject map
		 * statement element and add its block and field elements
		 * 
		 * The Element arg passed in here is the overall statement element for a
		 * particular triplesmap. The subject map statement element and its
		 * parts need to be inserted into the triplesmap block element within
		 * this statement element
		 */	
		insertBasicSubjMap(tripmapStatementElem, termMapTypeStr, termMapVariableStr);

		/*
		 * If there is anything other than the default TermType (i.e not IRI),
		 * or if there is a class present, then a TermType statement element is
		 * needed. This goes inside the SubjectMap statement/block Elements.
		 * 
		 * Where there is a TermMap required, this is particular to this
		 * SubjectMap, which is particular to its TriplesMap. Therefore, the
		 * TriplesMap statement element passed into this class is passed to a
		 * separate (Java) Class for processing.
		 * 
		 * Logic for determining if a TermMap element is needed is done in the
		 * ProcessSmTermMap Class.
		 */

		Element tripMapBlockElem = (Element) tripmapStatementElem.getFirstChild();

		/*
		 * Get all the statement Elements in this, there may be ones for
		 * logicaltable and subjectmap at this point
		 */
		NodeList nodeList = tripMapBlockElem.getElementsByTagName(CONST.STATEMENT);

		Element subjMapStatementElm = null;
		for (int i = 0; i < nodeList.getLength(); i++) {

			Element e = (Element) nodeList.item(i);
			if (e.getAttribute(CONST.NAME).equals(CONST.SUBJECTMAP)) {
				subjMapStatementElm = e;
				break;
			}

		}

		/*
		 * ProcessTermMap is called, even if not needed, this is determined in
		 * the ProcessTermMap class.
		 */
		ProcessSmTermMap ptm = new ProcessSmTermMap(xml);
		ptm.processTermMap(subjMapStatementElm, subjmap);

		System.out.println("FINAL TRIPLESMAP STATEMENT...");
		PrettyPrintXML.printElement(tripmapStatementElem);
		
		/*
		 * ProcessSmGraphMap is called, the required actions (if any)
		 * are determined in the ProcessSmGraphMap class. 
		 */
		
		ProcessSmGraphMap pgm = new ProcessSmGraphMap(xml);
		
		pgm.processSmGraphMap(subjMapStatementElm, subjmap);

	}

	/*
	 * Adds a basic SubjectMap element with no elements for a TermMap. This
	 * assumes no classes and the default TermType, i.e. IRI.
	 */
	private void insertBasicSubjMap(Element tripmapStatementElem, String termMapTypeStr, String termMapVariableStr) {

		/*
		 * In reverse order, create the required elements, then append
		 */

		// TERMMAP field, <field name="TERMMAP">XXXXXXXX</field>
		Element fieldElementTermMap = xml.createElement(CONST.FIELD);
		fieldElementTermMap.setAttribute(CONST.NAME, CONST.TERMMAP_UC);
		fieldElementTermMap.appendChild(xml.createTextNode(termMapTypeStr));

		// TERMMAPVALUE field, <field
		// name="TERMMAPVALUE">http://example.com/example/{ex}</field>
		Element subjectMapFieldElementTermMapValue = xml.createElement("field");
		subjectMapFieldElementTermMapValue.setAttribute(CONST.NAME, CONST.TERMMAPVALUE_UC);
		subjectMapFieldElementTermMapValue.appendChild(xml.createTextNode(termMapVariableStr));

		/*
		 * Create a subject block element to hold the above elements as they are
		 * always present, later additions may not
		 */
		Element blockElementSubjMap = xml.createElement(CONST.BLOCK);
		blockElementSubjMap.setAttribute(CONST.TYPE, CONST.SUBJECTMAP);
		blockElementSubjMap.appendChild(fieldElementTermMap);
		blockElementSubjMap.appendChild(subjectMapFieldElementTermMapValue);

		/*
		 * Create a subjectmap statement to hold all of the above
		 */

		Element subjectMapStatementElement = xml.createElement(CONST.STATEMENT);
		subjectMapStatementElement.setAttribute(CONST.NAME, CONST.SUBJECTMAP);
		subjectMapStatementElement.appendChild(blockElementSubjMap);

		/*
		 * Append all of the above to the triples map statement element for this
		 * particular triplesmap
		 */
		Element tripMapBlockElem = (Element) tripmapStatementElem.getFirstChild();
		tripMapBlockElem.appendChild(subjectMapStatementElement);

	}

}
