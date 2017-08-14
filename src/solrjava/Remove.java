package solrjava;

import java.io.IOException;
import javax.swing.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

public class Remove {
	public Remove() {}
	
	public void acceptRemove(String id) throws SolrServerException, IOException {
        HttpSolrClient solr = new HttpSolrClient.Builder(GUI.urlString).build();
        solr.setParser(new XMLResponseParser());
        
        if(solr.getById(id)!=null) {
        	solr.deleteById(id);
        	
        	solr.commit();
            JOptionPane.showMessageDialog(new JFrame(), "Document with id:"+id+" has been deleted...");
        }
	}
}
