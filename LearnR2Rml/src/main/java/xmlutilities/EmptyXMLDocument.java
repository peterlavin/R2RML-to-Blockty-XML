package xmlutilities;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.constants.CONST;

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
		Element nsElem = emptyXMLdoc.createElementNS(CONST.W3_NS, CONST.XML);
			
		Element rootBlockElement = emptyXMLdoc.createElement(CONST.BLOCK);
		
		rootBlockElement.setAttribute(CONST.DELETABLE, CONST.FALSE);
		
		rootBlockElement.setAttribute(CONST.TYPE, CONST.MAPPING);
		
		nsElem.appendChild(rootBlockElement);
		
		emptyXMLdoc.appendChild(nsElem);
		
		
		return emptyXMLdoc;
		
	}
	
	
	
	

}
