package solrjava;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.common.SolrInputDocument;


public class Index {
    public Index() throws SolrServerException, IOException {}
    
    public int acceptDocument(File f) throws SolrServerException, IOException {
    	//Connect to server
    	String urlString = "http://localhost:8900/solr/solrservices";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        
        String name = f.getName();
        if(name.length()<5) {
        	return -1;
        }
        
        //JSON file addition
        if(name.substring(name.length()-5).equals(".json")) {
        	return 2;
        }
        //CSV file addition
        if(name.substring(name.length()-4).equals(".csv")) {
        	return 2;
        }
        //XML file addition
        if(name.substring(name.length()-4).equalsIgnoreCase(".xml")) {
            solr.setParser(new XMLResponseParser());
            InputStream is = new FileInputStream(f);
            @SuppressWarnings("deprecation")
    		String xml = IOUtils.toString(is);
            
        	DirectXmlRequest xmlreq = new DirectXmlRequest( "/update", xml); 
        	
        	solr.request(xmlreq);
        }
        
    	solr.commit();
    	return 1;
    }
    
    public void exampleDocs() throws SolrServerException, IOException {
    	Random r = new Random();
    	String urlString = "http://localhost:8900/solr/solrservices";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
        
        //Adding Documents
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "fruit-1");
        document.addField("name", "cherry");
        //document.addField("TEST", 18);
        document.addField("price", 86);
        solr.add(document);
        
        //Commit Changes
        solr.commit();
        
        for(int i=0;i<1000; i++) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("cat", "book");
            doc.addField("id", "book-" + i);
            doc.addField("blah", "total leg-1."+i);
            doc.addField("name", "The Legend of the Hobbit part " + i);
            doc.addField("NYAA", "fresh like always");
            doc.addField("price", r.nextInt(400));
            //doc.addField("TEST", 152);
            solr.add(doc);
            if(i%100==0) solr.commit();  // periodically flush
          }
          solr.commit(); 
    }
}