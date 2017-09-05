package r2rml.objects;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.constants.CONST;
import r2rml.model.SubjectMap;

import xmlutilities.PrettyPrintXML;

/**
 * Processes TermMaps associated with SubjectMaps and
 * creates an XML element which is used (in part) to recreate a mapping
 * visual in the r2rml-editor UI.
 * 
 * @author lavinpe
 *
 */

public class ProcessSmTermMap {

	private Document xml;

	public ProcessSmTermMap(Document xml) {

		this.xml = xml;

	}

	/**
	 * Receives a previously created XML element and a SubjectMap object.
	 * Extracts the details of the SubjectMap object and addes them to the
	 * XML element. The element is part of the XML Document which is a type
	 * of this class.
	 * 
	 * @param subjectMapStatement
	 * @param subjmap
	 */
	public void processTermMap(Element subjectMapStatement, SubjectMap subjmap) {

		if (subjmap.getClasses().size() == 0 && subjmap.isTermTypeIRI()) {
			/*
			 * Do nothing, IRI is the default, nothing different was declared.
			 */
			return;

		} else if (subjmap.getClasses().size() == 0 && !subjmap.isTermTypeIRI()) {

			System.out.println("Case 1 - no classes but BN declared for TermType");

			Element noClassSubjMap = createNoClassSubjectMap(subjmap);

			/*
			 * The Element passed in is a statement element, statement elements
			 * always have only one (block) child element (blocks can have many)
			 */
			Element subjMapBlockElem = (Element) subjectMapStatement.getFirstChild();
			subjMapBlockElem.appendChild(noClassSubjMap);

		} else if (subjmap.getClasses().size() > 0) {

			List<Resource> classList = subjmap.getClasses();

			Element classBlocksElm = null;

			/*
			 * Iterate over all classes found
			 */
			for (int i = 0; i < classList.size(); i++) {

				if (i == 0) {

					/*
					 * If the default TermType is found, create a TermMap block
					 * without a TermType block in it
					 */
					if(subjmap.isTermTypeIRI()){
						
						classBlocksElm = createClassBlock((Resource) classList.get(i));
						
					} else {
						
						/*
						 * Deal with the TermType other than the default (i.e. IRI)
						 * when dealing with the first element
						 */
						boolean isNotIRI = true; // Can be set by to true if in this if-stm						
						classBlocksElm = createClassTTBlock((Resource) classList.get(i), subjmap, isNotIRI);
						
					}
					
				} else {

					/*
					 * This is a subsequent class, so the previous one(s) need
					 * to go inside a <next> element
					 */
					classBlocksElm = putClassBlkInNext(classBlocksElm);

					// Create another class block
					Element outerClassElement = createClassBlock((Resource) classList.get(i));

					outerClassElement.appendChild(classBlocksElm);
					/*
					 * Reassign this to now become the current class block, all
					 * subsequent classes will *nest* this
					 */
					classBlocksElm = outerClassElement;

				}

			}

			/*
			 * All of the above class block(s) need to be in a statement element
			 */
			Element termMapStatement = xml.createElement(CONST.STATEMENT);
			termMapStatement.setAttribute(CONST.NAME, CONST.TERMMAP);
			termMapStatement.appendChild(classBlocksElm);

			/*
			 * The Element passed in is a statement element, statement elements
			 * always have only one (block) child element (blocks can have many)
			 */
			Element subjMapBlockElem = (Element) subjectMapStatement.getFirstChild();
			subjMapBlockElem.appendChild(termMapStatement);

		}
	}

