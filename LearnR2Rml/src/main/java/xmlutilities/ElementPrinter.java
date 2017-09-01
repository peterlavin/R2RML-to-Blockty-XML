package xmlutilities;

import org.w3c.dom.Element;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

public class ElementPrinter {
	
	public ElementPrinter(){
		
		
		
	}
	
	public void printElement(Element elem){
		
		
		

		TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String strObject = result.getWriter().toString();
        
        System.out.println("\n\n" + strObject + "\n\n");
		
		
	}
	

}
