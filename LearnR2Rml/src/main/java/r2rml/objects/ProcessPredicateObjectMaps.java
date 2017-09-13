package r2rml.objects;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.constants.CONST;
import r2rml.model.GraphMap;
import r2rml.model.ObjectMap;
import r2rml.model.PredicateMap;
import r2rml.model.PredicateObjectMap;
import r2rml.model.TermMap;
import r2rml.model.TriplesMap;

public class ProcessPredicateObjectMaps {

	private Document xml;

	public ProcessPredicateObjectMaps(Document xml) {
		
		this.xml = xml;
		
		
		
	}

	public void processPredicateObjectMaps(TriplesMap tmap, Element tripmapStatementElem) {
		
		
		
		List<PredicateObjectMap> pomList = tmap.getPredicateObjectMaps();
		
		Element predObjBlock = null;
		Element savedPredObjBlock = null;
		
		
		/*
		 * There can be multiple Predicate Object Maps, each
		 * one needs to be nested in <next> elements in the XML
		 */
		for(int i = 0; i < pomList.size(); i++){
	
			PredicateObjectMap pom = pomList.get(i);
			
			/*
			 * Create a basic POM statement and inner block
			 */

			if(i < (pomList.size() - 1)){
				/*
				 * There is more than one PredicateObjectMap, and the current one is
				 * any except not the last one. Therefore, <next> blocks are required.
				 */
				
				
				predObjBlock = createPomBlock();
				
				/*
				 * Add parts for this pom here
				 */
				ProcessPartsOfPredObjMap ppom = new ProcessPartsOfPredObjMap(xml); 
				ppom.processPartsPredObjMap(predObjBlock, pom);
				
				predObjBlock = putBlockInNext(predObjBlock);
				
				if(savedPredObjBlock != null){
					predObjBlock.appendChild(savedPredObjBlock);
				}
				
				savedPredObjBlock = predObjBlock;
				
				
			} else if(i == (pomList.size() - 1)){
				/*
				 * This is either the only POM, or 
				 * the last of many, no <next> required
				 */
				
				predObjBlock = createPomBlock();
				
				/*
				 * Add parts for this pom here
				 */
				ProcessPartsOfPredObjMap ppom = new ProcessPartsOfPredObjMap(xml); 
				ppom.processPartsPredObjMap(predObjBlock, pom);
				
				/*
				 * Add all previously <next>'ed POMs
				 */
				
				if(savedPredObjBlock != null){
					
					predObjBlock.appendChild(savedPredObjBlock);
					
				}
				
				
			}
			


		}
		
	
		/*
		 * Append the (possible multiple/nested POM elements
		 * to the tripmapStatementElem pass in as an arg.
		 * 
		 * There should always be a POM object for each
		 * TriplesMap, if stm is used for sanity check only
		 */
		
		if(predObjBlock != null){
			
			Element predObjStatement = putBlockInStatement(predObjBlock);
			
			Element tripMapBlockElem = (Element) tripmapStatementElem.getFirstChild();
			tripMapBlockElem.appendChild(predObjStatement);
			
		} else {
			System.out.println("NO PREDICATE OBJECT MAP ELEMENT FOUND - THIS IS BAD!!!!!!!");
		}
		
		
		
	}

	/*
	 * Create an empty Predicate Object Map Statement and
	 * inner block
	 */
	private Element createPomBlock() {
		
		Element predObjStmBlock = xml.createElement(CONST.BLOCK);
		predObjStmBlock.setAttribute(CONST.TYPE, CONST.PREDICATEOBJECTMAP);
		
	//	Element predOjbMapStm = xml.createElement(CONST.STATEMENT);
	//	predOjbMapStm.setAttribute(CONST.NAME, CONST.PREDICATEOBJECTMAP);
		
	//	predOjbMapStm.appendChild(predObjStmBlock);
		
	//	return predOjbMapStm;
		
		return predObjStmBlock;
	}
	
	 /* 
	 * Takes any block element and puts it in a <next></next> element
	 */
	private Element putBlockInNext(Element blockElm) {

		Element nextElement = xml.createElement(CONST.NEXT);

		nextElement.appendChild(blockElm);

		return nextElement;

	}
	
	private Element putBlockInStatement(Element predObjBlock){
		
		Element pomStatemmentElm = xml.createElement(CONST.STATEMENT);
		pomStatemmentElm.setAttribute(CONST.NAME, CONST.PREDICATEOBJECTMAP);
		
		pomStatemmentElm.appendChild(predObjBlock);
		
		
		
		return pomStatemmentElm;
	}

}
