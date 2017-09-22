package r2rml.objects;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.constants.CONST;
import r2rml.model.GraphMap;
import r2rml.model.Join;
import r2rml.model.ObjectMap;
import r2rml.model.PredicateMap;
import r2rml.model.PredicateObjectMap;
import r2rml.model.RefObjectMap;
import xmlutilities.PrettyPrintXML;

public class ProcessPartsOfPredObjMap {

	private Document xml;

	public ProcessPartsOfPredObjMap(Document xml) {

		// Constructor
		this.xml = xml;

	}

	public void processPartsPredObjMap(Element predObjBlock, PredicateObjectMap pom) {

		/*
		 * A PredicateObjectMap must contain at least one Predicate Map and at
		 * least one Object Map. Graphs are not always present.
		 */

		Element predMapStatement = createPredicateMap(pom.getPredicateMaps());
		predObjBlock.appendChild(predMapStatement);

		/*
		 * A PredicateObjectMap must also have at least one object map, or one
		 * join condition, but may also have a combination of both object maps
		 * and join(s)
		 */
		if (!pom.getObjectMaps().isEmpty() && pom.getRefObjectMaps().isEmpty()) {
			// Has Object Maps but no RefObjectMaps (Joins)

			Element objectMapStatement = createObjMap(pom.getObjectMaps());
			predObjBlock.appendChild(objectMapStatement);

			System.out.println("\n!!!!! Found Object Maps  !!!!\n");

		} else if (!pom.getRefObjectMaps().isEmpty() && pom.getObjectMaps().isEmpty()) {
			// Has RefObjectMaps (joins) but no Object Maps

			System.out.println("\n!!!!! Found Joins !!!!\n");

			Element joinMapStatement = createJoinMap(pom.getRefObjectMaps());
			predObjBlock.appendChild(joinMapStatement);

		} else if (!pom.getObjectMaps().isEmpty() && !pom.getRefObjectMaps().isEmpty()) {
			// Has both Object Maps and RefObjectMaps (joins)

			Element joinsAndObjJoinMapStatement = createJoinsAndObjMap(pom.getRefObjectMaps(), pom.getObjectMaps());
			predObjBlock.appendChild(joinsAndObjJoinMapStatement);

		}

		if (!pom.getGraphMaps().isEmpty()) {

			Element graphMapsStatement = createGraphMap(pom.getGraphMaps());
			predObjBlock.appendChild(graphMapsStatement);

		}

	}

	/*
	 * HELPER METHODS TO CREATE THE ELEMENTS
	 */

	/**
	 * Creates a Predicate Map element which can contain one or more (nested)
	 * Predicate Map elements
	 * 
	 * @param predMapsList
	 * @return
	 */
	private Element createPredicateMap(List<PredicateMap> predMapsList) {

		Element savedPredMapBlock = null;
		Element basicPredMapBlock = null;

		for (int i = 0; i < predMapsList.size(); i++) {

			PredicateMap pm = predMapsList.get(i);

			String termMapTypeStr = "";
			String prefixAndName = "";

			if (pm.isConstantValuedTermMap()) {

				termMapTypeStr = CONST.CONSTANT_UC;
				/*
				 * Prefix:name set need for constant only
				 */
				prefixAndName = getResourcePrefix(pm.getConstant().asResource());

			} else if (pm.isColumnValuedTermMap()) {
				
				termMapTypeStr = CONST.COLUMN_UC;
				prefixAndName = pm.getColumn().toString();
				
			} else {
				
				termMapTypeStr = CONST.TEMPLATE_UC;
				prefixAndName = pm.getTemplate().toString();
				
			}

			if (i < (predMapsList.size() - 1)) {
				// More than one map, but this is not the last one

				basicPredMapBlock = createBasicPmBlock(termMapTypeStr, prefixAndName);

				/*
				 * If this is the first graph, there will be no saved graph
				 */
				if (savedPredMapBlock != null) {
					basicPredMapBlock.appendChild(savedPredMapBlock);
				}

				/*
				 * This is not the last graph, so use a <next> block
				 */
				basicPredMapBlock = putInNextElement(basicPredMapBlock);

				/*
				 * Save this for the next iteration
				 */
				savedPredMapBlock = basicPredMapBlock;

			} else if (i == (predMapsList.size() - 1)) {
				// Only one map, or this is the last one

				basicPredMapBlock = createBasicPmBlock(termMapTypeStr, prefixAndName);

				/*
				 * If this is the only map, then defend against
				 * savedPredMapBlock being null (as initialised).
				 */
				if (savedPredMapBlock != null) {

					/*
					 * Recall, any savedGraphBlock will already be surrounded by
					 * a <next> block
					 */
					basicPredMapBlock.appendChild(savedPredMapBlock);

				}

			}

		}

		/*
		 * This needs to be in a <statement> element
		 */
		Element predMapStatement = xml.createElement(CONST.STATEMENT);
		predMapStatement.setAttribute(CONST.NAME, CONST.PPREDICATEOBJECTMAP);
		predMapStatement.appendChild(basicPredMapBlock);

		return predMapStatement;

	}

