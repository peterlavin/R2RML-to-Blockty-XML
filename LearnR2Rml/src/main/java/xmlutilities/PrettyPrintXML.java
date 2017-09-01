package xmlutilities;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PrettyPrintXML {

	public static void printDocument(Document xml){

		Transformer tf = null;
		try {
			tf = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformerFactoryConfigurationError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		tf.setOutputProperty(OutputKeys.INDENT, "yes");

		tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		Writer out = new StringWriter();

		/*
		 * Added to close empty elements with full tag, needed for
		 * <mutation></mutation> syntax in logical table elements
		 */
		tf.setOutputProperty(OutputKeys.METHOD, "html");

		try {
			tf.transform(new DOMSource(xml), new StreamResult(out));
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		System.out.println(out.toString());

	}

	public static void printElement(Element elem) {

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer tf = null;
		try {
			tf = transformerFactory.newTransformer();

			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			tf.setOutputProperty(OutputKeys.INDENT, "yes");

			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(elem);
		StreamResult result = new StreamResult(new StringWriter());

		try {
			tf.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		String strObject = result.getWriter().toString();

		// Trim off the following... <?xml version="1.0" encoding="UTF-8"?>
		strObject = strObject.substring(38);

		System.out.println("\n" + strObject + "\n");

	}

}
