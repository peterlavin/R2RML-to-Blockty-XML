package r2rml.objects;

import java.util.Map;

import org.apache.jena.rdf.model.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
			tableSqlQueryVariable = "table";
		} 
		else if(logTable.getSqlQuery() != null) {
			mutationVariable = logTable.getSqlQuery().toString();
			tableSqlQueryVariable = "sqlquery";
		} 
		else {
			System.out.println("Something is gone wrong, no table nor sql query found");
		}
		
		
		
		
		Element tripmapBlockElem = (Element) tripmapStatementElem.getFirstChild();

		if(tripmapBlockElem.hasAttribute("type") && (tripmapBlockElem.getAttribute("type").equalsIgnoreCase("triplesmap"))){
			
			System.out.println("Getting into " + tripmapBlockElem.getAttribute("type"));
			
			/*
			 * Create a logical table statement element for this triplesmap
			 */
			Element logicalTableStatementElem = xml.createElement("statement");
			logicalTableStatementElem.setAttribute("name", "logicaltable");
			
			/*
			 * Create a logical table block element and append it to
			 * logicalTableStatementElem created above
			 */
			Element logicalTableBlockElement = xml.createElement("block");
			logicalTableBlockElement.setAttribute("type", "tablesqlquery");
			// Append the block element to the statement element
			logicalTableStatementElem.appendChild(logicalTableBlockElement);
			
			/*
			 * Add the mutation element
			 */
			Element logicalTableMutationElem = xml.createElement("mutation");
			logicalTableMutationElem.setAttribute("sql", mutationVariable);

			/*
			 * Append it to its parent
			 */
			logicalTableBlockElement.appendChild(logicalTableMutationElem);
			
			/*
			 * Add the  field element
			 */
			Element logicalTableFieldElem = xml.createElement("field");
			logicalTableFieldElem.setAttribute("name", "TABLESQLQUERY");
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
