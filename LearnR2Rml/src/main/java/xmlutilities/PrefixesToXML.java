package xmlutilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PrefixesToXML {

	Document xml;

	public PrefixesToXML(Document xml) {

		this.xml = xml;

	}

//	public Document insertPrefix(String prefix, String prefixURI) {
//
//		/*
//		 * Get the ns element of the xml doc
//		 */
//		Element ns = xml.getDocumentElement();
//
//		/*
//		 * Get the root block element of the document
//		 */
//		Element rootBlockElement = (Element) ns.getFirstChild();
//
//		/*
//		 * Sanity check to ensure the root block element is being used
//		 */
//		if (rootBlockElement.hasAttribute("deletable") && rootBlockElement.hasAttribute("type")) {
//
//			/*
//			 * Create a statement element for prefixes
//			 */
//			Element prefixStatementElm = xml.createElement("statement");
//			prefixStatementElm.setAttribute("name", "mapping");
//
//			/*
//			 * Append this to the root block element
//			 */
//			rootBlockElement.appendChild(prefixStatementElm);
//
//			/*
//			 * Create a block element for prefixes
//			 */
//			Element prefixBlockElem = makePrefixElement(prefix, prefixURI);
//
//			prefixStatementElm.appendChild(prefixBlockElem);
//
//		}
//		else{
//			// TODO, catch this, throw exception
//		}
//		return xml;
//
//	}

	/*
	 * Creates a block element for a prefix, can be used for first and
	 * subsequent prefixes.
	 */
//	private Element makePrefixElement(String prefix, String prefixURI) {
//
//		/*
//		 * Create a block element with attr type=predefinedprefix
//		 */
//		Element prefixBlockElem = xml.createElement("block");
//		prefixBlockElem.setAttribute("type", "prefix");
//
//		/*
//		 * Create a field element with attr name=PREFIX, then append a text node
//		 * to it containing the actual prefix, e.g. rr or foaf
//		 */
//		Element prefixBlockFieldElm = xml.createElement("field");
//		prefixBlockFieldElm.setAttribute("name", "PREFIX");
//		prefixBlockFieldElm.appendChild(xml.createTextNode(prefix));
//
//		/*
//		 * Create a field element with attr name=URI, then append a text node
//		 * to it containing the URI, e.g. http://something/...
//		 */
//		Element prefixBlockFieldUriElm = xml.createElement("field");		
//		prefixBlockFieldUriElm.setAttribute("name", "URI");
//		prefixBlockFieldUriElm.appendChild(xml.createTextNode(prefixURI));
//
//
//		/*
//		 * Append the elements to the block element, return
//		 */
//		prefixBlockElem.appendChild(prefixBlockFieldElm);
//		prefixBlockElem.appendChild(prefixBlockFieldUriElm);
//		
//
//		return prefixBlockElem;
//
//	}
}
