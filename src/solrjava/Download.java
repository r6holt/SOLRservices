package solrjava;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Download {
	
	public Download() {}

	public void acceptDownload(String[] fields, String[]values, String id, File f) {

	  try {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document document = docBuilder.newDocument();
		Element rootElement = document.createElement("add");
		document.appendChild(rootElement);

		// staff elements
		Element doc = document.createElement("doc");
		rootElement.appendChild(doc);
		
		// set attributes
		for(int i=0; i<fields.length; i++) {
			doc.setAttribute(fields[i], values[i]);
		}
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		System.out.println(f.toString());
		StreamResult result = new StreamResult(new File(f.toString()+"\\"+id+".xml"));

		transformer.transform(source, result);

		JOptionPane.showMessageDialog(new JFrame(), "Download Complete!");

	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	}
}