	/**
	 * Creates an Object Map element with one or more (nested) Object Maps.
	 * 
	 * @param objectMaps
	 * @return
	 */
	private Element createObjMap(List<ObjectMap> objectMaps) {

		Element savedObjectMapBlock = null;
		Element basicObjectMapBlock = null;

		for (int i = 0; i < objectMaps.size(); i++) {

			ObjectMap om = objectMaps.get(i);

			if (i < (objectMaps.size() - 1)) {
				// More than one map, but this is not the last one

				basicObjectMapBlock = createBasicObjectBlock(om);

				/*
				 * If this is the first graph, there will be no saved graph
				 */
				if (savedObjectMapBlock != null) {
					basicObjectMapBlock.appendChild(savedObjectMapBlock);
				}

				/*
				 * This is not the last graph, so use a <next> block
				 */
				basicObjectMapBlock = putInNextElement(basicObjectMapBlock);

				/*
				 * Save this for the next iteration
				 */
				savedObjectMapBlock = basicObjectMapBlock;

			} else if (i == (objectMaps.size() - 1)) {
				// Only one map, or this is he last of many

				basicObjectMapBlock = createBasicObjectBlock(om);

				/*
				 * If this is the only graph, then defend against
				 * savedGraphBlock being null (as initialised).
				 */
				if (savedObjectMapBlock != null) {

					/*
					 * Recall, any savedGraphBlock will already be surrounded by
					 * a <next> block
					 */
					basicObjectMapBlock.appendChild(savedObjectMapBlock);

				}

			}

		}

		/*
		 * This needs to be in a <statement> element
		 */
		Element objectMapStatement = xml.createElement(CONST.STATEMENT);
		objectMapStatement.setAttribute(CONST.NAME, CONST.OPREDICATEOBJECTMAP);

		if (basicObjectMapBlock != null) {
			objectMapStatement.appendChild(basicObjectMapBlock);
		}

		return objectMapStatement;

	}

