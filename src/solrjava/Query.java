package solrjava;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.schema.SchemaRequest.SchemaName;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class Query {
	public static long FOUND = 0;
	private FieldTracker ft;
	
    public Query(FieldTracker ft) {
    	this.ft=ft;
    }
    
    public ArrayList<ProductBean> acceptQuery(String q, int START, int ROWS) throws SolrServerException, IOException{
    	String urlString = "http://localhost:8900/solr/solrservices";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());
        
        //Starting up a query
        SolrQuery query = new SolrQuery();
        
        //Adding fields to the query
        query.setQuery("*"+q+"*");
        query.set("rows", ROWS);
        query.set("start", START);
        query.set("sort", ft.getSortfield()+" "+ft.isSort());
        
        if(ft!=null && ft.getPriceQuery()) {
        	query.set("fq", "-price:[* TO "+ft.getMinPrice()+"] -price:["+ft.getMaxPrice()+" TO *]");
        }
        
        if(ft.getCategory()!="null") {
        	if(q.trim().equals("")) {
        		
        	}
        	else if(ft.getCategory().equals("price")) {
        		query.setQuery("price:["+q+" TO "+q+"]");
        	}
        	else {
        		query.setQuery(ft.getCategory()+":*"+q+"*");
        	}
        }
        
        //Getting results
        ArrayList<ProductBean> beans = new ArrayList<ProductBean>();
        try {
        	QueryResponse resp = solr.query(query);
        	SolrDocumentList list =resp.getResults();
        	FOUND = list.getNumFound();
        	
        	for(int i=0; (i<list.size() && i<list.getNumFound()); i++) {
        		beans.add(createBean(list.get(i)));
        	}
        	
        	
        	return beans;
        }
        catch (RemoteSolrException e1) {
        	return beans;
        }
        catch (Exception e) {
        	System.out.println("ERROR");
        	e.printStackTrace();
        	return beans;
        }
        
        
    }
    
    public ProductBean createBean(SolrDocument sd) {
    	ProductBean b = new ProductBean(sd.getFieldNames().toString(), sd.values().toString());
    	
    	return b;
    	
    }
    
    public void updateFT(FieldTracker f) {
    	this.ft=f;
    }
}