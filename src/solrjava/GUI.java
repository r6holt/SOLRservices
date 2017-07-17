package solrjava;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.solr.client.solrj.SolrServerException;

public class GUI {
	private static int START = 0;
	private  static int ROWS = 10;
	
	// Documents
	private ArrayList<ProductBean> results;
	FieldTracker ft = new FieldTracker();
	private boolean newDoc = false;

	// frame
	private JFrame frame = new JFrame("SOLR Search Services");
	private JPanel west = new JPanel();
	private JPanel east = new JPanel();

	// search
	private JPanel north = new JPanel(new FlowLayout());
	private JTextField searchbar = new JTextField(15);
	private JButton search = new JButton("Search");
	private JButton addDoc = new JButton("Upload File");
	private JButton deleteDoc = new JButton("Remove By ID");
	private JButton refresh = new JButton("Empty");
	private JButton examples = new JButton("Examples");
	private JComboBox<String> querycat = new JComboBox<String>();

	// display area
	private JPanel displayPan = new JPanel();
	private JScrollPane scroll = new JScrollPane(displayPan);
	private JPanel pages = new JPanel(new FlowLayout());
	private JButton nextPage = new JButton("Next");
	private JButton prevPage = new JButton("Prev");
	private JComboBox<Integer> numRows = new JComboBox<Integer>();
	private JLabel displayFound = new JLabel("Found ____ documents...       ");

	// options
	//private JPanel options = new JPanel(new FlowLayout());
	private JPanel sort = new JPanel();
	private JPanel price = new JPanel(new FlowLayout());
	private JTextField minPrice = new JTextField(5);
	private JTextField maxPrice = new JTextField(5);
	private JComboBox<String> fieldoptions = new JComboBox<String>();
	private JPanel fieldsort = new JPanel(new FlowLayout()); 
	private JComboBox<String> ascdesc = new JComboBox<String>();
	

	// info
	private JTextArea info = new JTextArea(12, 20);
	private JScrollPane infoscroll = new JScrollPane(info);

	// constructor for GUI
	public GUI() throws SolrServerException, IOException {
		int status;
		
		//init GUI functions
		setup();
		setImage();
		management();
		options();
		sorting();
		addComponents();
		initSearch();
		
		//Enter now works with search button
		frame.getRootPane().setDefaultButton(search);
		
		//Checks to see if SOLR server is running
		Server s = new Server();
		status = s.tryPort();
		if (status == -1) {
			JOptionPane.showMessageDialog(new JFrame("Unable to Reach Server"),
					"Unable to reach port 8900 with SOLR core \"solrservices\"\n"
							+ "at this time. Please follow the directions outlined\n in"
							+ "the README.txt file in the root directory.");
			frame.dispose();
		}
	}

	// setup for the GUI frame
	public void setup() {
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(810, 750);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	public void setImage() {
		BufferedImage logo = null;
		try {
			logo = ImageIO.read(new File("solr.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JLabel img = new JLabel(new ImageIcon(logo));
		//img.setBorder(BorderFactory.createLineBorder(Color.gray));
		img.setPreferredSize(new Dimension(300, 300));
		img.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		west.add(img);
	}

	public void addComponents() {
		Font font1 = new Font("SansSerif", Font.PLAIN, 22);
		Font font2 = new Font("SansSerif", Font.ITALIC, 18);
		// setup components
		displayPan.setLayout(new BoxLayout(displayPan, BoxLayout.Y_AXIS));
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(503, 600));
		scroll.setMinimumSize(new Dimension(503, 600));
		info.setEnabled(false);
		info.setBackground(Color.lightGray);
		info.setText("\tStart by adding Documents!");
		info.append("\n       Use the example documents or add your own!\n");
		searchbar.setFont(font1);
		querycat.setFont(font2);
		search.setFont(new Font("SansSerif", Font.BOLD, 17));
		sort.setLayout(new BoxLayout(sort, BoxLayout.Y_AXIS));
		west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
		east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
		examples.setPreferredSize(new Dimension(80,20));
		addDoc.setPreferredSize(new Dimension(80,20));
		refresh.setPreferredSize(new Dimension(80,20));
		sort.setBorder(BorderFactory.createLineBorder(Color.black));

		// add components to the frame
		pages.add(displayFound);
		pages.add(prevPage);
		pages.add(numRows);
		pages.add(nextPage);
		north.add(examples);
		north.add(addDoc);
		//north.add(deleteDoc);
		north.add(refresh);
		north.add(new JLabel("      "));
		north.add(querycat);
		north.add(searchbar);
		north.add(search);
		west.add(new JLabel("                      ------------------Sort------------------             "));
		west.add(sort);
		east.add(new JLabel("--------Search Results---------------"));
		east.add(scroll);
		east.add(pages);
		frame.add(west, BorderLayout.WEST);
		frame.add(north, BorderLayout.NORTH);
		frame.add(east, BorderLayout.EAST);

		// fit frame to component size and reveal
		frame.setVisible(true);
	}

	public void initSearch() {
		// adds listener to the "Search" button
		search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configSettings();
				START=0;
				
				//Search for query specified
				Query query = new Query(ft);
				try {
					results = query.acceptQuery(searchbar.getText(), START, ROWS);
				} 
				catch (SolrServerException | IOException e1) {
					e1.printStackTrace();
				}
				
				//set info bar
				if (results == null) {
					info.append("\n...Search successful");
				} else {
					displayResults();

					info.append("\n...Search successful");
				}
				//update search results
				updateDisplay();
			}
		});
	}

