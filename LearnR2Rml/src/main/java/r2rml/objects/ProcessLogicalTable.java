package r2rml.objects;

import java.util.Map;

import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import r2rml.constants.CONST;
import r2rml.model.LogicalTable;
import r2rml.model.TriplesMap;

public class ProcessLogicalTable {

	Document xml;

	public ProcessLogicalTable(Document xml) {
		
		this.xml = xml;
	
	}
	
	public Document processLogicalTable(TriplesMap tmap, Element tripmapStatementElem){
		
		// TODO process the logical table object and update XML, return

		LogicalTable logTable = tmap.getLogicalTable();
		
		/*
		 * Determine if the logical table is a table or SQL query
		 */
		
		
		String mutationVariable= "";
		String tableSqlQueryVariable = "";
		
		if(logTable.getTableName() != null){
			mutationVariable = logTable.getTableName().toString();
			tableSqlQueryVariable = CONST.TABLE;
		} 
		else if(logTable.getSqlQuery() != null) {
			mutationVariable = logTable.getSqlQuery().toString();
			tableSqlQueryVariable = CONST.SQLQUERY;
		} 
		else {
			System.out.println("Something is gone wrong, no table nor sql query found");
		}
		
		
		
		
		Element tripmapBlockElem = (Element) tripmapStatementElem.getFirstChild();

		if(tripmapBlockElem.hasAttribute(CONST.TYPE) && (tripmapBlockElem.getAttribute(CONST.TYPE).equalsIgnoreCase(CONST.TRIPLESMAP))){

			
			/*
			 * Create a logical table statement element for this triplesmap
			 */
			Element logicalTableStatementElem = xml.createElement(CONST.STATEMENT);
			logicalTableStatementElem.setAttribute(CONST.NAME, CONST.LOGICALTABLE);
			
			/*
			 * Create a logical table block element and append it to
			 * logicalTableStatementElem created above
			 */
			Element logicalTableBlockElement = xml.createElement(CONST.BLOCK);
			logicalTableBlockElement.setAttribute(CONST.TYPE, CONST.TABLESQLQUERY);
			// Append the block element to the statement element
			logicalTableStatementElem.appendChild(logicalTableBlockElement);
			
			/*
			 * Add the mutation element
			 */
			//Element logicalTableMutationElem = xml.createElement(CONST.MUTATION);
			Element logicalTableMutationElem = xml.createElement("mutation");
			logicalTableMutationElem.setAttribute(CONST.SQL, mutationVariable);

			/*
			 * Append it to its parent
			 */
			logicalTableBlockElement.appendChild(logicalTableMutationElem);
			
			/*
			 * Add the  field element
			 */
			Element logicalTableFieldElem = xml.createElement(CONST.FIELD);
			logicalTableFieldElem.setAttribute(CONST.NAME, CONST.TABLESQLQUERY_UC);
			logicalTableFieldElem.appendChild(xml.createTextNode(tableSqlQueryVariable));
			
			logicalTableBlockElement.appendChild(logicalTableFieldElem);
			
			tripmapBlockElem.appendChild(logicalTableStatementElem);
			
			
			
		}
		else {
			// TODO, handle this eventuality, at wrong element, throw something
		}
	
		
		return xml;
		
	}
	

	

}
