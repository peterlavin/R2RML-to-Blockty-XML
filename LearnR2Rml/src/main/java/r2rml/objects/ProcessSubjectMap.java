package r2rml.objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.model.SubjectMap;
import r2rml.model.TriplesMap;

public class ProcessSubjectMap {

	private Document xml;

	public ProcessSubjectMap(Document xml) {
		
		this.xml = xml;
		
	}

	public Document processSubjectMap(TriplesMap tmap, Element tripmapStatementElem) {

		
		SubjectMap subjmap = tmap.getSubjectMap();

		String termmapTypeStr = "";
		String termmapVariableStr = "";
		
		
		/*
		 * The subject map may be of type template, column or constant,
		 * all three are checked and the requred details found 
		 */
		if(subjmap.isTemplateValuedTermMap()){
			
			System.out.println("Template Valued Term Map found Subject Map");
			System.out.println("Subject Map TERMMAPVALUE\t\t" + subjmap.getTemplate());
			// do stuff with this value
			
			termmapTypeStr = "TEMPLATE";
			termmapVariableStr = subjmap.getTemplate().toString();
			
		}
		else if(subjmap.isColumnValuedTermMap()){
			System.out.println("Column Valued Term Map found in Subject Map");
			System.out.println("Subject Map TERMMAPVALUE\t\t" + subjmap.getColumn());
			// TODO, get examples from AC to see how to use this value
			
			termmapTypeStr = "COLUMN";
			termmapVariableStr = subjmap.getColumn();
			
		}
		else if(subjmap.isConstantValuedTermMap()){
				System.out.println("Constant Valued Term Map found in Subject Map");
				System.out.println("TERMMAPVALUE\t\t" + subjmap.getConstant());
				// TODO, get examples from AC to see how to use this value
				
				termmapTypeStr = "CONSTANT";
				termmapVariableStr = subjmap.getConstant().toString();
				
		}
		else {
			System.out.println("Something went wrong when determining TEMPLATE/COLUMN/ "
					+ "CONSTANT type for a subject map");
			// TODO handle this failure better FIXME
		}
		
		/*
		 * Now that all variables are determined, create the subject map statement element
		 * and add its block and field elements
		 * 
		 * 
		 * The Element arg passed in here is the overall statement element for a
		 * particular triplesmap. The subject map statement element and its parts
		 * need to be inserted into the triplesmap block element within this
		 * statement element
		 */
		Element tripmapBlockElem = (Element) tripmapStatementElem.getFirstChild();
		
		tripmapBlockElem = insertSubjectMapElement(tripmapBlockElem,termmapTypeStr,termmapVariableStr);
		
		return this.xml;
		
	}

	private Element insertSubjectMapElement(Element tripmapBlockElem, 
			String termmapTypeStr,String termmapVariableStr) {
		
		
		/*
		 * In reverse order, create the two required field elements
		 */
		Element subjectMapFieldElementTermMap = xml.createElement("field");
		subjectMapFieldElementTermMap.setAttribute("name", "TERMMAP");
		subjectMapFieldElementTermMap.appendChild(xml.createTextNode(termmapTypeStr));
		
		Element subjectMapFieldElementTermMapValue = xml.createElement("field");
		subjectMapFieldElementTermMapValue.setAttribute("name", "TERMMAPVALUE");
		subjectMapFieldElementTermMapValue.appendChild(xml.createTextNode(termmapVariableStr));
		
		/*
		 * Create a subject block element to hold the two above field elements
		 */
		Element subjectMapBlockElement = xml.createElement("block");
		subjectMapBlockElement.setAttribute("type", "subjectmap");
		subjectMapBlockElement.appendChild(subjectMapFieldElementTermMap);
		subjectMapBlockElement.appendChild(subjectMapFieldElementTermMapValue);
		
		
		/*
		 * Create a subject map statement element to hold all this
		 */
		Element subjectMapStatementElement = xml.createElement("statement");
		subjectMapStatementElement.setAttribute("name", "subjectmap");
		subjectMapStatementElement.appendChild(subjectMapBlockElement);
		
		/*
		 * Append all of the above to the triples map statement element for
		 * this particular triplesmap
		 */
		tripmapBlockElem.appendChild(subjectMapStatementElement);
		
		return tripmapBlockElem;
		
	}

}
