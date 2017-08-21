package solrjava;

import java.io.File;
import java.util.ArrayList;

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

	public void acceptDownload(ArrayList<String> fields, ArrayList<Object> values, String id, File f) {

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
		for(int i=0; i<fields.size(); i++) {
			Element field = document.createElement("field");
			
			if(fields.get(i).equals("_version_")) {}
			else if(values.get(i).getClass()==java.util.Date.class) {
				String[] items = values.get(i).toString().split(" ");
				String date = "";
				
				date+=items[5]+"-";
				String monthString = "0";
				String month= items[1];
				
				switch (month) {
		           case "Jan":  monthString += "1";
	                        break;
		           case "Feb":  monthString += "2";
		                    break;
		           case "Mar":  monthString += "3'";
		                    break;
		           case "Apr":  monthString += "4";
		                    break;
		           case "May":  monthString += "5";
		                    break;
		           case "Jun":  monthString += "6";
		                    break;
		           case "Jul":  monthString += "7";
		                    break;
	               case "Aug":  monthString += "8";
	                        break;
	               case "Sep":  monthString += "9";
	                        break;
	               case "Oct": monthString = "10";
	                        break;
	               case "Nov": monthString = "11";
	                        break;
	               case "Dec": monthString = "12";
	                        break;
	               default: monthString = "Invalid month";
	                        break;
				}
				
				date+=monthString+"-";
				date+=items[2]+"T";
				
				String[] time = items[3].split(":");
				date+= ((Integer.parseInt(time[0])+8)%24)+":";
				date+=time[1]+":"+time[2]+"Z";
				
				field.setAttribute("name", fields.get(i));
				field.setTextContent(date);
			}
			else {
				field.setAttribute("name", fields.get(i));
				field.setTextContent(values.get(i)+"");
			}
			
			doc.appendChild(field);
		}
		doc.removeChild(doc.getLastChild());
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
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