	/**
	 * Creates a map of a join.
	 * 
	 * @param refObjectMaps
	 * @return
	 */
	private Element createJoinMap(List<RefObjectMap> refObjectList) {
		// TODO joinMap

		Element savedRefObjBlock = null;
		Element basicRefObjBlock = null;

		for (int i = 0; i < refObjectList.size(); i++) {

			/*
			 * 
			 */
			if (i < (refObjectList.size() - 1)) {
				/*
				 * There is more than one graph, and the current graph is a 1st,
				 * 2nd, etc of many, but not the last one. Therefore, <next>
				 * blocks are required
				 */

				basicRefObjBlock = createBasicRefObjBlock(refObjectList.get(i));

				/*
				 * If this is the first graph, there will be no saved graph
				 */
				if (savedRefObjBlock != null) {
					basicRefObjBlock.appendChild(savedRefObjBlock);
				}

				/*
				 * This is not the last graph, so use a <next> block
				 */
				basicRefObjBlock = putInNextElement(basicRefObjBlock);

				/*
				 * Save this for the next iteration
				 */
				savedRefObjBlock = basicRefObjBlock;

			} else if (i == (refObjectList.size() - 1)) {
				/*
				 * There is only one graph or the current graph is the last one.
				 * It should be returned but not surrounded by a <next>
				 */

				basicRefObjBlock = createBasicRefObjBlock(refObjectList.get(i));

				/*
				 * If this is the only graph, then defend against
				 * savedGraphBlock being null (as initialised).
				 */
				if (savedRefObjBlock != null) {

					/*
					 * Recall, any savedGraphBlock will already be surrounded by
					 * a <next> block
					 */
					basicRefObjBlock.appendChild(savedRefObjBlock);

				}

			}

		}

		/*
		 * This needs to be in a <statement> element
		 */
		Element RefObjStatement = xml.createElement(CONST.STATEMENT);
		RefObjStatement.setAttribute(CONST.NAME, CONST.OPREDICATEOBJECTMAP);
		RefObjStatement.appendChild(basicRefObjBlock);

		return RefObjStatement;

	}

	/**
	 * Creates an element which contains a mix of RefObject (joins) and Object
	 * Maps.
	 * 
	 * @param refObjectMaps
	 * @param objectMaps
	 * @return
	 */
	private Element createJoinsAndObjMap(List<RefObjectMap> refObjMaps, List<ObjectMap> objectMaps) {

		/*
		 * First, Create the block for the Object Map(s), this is placed in a
		 * <next> and added after the first RefObject Map (join) is processed.
		 */

		/*
		 * This returns a <statement> element, so need to get the <block> within
		 * it.
		 */
		Element objectMapStatement = createObjMap(objectMaps);
		Element objectMapBlock = (Element) objectMapStatement.getFirstChild();

		Element basicJoinAndObjBlock = null;
		Element savedJoinAndObjBlock = null;

		for (int i = 0; i < refObjMaps.size(); i++) {

			if (i < (refObjMaps.size() - 1)) {

				basicJoinAndObjBlock = createBasicRefObjBlock(refObjMaps.get(i));

				/*
				 * If this is the first join, append objectMapBlock in a <next>
				 * element (only ever happens once)
				 */
				if (i == 0) {
					objectMapBlock = putInNextElement(objectMapBlock);
					basicJoinAndObjBlock.appendChild(objectMapBlock);
				}

				/*
				 * If savedJoinAndObjBlock is not null, append it
				 */
				if (savedJoinAndObjBlock != null) {
					basicJoinAndObjBlock.appendChild(savedJoinAndObjBlock);
				}

				/*
				 * As this is NOT the last class, put basicJoinAndObjBlock in a
				 * <next> block and assign to 'saved' element
				 */
				basicJoinAndObjBlock = putInNextElement(basicJoinAndObjBlock);

				savedJoinAndObjBlock = basicJoinAndObjBlock;

			} else if (i == (refObjMaps.size() - 1)) {
				/*
				 * This is only class, or the last one, no <next> needed
				 */

				basicJoinAndObjBlock = createBasicRefObjBlock(refObjMaps.get(i));

				/*
				 * If this is the first join, append objectMapBlock in a <next>
				 * element (only ever happens once)
				 */
				if (i == 0) {
					objectMapBlock = putInNextElement(objectMapBlock);
					basicJoinAndObjBlock.appendChild(objectMapBlock);
				}

				/*
				 * If this is the last of many joins, append previously saved
				 * elements
				 */
				if (savedJoinAndObjBlock != null) {
					basicJoinAndObjBlock.appendChild(savedJoinAndObjBlock);
				}

			}

		}

		/*
		 * This needs to be in a <statement> element
		 */
		Element joinAndObjStatement = xml.createElement(CONST.STATEMENT);
		joinAndObjStatement.setAttribute(CONST.NAME, CONST.OPREDICATEOBJECTMAP);
		joinAndObjStatement.appendChild(basicJoinAndObjBlock);
		
		return joinAndObjStatement;

	}

