package xmlutilities;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class PrettyPrintXML {

	
	public static void printXml(Document xml) throws TransformerException{
		
		Transformer tf = TransformerFactory.newInstance().newTransformer();

		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		tf.setOutputProperty(OutputKeys.INDENT, "yes");

		tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		Writer out = new StringWriter();
		
		/*
		 * Added to close empty elements with full tag, needed
		 * for <mutation></mutation> syntax in logical table elements
		 */
		tf.setOutputProperty(OutputKeys.METHOD, "html");

		tf.transform(new DOMSource(xml), new StreamResult(out));
		
		System.out.println(out.toString());
		
		
		
	}
	

}
