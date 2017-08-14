package solrjava;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.solr.client.solrj.SolrServerException;

public class runTests {
	public static void main(String []args) throws SolrServerException, IOException {
		
		Index index = new Index(new SchemaEditor());
		
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println( "Begin..."+sdf.format(cal.getTime()) );
		
		index.exampleDocs();

		cal = Calendar.getInstance();
		System.out.println( "Finish..."+sdf.format(cal.getTime()) );
		
	}
}