	/**
	 * Creates a Graph Map element for one or more (nested) Graph Maps.
	 * 
	 * @param graphMaps
	 * @return
	 */
	private Element createGraphMap(List<GraphMap> graphList) {

		Element savedGraphBlock = null;
		Element basicGraphBlock = null;

		for (int i = 0; i < graphList.size(); i++) {

			GraphMap gm = graphList.get(i);

			String termMapTypeStr = "";
			String prefixAndName = "";

			/*
			 * Prepare these variables for graph block creation
			 */
			if (gm.isConstantValuedTermMap()) {

				termMapTypeStr = CONST.CONSTANT_UC;
				/*
				 * Prefix:name set need for constant only
				 */
				prefixAndName = getResourcePrefix(gm.getConstant().asResource());

			} else if (gm.isColumnValuedTermMap()) {
				
				termMapTypeStr = CONST.COLUMN_UC;
				prefixAndName = gm.getColumn().toString();
				
			} else {
				
				termMapTypeStr = CONST.TEMPLATE_UC;
				prefixAndName = gm.getTemplate().toString();
				
			}

			/*
			 * 
			 */
			if (i < (graphList.size() - 1)) {
				/*
				 * There is more than one graph, and the current graph is a 1st,
				 * 2nd, etc of many, but not the last one. Therefore, <next>
				 * blocks are required
				 */

				basicGraphBlock = createPomGraphBlock(termMapTypeStr, prefixAndName);

				/*
				 * If this is the first graph, there will be no saved graph
				 */
				if (savedGraphBlock != null) {
					basicGraphBlock.appendChild(savedGraphBlock);
				}

				/*
				 * This is not the last graph, so use a <next> block
				 */
				basicGraphBlock = putInNextElement(basicGraphBlock);

				/*
				 * Save this for the next iteration
				 */
				savedGraphBlock = basicGraphBlock;

			} else if (i == (graphList.size() - 1)) {
				/*
				 * There is only one graph or the current graph is the last one.
				 * It should be returned but not surrounded by a <next>
				 */

				basicGraphBlock = createPomGraphBlock(termMapTypeStr, prefixAndName);

				/*
				 * If this is the only graph, then defend against
				 * savedGraphBlock being null (as initialised).
				 */
				if (savedGraphBlock != null) {

					/*
					 * Recall, any savedGraphBlock will already be surrounded by
					 * a <next> block
					 */
					basicGraphBlock.appendChild(savedGraphBlock);

				}

			}

		}

		/*
		 * This needs to be in a <statement> element
		 */
		Element graphMapStatement = xml.createElement(CONST.STATEMENT);
		graphMapStatement.setAttribute(CONST.NAME, CONST.GRAPHMAP);
		graphMapStatement.appendChild(basicGraphBlock);

		return graphMapStatement;

	}

	/*
	 * Helper method to get the prefix and its local name of a TermType class.
	 * These make up the name of the subject map class.
	 */
	private String getResourcePrefix(Resource resource) {

		String prefixAndName = "";

		Map<String, String> pmap = (Map<String, String>) resource.getModel().getNsPrefixMap();

		for (Entry<String, String> value : pmap.entrySet()) {

			// System.out.println(value.getValue() + "\n" +
			// resource.getNameSpace() + "\n");

			if (value.getValue().equals(resource.getNameSpace())) {

				prefixAndName = value.getKey() + ":" + resource.getLocalName();
				return prefixAndName;

			} else {
				prefixAndName = "<" + resource.toString() + ">";
			}
		}

		return prefixAndName;

	}

