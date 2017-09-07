package r2rml.objects;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.model.PredicateObjectMap;
import r2rml.model.TriplesMap;

public class ProcessPredicateObjectMaps {

	private Document xml;

	public ProcessPredicateObjectMaps(Document xml) {
		
		this.xml = xml;
		
		
		
	}

	public Document processPredicateObjectMaps(TriplesMap tmap, Element tripmapStatementElem) {
		
		
		
		List<PredicateObjectMap> pomList = tmap.getPredicateObjectMaps();
		
		for(PredicateObjectMap pom : pomList){
			
			
			
		}
		
		
		
		
		
		
		
		
		return this.xml;
	}

}
