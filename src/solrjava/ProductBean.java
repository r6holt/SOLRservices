package solrjava;
import org.apache.solr.client.solrj.beans.Field;

public class ProductBean {
	String id;
    String name;
    String price;
	public ProductBean(String i, String n, String p) {
		id=i;
		name=n;
		price=p;
	}
 
    @Field("id")
    protected void setId(String id) {
        this.id = id;
    }
 
    @Field("name")
    protected void setName(String name) {
        this.name = name;
    }
 
    @Field("price")
    protected void setPrice(String price) {
        this.price = price;
    }

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPrice() {
		return price;
	}
 
    // getters and constructor omitted for space
}