	/*
	 * Takes any block element and puts it in a <next></next> element
	 */
	private Element putInNextElement(Element blockElm) {

		Element nextElement = xml.createElement(CONST.NEXT);

		nextElement.appendChild(blockElm);

		return nextElement;

	}

	/**
	 * Creates a basic block for a Graph Map, as part of a Subject Map
	 */
	private Element createBasicPmBlock(String termMapTypeStr, String prefixAndName) {

		/*
		 * Create the inner field elements
		 */
		Element fieldTermMap = xml.createElement(CONST.FIELD);

		fieldTermMap.setAttribute(CONST.NAME, CONST.TERMMAP_UC);
		fieldTermMap.appendChild(xml.createTextNode(termMapTypeStr));

		Element fieldTermMapValue = xml.createElement(CONST.FIELD);
		fieldTermMapValue.setAttribute(CONST.NAME, CONST.TERMMAPVALUE_UC);
		fieldTermMapValue.appendChild(xml.createTextNode(prefixAndName));

		/*
		 * Append these both to a <block type="subjectgraphtermap">
		 */
		Element pmGraphBlock = xml.createElement(CONST.BLOCK);

		pmGraphBlock.setAttribute(CONST.TYPE, CONST.PREDICATEMAP);
		pmGraphBlock.appendChild(fieldTermMap);
		pmGraphBlock.appendChild(fieldTermMapValue);

		return pmGraphBlock;
	}

	/**
	 * Creates a basic block for a Graph Map, as part of a Predicate Object Map
	 */
	private Element createPomGraphBlock(String termMapTypeStr, String prefixAndName) {

		/*
		 * Create the inner field elements
		 */
		Element fieldTermMap = xml.createElement(CONST.FIELD);

		fieldTermMap.setAttribute(CONST.NAME, CONST.TERMMAP_UC);
		fieldTermMap.appendChild(xml.createTextNode(termMapTypeStr));

		Element fieldTermMapValue = xml.createElement(CONST.FIELD);
		fieldTermMapValue.setAttribute(CONST.NAME, CONST.TERMMAPVALUE_UC);
		fieldTermMapValue.appendChild(xml.createTextNode(prefixAndName));

		/*
		 * Append these both to a <block type="subjectgraphtermap">
		 */
		Element pomGraphBlock = xml.createElement(CONST.BLOCK);

		pomGraphBlock.setAttribute(CONST.TYPE, CONST.PREDICATEGRAPHTERMAP);
		pomGraphBlock.appendChild(fieldTermMap);
		pomGraphBlock.appendChild(fieldTermMapValue);

		return pomGraphBlock;
	}

