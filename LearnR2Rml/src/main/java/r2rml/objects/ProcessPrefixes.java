package r2rml.objects;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.constants.CONST;
import xmlutilities.PrefixesToXML;

public class ProcessPrefixes {

	Document xml;

	public ProcessPrefixes(Document xml) {

		this.xml = xml;

	}

	public Document processPrefixes(Map<String, String> pmap) {

		/*
		 * R2RML will have at least one prefix (rr=http://www.w3.org/ns/r2rml#)
		 * plus any added ones. The r2rml one is ignored, all subsequent ones
		 * are added here
		 *
		 * This code puts each prefix in its own <statement name="mapping">
		 * element, no <next> elements are used.
		 * 
		 * A (Java) Map is used so order may not be preserved.
		 */

		for (Entry<String, String> value : pmap.entrySet()) {

			String prefix = value.getKey().toString();
			String prefixURI = value.getValue().toString();

			/*
			 * Filter out the prefix... http://www.w3.org/ns/r2rml
			 */
			if (!prefixURI.contains(CONST.R2RML_NS)) {

				insertPrefix(prefix, prefixURI);

			}

		}

		return xml;
	}
	
		
	
	
	/*
	 * Helper methods to insert prefixes to XL
	 */
	public Document insertPrefix(String prefix, String prefixURI) {

		/*
		 * Get the ns element of the xml doc
		 */
		Element ns = xml.getDocumentElement();

		/*
		 * Get the root block element of the document
		 */
		Element rootBlockElement = (Element) ns.getFirstChild();

		/*
		 * Sanity check to ensure the root block element is being used
		 */
		if (rootBlockElement.hasAttribute(CONST.DELETABLE) && rootBlockElement.hasAttribute(CONST.TYPE)) {

			/*
			 * Create a statement element for prefixes
			 */
			Element prefixStatementElm = xml.createElement(CONST.STATEMENT);
			prefixStatementElm.setAttribute(CONST.NAME, CONST.MAPPING);

			/*
			 * Append this to the root block element
			 */
			rootBlockElement.appendChild(prefixStatementElm);

			/*
			 * Create a block element for prefixes
			 */
			Element prefixBlockElem = makePrefixElement(prefix, prefixURI);

			prefixStatementElm.appendChild(prefixBlockElem);

		}
		else{
			
			System.out.println("Something wrong, appending prefixes to root block element went wrong");

		}
		return xml;

	}

	/*
	 * Creates a block element for a prefix, can be used for first and
	 * subsequent prefixes.
	 */
	private Element makePrefixElement(String prefix, String prefixURI) {

		/*
		 * Create a block element with attr type=predefinedprefix
		 */
		Element prefixBlockElem = xml.createElement(CONST.BLOCK);
		prefixBlockElem.setAttribute("type", "prefix");

		/*
		 * Create a field element with attr name=PREFIX, then append a text node
		 * to it containing the actual prefix, e.g. rr or foaf
		 */
		Element prefixBlockFieldElm = xml.createElement("field");
		prefixBlockFieldElm.setAttribute(CONST.NAME, CONST.PREFIX_UC);
		prefixBlockFieldElm.appendChild(xml.createTextNode(prefix));

		/*
		 * Create a field element with attr name=URI, then append a text node
		 * to it containing the URI, e.g. http://something/...
		 */
		Element prefixBlockFieldUriElm = xml.createElement(CONST.FIELD);		
		prefixBlockFieldUriElm.setAttribute(CONST.NAME, CONST.URI);
		prefixBlockFieldUriElm.appendChild(xml.createTextNode(prefixURI));


		/*
		 * Append the elements to the block element, return
		 */
		prefixBlockElem.appendChild(prefixBlockFieldElm);
		prefixBlockElem.appendChild(prefixBlockFieldUriElm);
		

		return prefixBlockElem;

	}
	
}
