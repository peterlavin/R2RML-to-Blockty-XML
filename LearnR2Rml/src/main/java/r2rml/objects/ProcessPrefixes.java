package r2rml.objects;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;

import xmlutilities.PrefixesToXML;

public class ProcessPrefixes {

	Document xml;

	public ProcessPrefixes(Document xml) {

		this.xml = xml;

	}

	public Document processPrefixes(Map<String, String> pmap) {

		PrefixesToXML ptx = new PrefixesToXML(xml);

		/*
		 * R2RML will have at least one prefix (rr=http://www.w3.org/ns/r2rml#)
		 * plus any added ones. The r2rml one is ignored, all subsequent ones
		 * are added here
		 *
		 * This code puts each prefix in its own <statement name="mapping">
		 * element, no <next> elements are used.
		 * 
		 * A (Java) Map is used so order may not be preserved.
		 */

		for (Entry<String, String> value : pmap.entrySet()) {

			String prefix = value.getKey().toString();
			String prefixURI = value.getValue().toString();

			/*
			 * Filter out the prefix... http://www.w3.org/ns/r2rml
			 */
			if (!prefixURI.contains("http://www.w3.org/ns/r2rml")) {

				ptx.insertPrefix(prefix, prefixURI);

			}

		}

		return xml;
	}

}