	/**
	 * Creates a basic block for an Object Map, which is a sub-part of a
	 * Predicate Object Map
	 * 
	 * @param termMapTypeStr
	 * @param prefixAndName
	 * @return
	 */
	private Element createBasicObjectBlock(ObjectMap om) {

		String termMapTypeStr = "";
		String prefixAndName = "";

		/*
		 * Prepare these variables for graph block creation
		 */
		if (om.isConstantValuedTermMap()) {

			termMapTypeStr = CONST.CONSTANT_UC;
			/*
			 * Prefix:name set needed for constant only
			 */
			prefixAndName = getResourcePrefix(om.getConstant().asResource());

		} else if (om.isColumnValuedTermMap()) {
			
			termMapTypeStr = CONST.COLUMN_UC;
			prefixAndName = om.getColumn().toString();
			
		} else if (om.isTemplateValuedTermMap()) {
			
			termMapTypeStr = CONST.TEMPLATE_UC;
			prefixAndName = om.getTemplate().toString();
			
		}

		/*
		 * Create the inner field elements
		 */
		Element fieldTermMap = xml.createElement(CONST.FIELD);

		fieldTermMap.setAttribute(CONST.NAME, CONST.TERMMAP_UC);
		fieldTermMap.appendChild(xml.createTextNode(termMapTypeStr));

		Element fieldTermMapValue = xml.createElement(CONST.FIELD);
		fieldTermMapValue.setAttribute(CONST.NAME, CONST.TERMMAPVALUE_UC);
		fieldTermMapValue.appendChild(xml.createTextNode(prefixAndName));

		/*
		 * Check for literal types, if just a literal, do not
		 * make any termtype statement
		 * 
		 * 
		 * if a literal and has language *OR* datatype is set, make...
		 * 
		 * <statement name="termmap"> <block type="objecttermtype"> <field
		 * name="TERMTYPE">termtypeliteral</field> <value name="termtypevalue">
		 * <block type="objectdatatype"> <field name="DATATYPE">????????</field>
		 * </block> </value> </block> </statement>
		 */

		Element termMapStatement = null;

		if (om.isTermTypeLiteral()) {

			/*
			 * Create this inner block, needed by every 'literal' termtype
			 * 
			 * <block type="objecttermtype"> <field
			 * name="TERMTYPE">termtypeliteral</field> </block>
			 */

			Element fieldLitTermMap = xml.createElement(CONST.FIELD);
			fieldLitTermMap.setAttribute(CONST.NAME, CONST.TERMTYPE_UC);
			fieldLitTermMap.appendChild(xml.createTextNode(CONST.TERMTYPELITERAL));

			Element objTermTypeBlock = xml.createElement(CONST.BLOCK);
			objTermTypeBlock.setAttribute(CONST.TYPE, CONST.OBJECTTERMTYPE);
			objTermTypeBlock.appendChild(fieldLitTermMap);

			/*
			 * If there is a datatype or language set, add a <value> element for
			 * this, append to block also.
			 * 
			 * The model dictates that there cannot be a language AND a datatype set
			 */

			
			
			
			
			
			
			
			
			
			
			if (!om.getLanguages().isEmpty()) {

				String languageValue = om.getLanguages().get(0).getObject().toString();

				Element fieldLitLanguage = createFieldElement(CONST.LANGUAGE_UC, languageValue);

				Element objLanguageBlock = createBlockElement(CONST.OBJECTLANGUAGE, fieldLitLanguage);

				Element termTypeValue = putInValueElement(objLanguageBlock);

				objTermTypeBlock.appendChild(termTypeValue);

				/*
				 * Put in <statement name="termmap">
				 */

				// Create an Statement element for all this
				termMapStatement = createStatementElement(CONST.TERMMAP, objTermTypeBlock);

				// Append with other fields
				termMapStatement.appendChild(objTermTypeBlock);

			} else if (!om.getDatatypes().isEmpty()) {

				String datatypeValue = getResourcePrefix(om.getDatatypes().get(0).getObject().asResource());

				Element fieldLitDatatype = createFieldElement(CONST.DATATYPE_UC, datatypeValue);

				Element objDataTypeBlock = createBlockElement(CONST.OBJECTDATATYPE, fieldLitDatatype);

				Element termTypeValue = putInValueElement(objDataTypeBlock);

				objTermTypeBlock.appendChild(termTypeValue);

				// Create an Statement element for all this
				termMapStatement = createStatementElement(CONST.TERMMAP, objTermTypeBlock);

				// Append with other fields
				termMapStatement.appendChild(objTermTypeBlock);


			} else if (om.getDatatypes().isEmpty() && om.getLanguages().isEmpty()) {
				/*
				 * Is a literal but *NO* DataType or Language is set,
				 * 
				 * do not add anything
				 */

//				// Just create a Statement element
//				termMapStatement = createStatementElement(CONST.TERMMAP, objTermTypeBlock);
//
//				// Append the basic termmap statement
//				termMapStatement.appendChild(objTermTypeBlock);
//				
//				System.out.println("objTermTypeBlock......");
//				PrettyPrintXML.printElement(objTermTypeBlock);

			}
			
			
			
			
			
			
			
			
			
			
			
			

		} else if (om.isTermTypeBlankNode()) {
			/*
			 * For blank nodes
			 * 
			 * Create this only..
			 * 
			 * <block type="objecttermtype"> <field
			 * name="TERMTYPE">termtypeblanknode</field> </block>
			 */

			Element fieldBntTermMap = createFieldElement(CONST.TERMTYPE_UC, CONST.TERMTYPEBLANKNODE);

			Element objTermTypeBlock = createBlockElement(CONST.OBJECTTERMTYPE, fieldBntTermMap);

			termMapStatement = createStatementElement(CONST.TERMMAP, objTermTypeBlock);

			// Append the basic termmap statement
			termMapStatement.appendChild(objTermTypeBlock);

		} else if (om.isTermTypeIRI()) {
			/*
			 * IRI is declared or inferred
			 */

			// TODO RF x3
			Element fieldIriTermMap = xml.createElement(CONST.FIELD);
			fieldIriTermMap.setAttribute(CONST.NAME, CONST.TERMTYPE_UC);
			fieldIriTermMap.appendChild(xml.createTextNode(CONST.TERMTYPEIRI));

			Element objTermTypeBlock = xml.createElement(CONST.BLOCK);
			objTermTypeBlock.setAttribute(CONST.TYPE, CONST.OBJECTTERMTYPE);
			objTermTypeBlock.appendChild(fieldIriTermMap);

			// Just create a Statement element
			termMapStatement = xml.createElement(CONST.STATEMENT);
			termMapStatement.setAttribute(CONST.NAME, CONST.TERMMAP);
			termMapStatement.appendChild(objTermTypeBlock);

			// Append the basic termmap statement
			termMapStatement.appendChild(objTermTypeBlock);

		}

		/*
		 * Append all this to a <block type="subjectgraphtermap">
		 */
		Element pomObjectMapBlock = xml.createElement(CONST.BLOCK);

		pomObjectMapBlock.setAttribute(CONST.TYPE, CONST.OBJECTMAP);
		pomObjectMapBlock.appendChild(fieldTermMap);
		pomObjectMapBlock.appendChild(fieldTermMapValue);

		// Only attempt to append if these has been created
		if (termMapStatement != null) {
			pomObjectMapBlock.appendChild(termMapStatement);
		}

		return pomObjectMapBlock;

	}

	
	/**
	 * Creates a basic reference object block, may be called
	 * multiple times where more that one is present.
	 * 
	 * @param rom
	 * @return
	 */
	private Element createBasicRefObjBlock(RefObjectMap rom) {
		// TODO Auto-generated method stub

		/*
		 * Create a basic Reference Object Block to go into a <statement
		 * name="opredicateobjectmap">, the same place as an ObjectMap goes.
		 */

		String parentTripMap = rom.getParentTriplesMap().getLocalName();

		Element fieldParentTripMap = createFieldElement(CONST.PARENTTRIPLEMAP_UC, parentTripMap);

		Element blockParentTripMap = createBlockElement(CONST.PARENTTRIPLESMAP, fieldParentTripMap);

		/*
		 * TODO, unsure if Join list can be empty, in if-stm for fail safety
		 */
		if (!rom.getJoins().isEmpty()) {

			List<Join> joinList = rom.getJoins();
			Element joinStatementElement = createJoinCondStatement(joinList);
			blockParentTripMap.appendChild(joinStatementElement);

		}

		// put this in a <statement name="opredicateobjectmap">
		// TODO, deal with a mix of joins and obj maps, i.e. do
		// not make two of <statement name="opredicateobjectmap">

		return blockParentTripMap;
	}

