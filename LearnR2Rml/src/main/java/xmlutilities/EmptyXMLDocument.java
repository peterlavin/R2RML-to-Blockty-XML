package xmlutilities;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EmptyXMLDocument {
	
	public EmptyXMLDocument(){
		// Constructor
	}
	
	
	public Document createBlankDocument() {

		DocumentBuilder documentBuilder = null;

		// creating an instance of a document (needs a factory instance first)
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		Document emptyXMLdoc = documentBuilder.newDocument();
		
		// TODO get constants for this...
		Element nsElem = emptyXMLdoc.createElementNS("http://www.w3.org/1999/xhtml", "xml");
		
		Element rootBlockElement = emptyXMLdoc.createElement("block");
		
		rootBlockElement.setAttribute("deletable", "false");
		
		rootBlockElement.setAttribute("type", "mapping");
		
		nsElem.appendChild(rootBlockElement);
		
		emptyXMLdoc.appendChild(nsElem);
		
		
		return emptyXMLdoc;
		
	}
	
	
	
	

}
