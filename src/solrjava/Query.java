package solrjava;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class Query {
	private long FOUND = 0;
	private FieldTracker ft;
	private int searches = 0;
	private List<FacetField> facet;
	
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
        query.set("hl", "on");
        query.setFacet(true);
        query.set("facet.field", "cat", "price");
        
        //spatial
        query.set("pt", "");
        query.set("sfield", "");
        query.set("d", "");
        
        if(ft!=null && ft.getPriceQuery()) {
        	if(ft.getMinPrice()==-1) {
        		query.set("fq", "-price:["+ft.getMaxPrice()+" TO *]");
        	}
        	else if(ft.getMaxPrice()==-1) {
        		query.set("fq", "-price:[* TO "+ft.getMinPrice()+"]");
        	}
        	else {
        		query.set("fq", "-price:[* TO "+ft.getMinPrice()+"] -price:["+ft.getMaxPrice()+" TO *]");
        	}
        }
        
        if(ft.getCategory()!="null") {
        	if(q.trim().equals("")) {
        		query.setQuery(ft.getCategory()+":*");
        	}
        	else if(ft.getCategory().equals("price")) {
        		query.setQuery("price:["+q+" TO "+q+"]");
        	}
        	else {
        		query.setQuery(ft.getCategory()+":*"+q+"*");
        	}
        }
        
        if(!ft.getFacetchoice().equals("")) {
        	query.set("fq", "cat:"+ft.getFacetchoice());
        }
        if(!ft.getPricechoice().equals("")) {
        	query.set("fq", "price:"+ft.getPricechoice());
        }
        
        //Getting results
        ArrayList<ProductBean> beans = new ArrayList<ProductBean>();
        try {
        	QueryResponse resp = solr.query(query);
        	facet = resp.getFacetFields();
        	SolrDocumentList list =resp.getResults();
        	FOUND = list.getNumFound();
        	searches+=1;
        	
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
    
    public List<FacetField> getFacet() {
		return facet;
	}

	public long getFOUND() {
		return FOUND;
	}

	public ProductBean createBean(SolrDocument sd) {
		Iterator<String> fields = sd.getFieldNames().iterator();
		@SuppressWarnings("rawtypes")
		Iterator values = sd.values().iterator();
		
    	ProductBean b = new ProductBean(fields, values);
    	
    	return b;
    	
    }
    
    public void updateFT(FieldTracker f) {
    	this.ft=f;
    }
    
    public void addSearch() {
    	searches+=1;
    }
    
    public int getSearches() {
    	return searches;
    }
}