	/*
	 * Helper reusable methods to create field, block and statement element.
	 */
	private Element createFieldElement(String nameValue, String contentValue) {

		Element fieldLitLanguage = xml.createElement(CONST.FIELD);
		fieldLitLanguage.setAttribute(CONST.NAME, nameValue);
		fieldLitLanguage.appendChild(xml.createTextNode(contentValue));

		return fieldLitLanguage;
	}

	private Element createBlockElement(String typeValue, Element fieldElem) {

		Element objLanguageBlock = xml.createElement(CONST.BLOCK);
		objLanguageBlock.setAttribute(CONST.TYPE, typeValue);
		objLanguageBlock.appendChild(fieldElem);

		return objLanguageBlock;

	}

	private Element createStatementElement(String nameValue, Element innerBlock) {

		Element termMapStatement = xml.createElement(CONST.STATEMENT);
		termMapStatement.setAttribute(CONST.NAME, nameValue);
		termMapStatement.appendChild(innerBlock);

		return termMapStatement;
	}

	private Element putInValueElement(Element innerElem) {

		Element valueElm = xml.createElement(CONST.VALUE);
		valueElm.setAttribute(CONST.NAME, CONST.TERMTYPEVALUE);
		valueElm.appendChild(innerElem);

		return valueElm;

	}

