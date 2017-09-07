package r2rml.objects;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.constants.CONST;
import r2rml.model.GraphMap;
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
		
		
		/*
		 * Three entities relating to SubjectMaps are converted to XML in this class.
		 * If there is a declared TermType other than IRI, that is processed first,
		 * and nested within <next> blocks below all other entities.
		 * 
		 *  Subject Graph Maps are nest, followed by classes.
		 *  
		 *  All of these are nested in the SubjectMap statement/block element.
		 */
		
		Element termTypeBlock= null;
		Element graphsBlock= null;
		Element classBlock= null;
		
		
		/*
		 * First, determine if an Element for TermType is needed, i.e.
		 * if the TermType is other than IRI. There can only be one
		 * Term Type
		 */
		
		if(!subjmap.isTermTypeIRI()){
			
			termTypeBlock= createTermTypeBlock(subjmap);
			
			System.out.println("1. Returned subjGraphBlock for output...\n");
			PrettyPrintXML.printElement(termTypeBlock);
			
		}
		
		
		/*
		 * Second, determine if there are one or more GraphsMaps, if so, prepare
		 * the block-next structure for insertion. If termTypeBlock is null, 
		 * ignore it, otherwise, nest it with the first Graph Map
		 */

		if(subjmap.getGraphMaps().size() > 0){
					
			graphsBlock= createGraphMapsBlock(subjmap.getGraphMaps(), termTypeBlock);
						
			System.out.println("2. Returned subjGraphBlock for output...\n");
			PrettyPrintXML.printElement(graphsBlock);
			
		}
		
		/*
		 * Third, determine if there are any classes to be processed
		 */
		if(subjmap.getClasses().size() > 0){
			
			//classBlock = createClassBlock(subjmap);
		}
		
		
		
		if (subjmap.getClasses().size() == 0 && subjmap.isTermTypeIRI() && graphsBlock== null) {
			/*
			 * Do nothing, IRI is the default, nothing different was declared.
			 * 
			 * TODO, this much change to accommodate GraphMaps sub-block
			 */
			return;

		} else if (subjmap.getClasses().size() == 0 && !subjmap.isTermTypeIRI()) {

			Element noClassSubjMap = createNoClassSubjectMap(subjmap);

			/*
			 * The Element passed in is a statement element, statement elements
			 * always have only one (block) child element (blocks can have many)
			 */
			Element subjMapBlockElem = (Element) subjectMapStatement.getFirstChild();
			subjMapBlockElem.appendChild(noClassSubjMap);
			
			PrettyPrintXML.printElement(subjMapBlockElem);
			
			if(graphsBlock!= null){
				
				graphsBlock= putBlockInNext(graphsBlock);
				subjMapBlockElem.appendChild(graphsBlock);
								
			}
			
			PrettyPrintXML.printElement(subjMapBlockElem);

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
						classBlocksElm = createClassTTBlock((Resource) classList.get(i), subjmap);
						
					}
					
				} else {

					/*
					 * This is a subsequent class, so the previous one(s) need
					 * to go inside a <next> element
					 */
					classBlocksElm = putBlockInNext(classBlocksElm);

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
			 * All of the above class block(s) need to be in a statement element,
			 * depending on which have remined null, and which have become XML
			 * elements
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
	private Element createClassTTBlock(Resource subjectMapClass, SubjectMap subjmap) {
		
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
		
		termTypeBlockElement = putBlockInNext(termTypeBlockElement);
		
		/*
		 * Now create the class block
		 */
		String classPrefixName = getResourcePrefix(subjectMapClass);

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
	private Element putBlockInNext(Element blockElm) {

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
	private String getResourcePrefix(Resource resource) {

		String prefixAndName = "";

		Map<String, String> pmap = (Map<String, String>) resource.getModel().getNsPrefixMap();

		for (Entry<String, String> value : pmap.entrySet()) {
			
			//System.out.println(value.getValue() + "\n" + resource.getNameSpace() + "\n");

			if (value.getValue().equals(resource.getNameSpace())) {
				
				prefixAndName = value.getKey() + ":" + resource.getLocalName();
				return prefixAndName; //value.getKey() + ":" + resource.getLocalName();				
			} else {
				
				prefixAndName = "<" + resource.toString() + ">";
				
			}

		}

		return prefixAndName;

	}

	/*
	 * Creates a single class block with no TermType elements
	 */
	private Element createClassBlock(Resource subjmapClass) {

		String classPrefixName = getResourcePrefix(subjmapClass);

		Element classField = xml.createElement(CONST.FIELD);
		classField.setAttribute(CONST.NAME, CONST.CLASS_UC);
		classField.appendChild(xml.createTextNode(classPrefixName));

		// Create block of type 'class' and add a field of name 'class' to it
		Element termMapBlock = xml.createElement(CONST.BLOCK);
		termMapBlock.setAttribute(CONST.TYPE, CONST.CLASS);

		termMapBlock.appendChild(classField);
				
		return termMapBlock;
	}
	
	
	/*
	 * Creates a block for a single graph map, or a nest
	 * of blocks for a number of graph maps using <next>
	 * blocks.
	 */
	private Element createGraphMapsBlock(List<GraphMap> gList, Element termTypeBlock){
		
		
		
		System.out.println("gList size... " + gList.size());
		
		Element savedGraphBlock = null;
		Element basicGraphBlock = null;
		
		for(int i = 0; i < gList.size(); i++){
			
			GraphMap gm = gList.get(i);
			
			// constant, column or template
			
			String termMapTypeStr = "";
			String prefixAndName = "";
			
			
			/*
			 * Prepare these variables for graph block creation
			 */
			if(gm.isConstantValuedTermMap()) {
				
				termMapTypeStr = CONST.CONSTANT_UC;
				/*
				 *  Prefix:name set need for constant only
				 */
				prefixAndName = getResourcePrefix(gm.getConstant().asResource());
				
			} else if(gm.isColumnValuedTermMap()) {
				termMapTypeStr = CONST.COLUMN_UC;
				prefixAndName = gm.getColumn().toString(); 
			} else {
				termMapTypeStr = CONST.TEMPLATE_UC;
				prefixAndName = gm.getTemplate().toString();
			}
			

//			System.out.println("termMapTypeStr " + termMapTypeStr + " " + i);
//			System.out.println("prefixAndName " + prefixAndName  + " " + i);


			


			if (i < (gList.size() -1)) {				
				/*
				 * There is more than one graph, and the current graph
				 * is a 1st, 2nd, etc of many, but not the last one,
				 * so <next> blocks are required
				 */

				basicGraphBlock = createSubjGraphBlock(termMapTypeStr, prefixAndName);

				/*
				 * If this is the first/only graph, there will be no saved graph
				 */
				if(savedGraphBlock != null){
					basicGraphBlock.appendChild(savedGraphBlock);
				}

				/*
				 * If this is the first graph, then next a non-null
				 * termTypeBlock in it, if available.
				 * 
				 * Once this happens, it should not be done again
				 */
				if(i == 0){
				
					if(termTypeBlock != null){
						
						System.out.println("Adding TT in first block...\n");
						
						termTypeBlock = putBlockInNext(termTypeBlock);
						basicGraphBlock.appendChild(termTypeBlock);
						
					}
				
					/*
					 * As this is not the last graph, use a <next> block
					 */
					basicGraphBlock = putBlockInNext(basicGraphBlock);
				
				}
				
				/*
				 * Save this for the next iteration
				 */
				savedGraphBlock = basicGraphBlock;
				
				System.out.println("savedGraphBlock for MID-WAY GRAPH...\n");
				PrettyPrintXML.printElement(savedGraphBlock);				


			} else if (i == (gList.size() -1)) {
				/*
				 * There is only one graph or the current graph
				 * is the last one. It should be returned, with its
				 * nested blocks (of present), but not surrounded by a next
				 */
				
				basicGraphBlock = createSubjGraphBlock(termMapTypeStr, prefixAndName);
					
				
				/*
				 * If this is the first and only graph, then defend against
				 * savedGraphBlock being null (as initialised).
				 * 
				 * If savedGraphBLock is null at this point, then there
				 * is only one Graph being processed.
				 * 
				 * If savedGraphBlock is !null, then termTypeBlock will 
				 * be nested within it.
				 * 
				 *  However, if this is the only graph, the above if- will
				 *  never be entered, and a non-null termTpeBlock must be
				 *  added here 
				 */
				if(savedGraphBlock != null){
					
					basicGraphBlock.appendChild(savedGraphBlock);
					
				} else if (termTypeBlock != null && i == 0){
					
					termTypeBlock = putBlockInNext(termTypeBlock);
					basicGraphBlock.appendChild(termTypeBlock);
					
				}

				System.out.println("subjGraphBlock for LAST GRAPH...\n");
				PrettyPrintXML.printElement(basicGraphBlock);				
				
			}
			
		}
		
		return basicGraphBlock;
	
	}

	private Element createSubjGraphBlock(String termMapTypeStr, String prefixAndName) {
		
		/*
		 * Create the inner field elements 
		 */
		Element fieldTermMap = xml.createElement(CONST.FIELD);
		fieldTermMap.setAttribute(CONST.NAME, CONST.TERMMAP_UC);
		fieldTermMap.appendChild(xml.createTextNode(CONST.COLUMN_UC));
		
		Element fieldTermMapValue = xml.createElement(CONST.TERMMAPVALUE_UC);
		fieldTermMapValue.setAttribute(CONST.NAME, CONST.TERMMAPVALUE_UC);
		fieldTermMapValue.appendChild(xml.createTextNode(prefixAndName));
		
		/*
		 * Append these both to a <block type="subjectgraphtermap">
		 */
		Element subjGraphBlock = xml.createElement(CONST.BLOCK);
		subjGraphBlock.setAttribute(CONST.TYPE, CONST.SUBJECTGRAPHTERMAP);
		subjGraphBlock.appendChild(fieldTermMap);
		subjGraphBlock.appendChild(fieldTermMapValue);
		
		
		return subjGraphBlock;
	}

	/*
	 * Creates a block element which contains 
	 */
	
	private Element createTermTypeBlock(SubjectMap subjmap){
		
		/*
		 * First create the term type field elements
		 */
		Element TermTypeField = xml.createElement(CONST.FIELD);
		TermTypeField.setAttribute(CONST.NAME, CONST.TERMTYPE_UC);

		String termTypeVar = subjmap.isTermTypeIRI() ? CONST.TERMTYPE_IRI : CONST.TERMTYPE_BN;

		TermTypeField.appendChild(xml.createTextNode(termTypeVar));

		/*
		 * Create a block to surround this and append
		 */
		Element termTypeBlock = xml.createElement(CONST.BLOCK);
		termTypeBlock.setAttribute(CONST.TYPE, CONST.SUBJECTTERMTYPE);

		termTypeBlock.appendChild(TermTypeField);
		
		return termTypeBlock;
	}
}
