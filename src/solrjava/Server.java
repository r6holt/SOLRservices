package solrjava;

import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class Server {
	public Server() {}
	
	public int tryPort () {
		
		try {	
	        HttpSolrClient solr = new HttpSolrClient.Builder(GUI.urlString).build();
	        
	        solr.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
}
