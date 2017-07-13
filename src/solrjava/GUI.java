package solrjava;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.solr.client.solrj.SolrServerException;


public class GUI {
	
	public static int ROWS = 10;
	//frame
	private JFrame frame = new JFrame("SOLR Search Services");
	private JPanel west = new JPanel(new FlowLayout());
	private JPanel east = new JPanel(new FlowLayout());
	
	//search
	private JPanel north = new JPanel(new FlowLayout());
	private JTextField searchbar = new JTextField(20);
	private JButton search = new JButton("Search");
	private JButton addDoc = new JButton("Upload File");
	private JButton deleteDoc = new JButton("Remove By ID");
	private JButton refresh = new JButton("Refresh");
	private JButton examples = new JButton("Examples");
	
	//display area
	private JPanel displayPan = new JPanel();
	private JScrollPane scroll = new JScrollPane(displayPan);
	//private JTextArea display = new JTextArea(10, 30);
	//private JScrollPane scroll;
	
	/*//options
	private JPanel options = new JPanel(new GridLayout(12,1));
	private JPanel advanced = new JPanel(new GridLayout(12,1));
	private JTextField wt = new JTextField("xml", 10);
	private JTextField ls = new JTextField(10);
	private JTextField rows = new JTextField("10", 10);
	private JTextField start = new JTextField("0", 10);
	private JTextField sort = new JTextField(10);
	private JTextField defType = new JTextField("lucene", 10);
	private JTextField fq = new JTextField(20);
	private JTextField facet = new JTextField(20);
	private JTextField rH = new JTextField(20);*/
	
	//info
	private JTextArea info = new JTextArea(12, 30);
	private JScrollPane infoscroll = new JScrollPane(info);
	
	
	//constructor for GUI
	public GUI() throws SolrServerException, IOException {
		int status;
		
		setup();
		management();
		//options();
		addButtons();
		initSearch();
		Server s = new Server();
		status = s.tryPort();
		if(status==-1) {
			JOptionPane.showMessageDialog(new JFrame("Unable to Reach Server"), 
					"Unable to reach port 8900 with SOLR core \"solrservices\"\n"
					+ "at this time. Please follow the directions outlined\n in"
					+ "the README.txt file in the root directory.");
			frame.dispose();
		}
	}
	
	//setup for the GUI frame
	public void setup() {
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(800, 800);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		//frame.setResizable(false);
		frame.setVisible(true);
	}
	
	public void addButtons() {		
		displayPan.setLayout(new BoxLayout(displayPan, BoxLayout.Y_AXIS));
		//displayPan.setPreferredSize(new Dimension(600,200));
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(400, 600));
		scroll.setMinimumSize(new Dimension(400, 600));
		info.setEnabled(false);
		info.setSelectionColor(Color.black);
		info.setText("\tStart by adding Documents!");
		info.append("\n       Use the example documents or add your own!\n");
	    
	    //add components to the frame
	    north.add(examples);
	    north.add(addDoc);
		north.add(deleteDoc);
		north.add(refresh);
		north.add(searchbar);
		north.add(search);
		west.add(infoscroll);
		east.add(scroll);
		frame.add(west, BorderLayout.WEST);
		frame.add(north, BorderLayout.NORTH);
		frame.add(east, BorderLayout.EAST);
		
		//fit frame to component size and reveal
		frame.setVisible(true);
	}
	
	public void initSearch() {
		//adds listener to the "Search" button
				search.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e)  {
						Query query = new Query();
						ArrayList<ProductBean> results = null;
						
						try {
							results = query.acceptQuery(searchbar.getText());
						} catch (SolrServerException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if(results==null) {
							//display.setText("\tNo results");
							info.append("\n...Search successful");
						}
						else {
							displayResults(results);
							
							
							info.append("\n...Search successful");
						}
						updateDisplay();
					}
				});
	}
	
	public void management() {
		//when add doc is clicked:
		addDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int status = 0;
				final JFileChooser fc = new JFileChooser();
				FileFilter filter = 
						new FileNameExtensionFilter("SOLR compatible","xml", "csv", "json");
				
				fc.setFileFilter(filter);
				
				fc.showOpenDialog(fc);
				File f = fc.getSelectedFile();
				Index adder;
				if(f==null) {
					
				}
				else {
					try {
						adder = new Index();
						status = adder.acceptDocument(f);
					} catch (SolrServerException | IOException e1) {
						// TODO Auto-generated catch block
					}
					
					if(status <= 0) {
						info.append("\nDocument failed to upload...Index unchanged");
					}
					else if(status == 2) {
						info.append("\nFile type not yet supported by application...Index unchanged");
					}
					else {
						info.append("\n...Index successful");
					}
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
					info.append("\nInvaid ID...Removal Failed");
				}
				else if(status == 1) {
					info.append("\n...Removal Succesful");
				}
				else {
					info.append("\n...No removal occured");
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
		
		//when Index Examples is clicked:
				examples.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Index ex = new Index();
							ex.exampleDocs();
							info.append("\nExample documents added...Index Successful");
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
	}
	
	public void displayResults(ArrayList<ProductBean> results) {
		displayPan.removeAll();
		for(ProductBean bean: results) {
			JButton b = new JButton("ID: "+bean.getId());
			String format= "";
			for(int i=0; i<bean.numFields(); i++) {
				format = format.concat(bean.getField(i)+": "+bean.getValue(i)+"\n");
			}
			//System.out.println(format);
			JPanel holder = new JPanel();
			holder.setBorder(BorderFactory.createLineBorder(Color.black));
			JTextArea p = new JTextArea();
			p.setText(format);
			holder.add(b);
			holder.add(p);
			displayPan.add(holder);
		}
	}
}
