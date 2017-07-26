package solrjava;

import java.io.IOException;
import javax.swing.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

public class Remove {
	public Remove() {}
	
	public int acceptRemove() throws SolrServerException, IOException {
		String urlString = "http://localhost:8900/solr/solrservices";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
        
        String index = (String)JOptionPane.showInputDialog(
                new JFrame("Remove"), "Select the ID you would like to remove:",
                "Remove",
                JOptionPane.PLAIN_MESSAGE);
        
        if(index==null || index.trim().equals("")) {
        	return 0;
        }
        else if(solr.getById(index)==null) {
        	return -1;
        }
        solr.deleteById(index);
        
        solr.commit();
        return 1;
	}
}
