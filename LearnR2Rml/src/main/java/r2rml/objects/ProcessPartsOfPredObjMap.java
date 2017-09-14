package r2rml.objects;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.constants.CONST;
import r2rml.model.GraphMap;
import r2rml.model.ObjectMap;
import r2rml.model.PredicateMap;
import r2rml.model.PredicateObjectMap;
import xmlutilities.PrettyPrintXML;

public class ProcessPartsOfPredObjMap {

	private Document xml;

	public ProcessPartsOfPredObjMap(Document xml) {

		// Constructor
		this.xml = xml;

	}

	public void processPartsPredObjMap(Element predObjBlock, PredicateObjectMap pom) {

		Element pogBlock = null;

		/*
		 * A PredicateObjectMap must contain at least one Predicate Map and at
		 * least one Object Map. Graphs are not always present.
		 */

		Element predMapStatement = createPredicateMap(pom.getPredicateMaps());
		predObjBlock.appendChild(predMapStatement);

		Element objectMapStatement = createObjMap(pom.getObjectMaps());
		predObjBlock.appendChild(objectMapStatement);

		if (!pom.getGraphMaps().isEmpty()) {

			Element graphMapsStatement = createGraphMap(pom.getGraphMaps());
			predObjBlock.appendChild(graphMapsStatement);

		} 

	}

	/*
	 * HELPER METHODS TO CREATE THE ELEMENTS
	 */
	
