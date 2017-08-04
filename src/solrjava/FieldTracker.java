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
	private ArrayList<String> datatypes = new ArrayList<String>();
	
	private String pickstring = null;
	private String picknum = null;
	
	private boolean price = false;
	private boolean cat = false;
	private int minPrice;
	private int maxPrice;
	private boolean priceQuery;
	
	private String category;
	private String[] facetchoice;
	private ArrayList<String> choices = new ArrayList<String>();
	private String pricechoice = "";
	
	private String sortfield;
	private String sort;
	
	private double lat;
	private double lon;

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
	        datatypes.add(field.get("type").toString());
	        if(field.get("name").toString().equals("price")) {
	        	price=true;
	        }
	        if(field.get("name").toString().contains("cat")) {
	        	cat=true;
	        }
	        
	    }

	}
	
	
	//updates fields on the core
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update() throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();
		fields.clear();
		
	    query.add(CommonParams.QT, "/schema/fields");
	    QueryResponse response = solr.query(query);
	    ArrayList<SimpleOrderedMap> f = (ArrayList<SimpleOrderedMap>) response.getResponse().get("fields");
	    
	    for (SimpleOrderedMap field : f) {
	        fields.add(field.get("name").toString());
	        datatypes.add(field.get("type").toString());
	        if(field.get("name").toString().equals("price")) {
	        	price=true;
	        }
	        if(field.get("name").toString().contains("cat")) {
	        	cat=true;
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
	
	public String[] getFacetchoice() {
		return facetchoice;
	}

	public void setFacetchoice(int i, String s) {
		this.facetchoice[i] = s;
	}
	
	public void setFacetchoice(String[] f) {
		facetchoice=f;
	}

	public String getPricechoice() {
		return pricechoice;
	}

	public void setPricechoice(String pricechoice) {
		this.pricechoice = pricechoice;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}


	public boolean isCat() {
		return cat;
	}


	public String getPickstring() {
		return pickstring;
	}


	public void setPickstring(String pickstring) {
		this.pickstring = pickstring;
	}


	public String getPicknum() {
		return picknum;
	}


	public void setPicknum(String picknum) {
		this.picknum = picknum;
	}


	public ArrayList<String> getDatatypes() {
		return datatypes;
	}


	public void setDatatypes(ArrayList<String> datatypes) {
		this.datatypes = datatypes;
	}


	public ArrayList<String> getChoices() {
		return choices;
	}


	public void setChoices(String s) {
		this.choices.add(s);
	}
	
	
}
