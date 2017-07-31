package solrjava;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

public class runApp {
	public static void main(String []args) throws SolrServerException, IOException {
		/*Index index = new Index();
		try {
			index.exampleDocs();
		}catch(IOException | SolrServerException e) {
			
		}
		System.out.println("Complete...");
		System.out.println(System.currentTimeMillis());*/
		new GUI();
		
	}
}