	public void management() {
		// when add doc is clicked:
		addDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int status = 0;
				
				//displays file explorer for user
				final JFileChooser fc = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("SOLR compatible", "xml", "csv", "json");
				fc.setFileFilter(filter);
				fc.showOpenDialog(fc);
				File f = fc.getSelectedFile();
				
				//index selected file if it is correct file type
				Index adder;
				if (f == null) {}
				else {
					try {
						adder = new Index();
						status = adder.acceptDocument(f);
						newDoc=true;
					} catch (SolrServerException | IOException e1) {
						e1.printStackTrace();
					}
					
					//set info bar
					if (status <= 0) {
						info.append("\nDocument failed to upload...Index unchanged");
					} else if (status == 2) {
						info.append("\nFile type not yet supported by application...Index unchanged");
					} else {
						info.append("\n...Index successful");
					}
				}

			}
		});

		// when delete doc is clicked:
		deleteDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int status = 0;
				
				//creates remover object to remove by ID
				Remove remover = new Remove();
				try {
					status = remover.acceptRemove();
				} catch (SolrServerException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				//set info bar
				if (status == -1) {
					info.append("\nInvaid ID...Removal Failed");
				} else if (status == 1) {
					info.append("\n...Removal Succesful");
				} else {
					info.append("\n...No removal occured");
				}
			}
		});

		// when refresh is clicked:
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//creates refresh object to delete all docs and clear beans
				try {
					new Refresh();
					if(results!=null) {
						results.clear();
					}
					ft.update();
					
				} catch (SolrServerException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// when Index Examples is clicked:
		examples.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//adds auto-generated docs to index
					Index ex = new Index();
					ex.exampleDocs();
					
					//set info bar
					info.append("\nExample documents added...Index Successful");
				
				} catch (SolrServerException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	//initializes options bar under the search results
	public void options() {
		//creates drop down menu for number of results per page
		int a, b, c;
		a = 10;
		b = 25;
		c = 50;
		numRows.addItem(a);
		numRows.addItem(b);
		numRows.addItem(c);
		
		//goes to next page of results
		nextPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//only continues if there are more results to show
				if(Query.FOUND>(START+ROWS)) {
					START+=ROWS;
					
					Query query = new Query(ft);
					configSettings();

					try {
						results = query.acceptQuery(searchbar.getText(), START, ROWS);
					} catch (SolrServerException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					displayResults();
					updateDisplay();
				}
			}
		});
		
		//goes to previous page of results
		prevPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//only continues if there are previous results to show
				if((START-ROWS)>=0) {
					START-=ROWS;
					
					Query query = new Query(ft);
					configSettings();

					try {
						results = query.acceptQuery(searchbar.getText(), START, ROWS);
					} catch (SolrServerException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					displayResults();
					updateDisplay();
				}
				else {
					START=0;
					
					Query query = new Query(ft);
					configSettings();

					try {
						results = query.acceptQuery(searchbar.getText(), START, ROWS);
					} catch (SolrServerException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					displayResults();
					updateDisplay();
				}
			}
		});
		
		querycat.setMaximumSize(new Dimension(60, 30));
		querycat.setForeground(Color.black);
		querycat.setBackground(Color.white);
		querycat.addItem("All:");
		for(int i=0; i<ft.numFields(); i++) {
			querycat.addItem(ft.getField(i));
		}
	}
	
	//set up for sort panel on left hand side
	public void sorting() {
		//updates fields
		try {
			ft.update();
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//refreshes the sort panel
		sort.removeAll();

		Font f1 = new Font("Serif", Font.ITALIC, 16);
		//refresh and setup field sorting
		fieldsort.removeAll();
		fieldoptions.removeAllItems();
		ascdesc.removeAllItems();
		fieldoptions.setBackground(Color.white);
		fieldoptions.addItem("id");
		for(int i=0; i<ft.numFields()/2; i++) {
			if(i!=6) {
				fieldoptions.addItem(ft.getField(i));
			}
		}
		//fieldoptions.setAlignmentX(Component.LEFT_ALIGNMENT);
		ascdesc.addItem("asc");
		ascdesc.addItem("desc");
		fieldsort.add(new JLabel("Sort by: "));
		fieldsort.add(fieldoptions);
		fieldsort.add(ascdesc);
		fieldsort.setAlignmentX(Component.LEFT_ALIGNMENT);
		fieldsort.setPreferredSize(new Dimension(100,100));
		JLabel lsort = new JLabel("   FIELD____");
		lsort.setFont(f1);
		sort.add(lsort);
		sort.add(fieldsort);
		//fieldsort.setBorder(BorderFactory.createLineBorder(Color.gray));
				
		//set up for price querying
		if(ft.getPrice()==true) {
			price.removeAll();
			price.add(new JLabel("Min:"));
			price.add(minPrice);
			price.add(new JLabel("Max:"));
			price.add(maxPrice);
			JLabel lprice = new JLabel("   PRICE____");
			lprice.setFont(f1);
			sort.add(lprice);
			sort.add(price);
			//price.setBorder(BorderFactory.createLineBorder(Color.gray));
		}
		price.setAlignmentX(Component.LEFT_ALIGNMENT);
		
	}

	// updates frame for new content
	public void updateDisplay() {
		frame.setVisible(true);
	}

	// configures query parameters
	public void configSettings() {
		ROWS = Integer.parseInt(numRows.getSelectedItem().toString());
		
		//see if user attempted a price filter correctly
		try {
			ft.setMaxPrice(Integer.parseInt(maxPrice.getText()));
			ft.setMinPrice(Integer.parseInt(minPrice.getText()));
			ft.setPriceQuery(true);
		}
		catch (Exception ex) {
			//incorrect price filter
			maxPrice.setText("");
			minPrice.setText("");
			ft.setPriceQuery(false);
		}
		
		ft.setSortfield(fieldoptions.getSelectedItem().toString());
		ft.setSort(ascdesc.getSelectedItem().toString());
		
		if(querycat.getSelectedIndex()!=0) {
			ft.setCategory(querycat.getSelectedItem().toString());
		}
		else {
			ft.setCategory("null");
		}
	}

	//displays results on the results panel
	public void displayResults() {
		//refresh results
		displayPan.removeAll();
		
		//adds a button and text field for each result found
		for (ProductBean bean: results) {
			JButton b = new JButton("ID: " + bean.getId());
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String f = "";
					JFrame message = new JFrame("Document ID: " + bean.getId());
					message.setSize(400, 300);
					message.setLayout(new GridLayout(bean.numFields() + 2, 1));
					message.add(new JLabel("______Document Information_____\n"));

					for (int i = 0; i < bean.numFields(); i++) {
						f = bean.getField(i) + ": " + bean.getValue(i) + "\n";
						message.add(new JLabel(f));
					}

					//TODO DOWNLOADING CAPABILITIES
					JButton dwld = new JButton("Download Now");
					message.add(dwld);
					message.setLocationRelativeTo(null);
					message.setVisible(true);
					
					dwld.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							new Download(bean.getFields(), bean.getValues(), bean.getId());
						}
					});
				}
			});

			//formats textfield text
			String format = "";
			for (int i = 0; i < bean.numFields(); i++) {
				format = format.concat(bean.getField(i) + ": " + bean.getValue(i) + "\n");
			}

			//adds button and textfield to the results panel
			JPanel holder = new JPanel();
			holder.setBorder(BorderFactory.createLineBorder(Color.black));
			JTextArea p = new JTextArea();
			p.setText(format);
			holder.add(b);
			holder.add(p);
			displayPan.add(holder);
			
			//refreshes documents found and what page user is on
			displayFound.setText("Found "+Query.FOUND+" documents...("+
			START+" - "+(START+results.size())+")       ");
		}
		
		//refreshes sort panel
		if(newDoc) {
			sorting();
		}
		newDoc=false;
		
	}
}
