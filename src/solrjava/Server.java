package solrjava;

import javax.swing.*;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;

import java.awt.*;
import java.io.IOException;

public class Server {
	public Server() throws SolrServerException, IOException {
		JFrame frame = new JFrame("Server");
		JLabel port = new JLabel("Server Port Number: ");
		JLabel core = new JLabel("Core Name: ");
		
		frame.setLayout(new FlowLayout());
		
		
		String urlString = "http://localhost:8980/solr/mycore";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        
        solr.setParser(new XMLResponseParser());
        
        //Adding Documents
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "389hf398ry");
        document.addField("name", "cherry");
        document.addField("price", "50.0");
        solr.add(document);
        
        //Remember to commit your changes!
        solr.commit();
        
        for(int i=0;i<100;++i) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("cat", "book");
            doc.addField("id", "book-" + i);
            doc.addField("name", "The Legend of the Hobbit part " + i);
            solr.add(doc);
            if(i%100==0) solr.commit();  // periodically flush
          }
          solr.commit(); 
          
          //Beans
          solr.addBean( new ProductBean("888", "Apple iPhone 6s", "299.99") );
          solr.commit();
	}
	
	public static void main(String []args) throws SolrServerException, IOException {
		new Remove();
	}
}
