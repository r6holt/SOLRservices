package solrjava;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;


public class GUI {
	//frame
	JFrame frame = new JFrame("SOLR Search Services");
	
	//search
	JPanel north = new JPanel(new FlowLayout());
	JTextField searchbar = new JTextField(20);
	JButton search = new JButton("Search");
	
	//display area
	JPanel displayPan = new JPanel(new FlowLayout());
	JTextArea display = new JTextArea(10, 40);
	JScrollPane scroll;
	
	//document management
	JPanel manage = new JPanel(new FlowLayout());
	JButton addDoc = new JButton("Add Document");
	JButton deleteDoc = new JButton("Delete Document");
	JButton refresh = new JButton("Refresh");
	
	//constructor for GUI
	public GUI() throws SolrServerException, IOException {
		setup();
		addButtons();
		management();
		//new Server();
	}
	
	//setup for the GUI frame
	public void setup() {
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setVisible(true);
	}
	
	public void addButtons() {
		//adds listener to the "Search" button
		search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)  {
				Query query = new Query();
				SolrDocumentList results = null;
				try {
					System.out.println(searchbar.getText());
					results = query.acceptQuery(searchbar.getText());
				} catch (SolrServerException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if(results==null || results.getNumFound()==0) {
					display.setText("\t\tNo results");
				}
				else {
					String docs = results.toString();
					
					//formatting results
					docs = docs.replaceAll("SolrDoc", "\nSolrDoc");
					docs = docs.replaceAll(", ", "\n\t     ");
					docs = docs.replaceAll("}", "\n");
					
					//display results
					display.setText(docs);
				}
				updateDisplay();
			}
		});
		
		//setup for display
		display.setEnabled(false);
		display.setFont(display.getFont().deriveFont(24f));
		
		//setup for display scroll
		scroll = new JScrollPane(display);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    displayPan.add(scroll);
	    
	    //add components to the frame
		north.add(searchbar);
		north.add(search);
		manage.add(addDoc);
		manage.add(deleteDoc);
		manage.add(refresh);
		frame.add(north, BorderLayout.NORTH);
		frame.add(manage, BorderLayout.CENTER);
		frame.add(displayPan, BorderLayout.SOUTH);
		
		//fit frame to component size and reveal
		frame.pack();
		frame.setVisible(true);
	}
	
	public void management() {
		//when add doc is clicked:
		addDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("SOLR compatible","xml");
				
				fc.setFileFilter(filter);
				
				int returnVal = fc.showOpenDialog(fc);
				File f = fc.getSelectedFile();
				Index adder;
				try {
					adder = new Index();
					adder.acceptDocument(f);
				} catch (SolrServerException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		//when delete doc is clicked:
		deleteDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Remove remover = new Remove();
				int status=0;
				try {
					status = remover.acceptRemove();
				} catch (SolrServerException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if(status == -1) {
					display.setText("\tRemoval Failed - Invalid ID");
				}
				else if(status == 1) {
					display.setText("\tRemoval Succesful");
				}
				else {
					display.setText("\tNo removal occured");
				}
			}
		});
		
		//when refresh is clicked:
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new Refresh();
				} catch (SolrServerException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	//updates frame for new content
	public void updateDisplay() {
		frame.setVisible(true);
		frame.pack();
	}
}
