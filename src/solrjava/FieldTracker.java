package solrjava;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.util.SimpleOrderedMap;

public class FieldTracker {
	
	private ArrayList<String> fields = new ArrayList<String>();
	
	private boolean price = false;
	private int minPrice;
	private int maxPrice;
	private boolean priceQuery;
	
	private String category;
	
	private String sortfield;
	private String sort;

	private HttpSolrClient solr;

	//init FieldTracker to keep track of fields and sorting
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FieldTracker() throws SolrServerException, IOException {
		String urlString = "http://localhost:8900/solr/solrservices";
        solr = new HttpSolrClient.Builder(urlString).build();
        
	    SolrQuery query = new SolrQuery();
	    query.add(CommonParams.QT, "/schema/fields");
	    QueryResponse response = solr.query(query);
	    ArrayList<SimpleOrderedMap> f = (ArrayList<SimpleOrderedMap>) response.getResponse().get("fields");
	    
	    for (SimpleOrderedMap field : f) {
	        fields.add(field.get("name").toString());
	        if(field.get("name").toString().equals("price")) {
	        	price=true;
	        }
	        
	    }

	}
	
	
	//updates fields on the core
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update() throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();
		
	    query.add(CommonParams.QT, "/schema/fields");
	    QueryResponse response = solr.query(query);
	    ArrayList<SimpleOrderedMap> f = (ArrayList<SimpleOrderedMap>) response.getResponse().get("fields");
	    
	    for (SimpleOrderedMap field : f) {
	        fields.add(field.get("name").toString());
	        if(field.get("name").toString().equals("price")) {
	        	price=true;
	        }	        
	    }
	}
	
	public void addField(String f) {
		fields.add(f);
	}

	public boolean getPrice() {
		return price;
	}
	
	public int getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}


	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}
	
	public int numFields() {
		return fields.size();
	}
	
	public String getField(int n) {
		return fields.get(n);
	}
	
	public void setPriceQuery(boolean b) {
		priceQuery=b;
	}
	
	public boolean getPriceQuery() {
		return priceQuery;
	}
	
	public String getSortfield() {
		return sortfield;
	}

	public void setSortfield(String sortfield) {
		this.sortfield = sortfield;
	}

	public String isSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}