	/*
	 * Creates a class block element with a term type declared, only used
	 * when the term type is not the default (IRI)
	 */
	private Element createClassTTBlock(Resource subjectMapClass, SubjectMap subjmap, boolean isNotIRI) {
		
		/*
		 * First create the term type block
		 */

		Element subjectTermTypeFieldElement = xml.createElement(CONST.FIELD);
		subjectTermTypeFieldElement.setAttribute(CONST.NAME, CONST.TERMTYPE_UC);

		/*
		 * Determine the type of the term, IRI by default, but may be a blank
		 * node
		 */
		String termTypeVar = subjmap.isTermTypeIRI() ? CONST.TERMTYPE_IRI : CONST.TERMTYPE_BN;

		subjectTermTypeFieldElement.appendChild(xml.createTextNode(termTypeVar));

		Element termTypeBlockElement = xml.createElement(CONST.BLOCK);
		termTypeBlockElement.setAttribute(CONST.TYPE, CONST.SUBJECTTERMTYPE);

		termTypeBlockElement.appendChild(subjectTermTypeFieldElement);
		
		// Put this in a <next> element
		
		termTypeBlockElement = putClassBlkInNext(termTypeBlockElement);
		
		/*
		 * Now create the class block
		 */
		String classPrefixName = getClassPrefix(subjectMapClass);

		Element classField = xml.createElement(CONST.FIELD);
		classField.setAttribute(CONST.NAME, CONST.CLASS_UC);
		classField.appendChild(xml.createTextNode(classPrefixName));

		// Create block of type 'class' and add a field of name 'class' to it
		Element termMapBlock = xml.createElement(CONST.BLOCK);
		termMapBlock.setAttribute(CONST.TYPE, CONST.CLASS);

		termMapBlock.appendChild(classField);
		
		/*
		 * Append the Term Type (next) block also
		 */
		termMapBlock.appendChild(termTypeBlockElement);

		return termMapBlock;
		
	}

	/*
	 * Takes a class block element where there is more than one class declared
	 * and puts it in a <next></next> element
	 */
	private Element putClassBlkInNext(Element blockElm) {

		Element nextElement = xml.createElement(CONST.NEXT);

		nextElement.appendChild(blockElm);

		return nextElement;

	}

	private Element createNoClassSubjectMap(SubjectMap subjmap) {

		/*
		 * Starting from the innermost element... Create a TERMTYPE field
		 * element and populate it. Surround this in a block element of type
		 * 'subjecttermtype'. Surround both these in a statement element with
		 * name termmap.
		 */

		Element subjectTermTypeFieldElement = xml.createElement(CONST.FIELD);
		subjectTermTypeFieldElement.setAttribute(CONST.NAME, CONST.TERMMAP_UC);

		/*
		 * Determine the type of the term, IRI by default, but may be a blank
		 * node
		 */
		String termTypeVar = subjmap.isTermTypeIRI() ? CONST.TERMTYPE_IRI : CONST.TERMTYPE_BN;

		subjectTermTypeFieldElement.appendChild(xml.createTextNode(termTypeVar));

		Element termTypeBlockElement = xml.createElement(CONST.BLOCK);
		termTypeBlockElement.setAttribute(CONST.TYPE, CONST.SUBJECTTERMTYPE);

		termTypeBlockElement.appendChild(subjectTermTypeFieldElement);

		// Create a stamement block to surround these and append the above as a
		// child
		Element termMapStatementElement = xml.createElement(CONST.STATEMENT);
		termMapStatementElement.setAttribute(CONST.NAME, CONST.TERMMAP);
		termMapStatementElement.appendChild(termTypeBlockElement);

		return termMapStatementElement;

	}

	/*
	 * Helper method to get the prefix and its local name of a TermType class.
	 * These make up the name of the subject map class.
	 */
	private String getClassPrefix(Resource subjectClass) {

		String prefixAndName = "";

		Map<String, String> pmap = (Map<String, String>) subjectClass.getModel().getNsPrefixMap();

		for (Entry<String, String> value : pmap.entrySet()) {
			
			System.out.println(value.getValue() + "\n" + subjectClass.getNameSpace() + "\n");

			if (value.getValue().equals(subjectClass.getNameSpace())) {

				prefixAndName = value.getKey() + ":" + subjectClass.getLocalName();
				break;
			}

		}

		return prefixAndName;

	}

	/*
	 * Creates a single class block with no TermType elements
	 */
	private Element createClassBlock(Resource subjmapClass) {

		String classPrefixName = getClassPrefix(subjmapClass);

		Element classField = xml.createElement(CONST.FIELD);
		classField.setAttribute(CONST.NAME, CONST.CLASS_UC);
		classField.appendChild(xml.createTextNode(classPrefixName));

		// Create block of type 'class' and add a field of name 'class' to it
		Element termMapBlock = xml.createElement(CONST.BLOCK);
		termMapBlock.setAttribute(CONST.TYPE, CONST.CLASS);

		termMapBlock.appendChild(classField);
		
		PrettyPrintXML.printElement(termMapBlock);

		return termMapBlock;
	}

}