	/**
	 * Creates a Predicate Map element which can contain
	 * one or more (nested) Predicate Map elements
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
				basicPredMapBlock = putBlockInNext(basicPredMapBlock);

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
	 * Creates an Object Map element with one or more (nested)
	 * Object Maps.
	 * 
	 * @param objectMaps
	 * @return
	 */
	private Element createObjMap(List<ObjectMap> objectMaps) {
		
		Element savedObjectMapBlock = null;
		Element basicObjectMapBlock = null;

		for(int i = 0; i < objectMaps.size(); i++){
			
			ObjectMap om = objectMaps.get(i);


//						if(!om.getDatatypes().isEmpty()){
//							// create termtype block as literal and datatype
//							System.out.println("om.getDatatypes " + getResourcePrefix(om.getDatatypes().get(0).getObject().asResource()));
//						} else {
//							System.out.println("Nothing from om.getDatatypes" + " " +i);
//						}
//						
//						if(!om.getLanguages().isEmpty()){
//							// create termtype block as literal and language
//							System.out.println("om.getLanguages " + om.getLanguages().get(0).getObject());
//						} else {
//							System.out.println("Nothing from om.getLanguages "  + " " +i + " size " + om.getLanguages().size());
//						}
			
						
						
						

			


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
				basicObjectMapBlock = putBlockInNext(basicObjectMapBlock);

				/*
				 * Save this for the next iteration
				 */
				savedObjectMapBlock = basicObjectMapBlock;
				
				
			
				
			} else if (i == (objectMaps.size() - 1)){
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
		objectMapStatement.appendChild(basicObjectMapBlock);

		System.out.println("objectMapStatement...");
		PrettyPrintXML.printElement(objectMapStatement);
		
		return objectMapStatement;
		
		

	}

	


	/**
	 * Creates a Graph Map element for one or
	 * more (nested) Graph Maps.
	 * 
	 * @param graphMaps
	 * @return
	 */
	private Element createGraphMap(List<GraphMap> graphList) {
		
		System.out.println("gList size... " + graphList.size());

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
				basicGraphBlock = putBlockInNext(basicGraphBlock);

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
				// System.out.println("subjGraphBlock for LAST GRAPH...\n");
				// PrettyPrintXML.printElement(basicGraphBlock);

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
	private Element putBlockInNext(Element blockElm) {

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
	 * Creates a basic block for a Graph Map, as part
	 * of a Predicate Object Map
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
	 * Creates a basic block for an Object Map, as part of
	 * a Predicate Object Map
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
			 * Prefix:name set need for constant only
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
		 * Check for literal types, if just a literal, make...
		 * 
		 * 
                        <statement name="termmap">
                          <block type="objecttermtype">
                            <field name="TERMTYPE">termtypeliteral</field>
                          </block>
                        </statement>
		 * 
		 * 
		 * if a language *OR* datatype is set, make...
		 * 
		 *          <value name="termtypevalue">
                      <block type="objectdatatype">
                        <field name="DATATYPE">xsd:string</field>
                      </block>
                    </value>
		 */
		
		if(om.isTermTypeLiteral()){
			
			Element fieldLitTermMap = xml.createElement(CONST.FIELD);
			fieldLitTermMap.setAttribute(CONST.NAME, CONST.TERMTYPE_UC);
			fieldLitTermMap.appendChild(xml.createTextNode(CONST.TERMTYPELITERAL));

			Element objTermTypeBlock = xml.createElement(CONST.BLOCK);
			objTermTypeBlock.setAttribute(CONST.TYPE, CONST.OBJECTTERMTYPE);
			objTermTypeBlock.appendChild(fieldLitTermMap);
			
			System.out.println("Before dealing with lang/data");
			PrettyPrintXML.printElement(objTermTypeBlock);
			
			/*
			 * If there is a datatype or language set,
			 * add a <value> element for this, append
			 * to block also
			 */
			
			if(!om.getLanguages().isEmpty()){
				
				
				Element fieldLitLanguage = xml.createElement(CONST.FIELD);
				fieldLitLanguage.setAttribute(CONST.NAME, CONST.LANGUAGE_UC);
				
				String languageValue = om.getLanguages().get(0).getObject().toString();
				fieldLitLanguage.appendChild(xml.createTextNode(languageValue));
				
				Element objLanguageBlock = xml.createElement(CONST.BLOCK);
				objLanguageBlock.setAttribute(CONST.TYPE, CONST.OBJECTLANGUAGE);
				objLanguageBlock.appendChild(fieldLitLanguage);
				
				// Put this in a <value> element TODO, can be reused as a method
				Element termTypeValue = xml.createElement(CONST.VALUE);
				termTypeValue.setAttribute(CONST.TYPE, CONST.TERMTYPEVALUE);
				termTypeValue.appendChild(objLanguageBlock);
				
				objTermTypeBlock.appendChild(termTypeValue);
				// put in <statement name="termmap">
				
				Element termMapStatement = xml.createElement(CONST.STATEMENT);
				termMapStatement.setAttribute(CONST.NAME, CONST.TERMMAP);
				termMapStatement.appendChild(objTermTypeBlock);
				
				
				
				System.out.println("After dealing wt language");
				PrettyPrintXML.printElement(termMapStatement);
				
			} else if (!om.getDatatypes().isEmpty()){
				
				Element fieldLitDatatype = xml.createElement(CONST.FIELD);
				fieldLitDatatype.setAttribute(CONST.NAME, CONST.DATATYPE_UC);
				
				String datatypeValue = getResourcePrefix(om.getDatatypes().get(0).getObject().asResource());
				fieldLitDatatype.appendChild(xml.createTextNode(datatypeValue));
				
				Element objLanguageBlock = xml.createElement(CONST.BLOCK);
				objLanguageBlock.setAttribute(CONST.TYPE, CONST.OBJECTDATATYPE);
				objLanguageBlock.appendChild(fieldLitDatatype);
				
				// Put this in a <value> element TODO, can be reused as a method
				Element termTypeValue = xml.createElement(CONST.VALUE);
				termTypeValue.setAttribute(CONST.TYPE, CONST.TERMTYPEVALUE);
				termTypeValue.appendChild(objLanguageBlock);
				
				objTermTypeBlock.appendChild(termTypeValue);
				// put in <statement name="termmap">
				
				Element termMapStatement = xml.createElement(CONST.STATEMENT);
				termMapStatement.setAttribute(CONST.NAME, CONST.TERMMAP);
				termMapStatement.appendChild(objTermTypeBlock);
				
				System.out.println("After dealing wt datatype");
				PrettyPrintXML.printElement(termMapStatement);
				
			}
			
			// put in <statement name="termmap">
			
			
			
			
		} else if (false){
			
			//TODO, deal with blank node
			
		}
		

		
		/*
		 * Append all this to a <block type="subjectgraphtermap">
		 */
		Element pomObjectMapBlock = xml.createElement(CONST.BLOCK);
		
		pomObjectMapBlock.setAttribute(CONST.TYPE, CONST.OBJECTMAP);
		pomObjectMapBlock.appendChild(fieldTermMap);
		pomObjectMapBlock.appendChild(fieldTermMapValue);

		return pomObjectMapBlock;

	}
	
}