	/**
	 * Creates a join condition statement element and nests each one in a
	 * <next> element if necessary
	 * 
	 * @param joinList
	 * @return
	 */
	private Element createJoinCondStatement(List<Join> joinList) {

		Element savedJoinCondBlock = null;
		Element basicJoinCondBlock = null;

		for (int i = 0; i < joinList.size(); i++) {

			if (i < (joinList.size() - 1)) {
				// More than one map, but this is not the last one

				basicJoinCondBlock = createJoinCondBlock(joinList.get(i));

				/*
				 * If this is the first graph, there will be no saved graph
				 */
				if (savedJoinCondBlock != null) {
					basicJoinCondBlock.appendChild(savedJoinCondBlock);
				}

				/*
				 * This is not the last graph, so use a <next> block
				 */
				basicJoinCondBlock = putInNextElement(basicJoinCondBlock);

				/*
				 * Save this for the next iteration
				 */
				savedJoinCondBlock = basicJoinCondBlock;

			} else if (i == (joinList.size() - 1)) {
				// Only one map, or this is the last one

				basicJoinCondBlock = createJoinCondBlock(joinList.get(i));

				/*
				 * If this is the only map, then defend against
				 * savedPredMapBlock being null (as initialised).
				 */
				if (savedJoinCondBlock != null) {

					/*
					 * Recall, any savedGraphBlock will already be surrounded by
					 * a <next> block
					 */
					basicJoinCondBlock.appendChild(savedJoinCondBlock);

				}

			}

		}

		/*
		 * This needs to be in a <statement> element
		 */
		Element joinCondStatement = xml.createElement(CONST.STATEMENT);
		joinCondStatement.setAttribute(CONST.NAME, CONST.JOINCONDITION);
		joinCondStatement.appendChild(basicJoinCondBlock);

		return joinCondStatement;

	}

	/**
	 * Used for Join conditions in ReferenceObjects
	 * 
	 * 
	 * <block type="joincondition"> <field name="CHILD">join_2</field>
	 * <field name="PARENT">join_2</field> </block>
	 * 
	 * @return
	 */
	private Element createJoinCondBlock(Join jn) {

		String childStr = jn.getChild();
		String parentStr = jn.getParent();

		Element joinChildField = createFieldElement(CONST.CHILD_UC, childStr);
		Element joinParentField = createFieldElement(CONST.PARENT_UC, parentStr);

		Element joinBlockElem = xml.createElement(CONST.BLOCK);
		joinBlockElem.setAttribute(CONST.TYPE, CONST.JOINCONDITION);
		joinBlockElem.appendChild(joinChildField);
		joinBlockElem.appendChild(joinParentField);

		return joinBlockElem;
	}

}
