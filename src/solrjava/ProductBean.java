package solrjava;

public class ProductBean {
	String id = null;
    String[] fields;
    String[] values;
    
	public ProductBean(String f, String v) {
		//System.out.println(fields.toString());
		f=f.substring(1, f.length()-2);
		fields = f.split(", ");
		
		v=v.substring(1, v.length()-2);
		values = v.split(", ");
		
		for(int i=0; i<fields.length; i++) {
			if(fields[i].equals("id")) {
				id = values[i];
				i=fields.length+1;
			}
		}
		/*for(int i=0; i<fields.size(); i++) {
			if(fields.equals("id")) {
				this.id = values.toString().toString();
			}
			else {
				System.out.println(fields[i].toString());
				this.fields.add(fields[i].toString());
				this.values.add(values[i].toString());
			}
		}*/
	}

	public String getId() {
		return id;
	}
	
	public String getValue(int n) {
		return this.values[n];
	}
	
	public String getField(int n) {
		return this.fields[n];
	}
	
	public int numFields() {
		return fields.length;
	}
}