package solrjava;

import java.awt.Color;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.solr.client.solrj.SolrServerException;

public class runApp {
	public static void main(String []args) throws SolrServerException, IOException {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            UIManager.put("control", new Color(237, 237, 230));
		            UIManager.put("nimbusOrange", new Color(50, 255, 50));
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		new GUI();
	}
}
