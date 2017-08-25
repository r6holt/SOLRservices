package solrjava;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.awt.event.ActionEvent;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;

import net.miginfocom.swing.MigLayout;

public class GUI {
	public static final String urlString = "http://40.85.156.209:8900/solr/solrservices";
	private static int START = 0;
	private  static int ROWS = 10;
	
	// Object Storage
	private ArrayList<ProductBean> results;
	private JPanel imgholder = new JPanel(new FlowLayout());
	private String pricerange = "";
	private String selectedCats = "";
	private long lastOccurance = 0;
	private long lastSearch = 0;
	private FieldTracker ft;
	private SchemaEditor schemaEditor = new SchemaEditor();
	private Remove remove = new Remove();
	private Refresh removeAll = new Refresh();
	private Query query = new Query(ft);
	private Index index = new Index(schemaEditor);
	private Download download = new Download();
	private boolean newDoc = true;
	private boolean reset = true;

	// frame
	private JFrame frame = new JFrame("SOLR Search Services");
	private JPanel middle = new JPanel(new FlowLayout());
	private JPanel west = new JPanel();
	private JPanel east = new JPanel();

	// search
	private JPanel north = new JPanel(new MigLayout());
	private JTextField searchbar = new JTextField(45);
	private JButton search = new JButton("      SEARCH      ");
	private JButton addDoc = new JButton();//"Upload File");
	private JButton refresh = new JButton();//"Empty");
	private JButton examples = new JButton();//"Examples");
	private JButton newfile = new JButton();
	private JComboBox<String> querycat = new JComboBox<String>();
	private JPanel suggestions = new JPanel(new MigLayout());
	
	// schema management
	private JPanel menu = new JPanel(new MigLayout());
	private JTextField nfield = new JTextField(12);
	private JButton addition = new JButton("Add Field");
	private JTextField editer = new JTextField(15);
	private JButton ex = new JButton("X");

	// display area
	private JPanel displayPan = new JPanel();
	private JScrollPane scroll = new JScrollPane(displayPan);
	private JPanel pages = new JPanel(new FlowLayout());
	private JButton nextPage = new JButton("Next");
	private JButton prevPage = new JButton("Prev");
	private JComboBox<Integer> numRows = new JComboBox<Integer>();
	private JLabel displayFound = new JLabel("Found ____ document(s)...       ");
	private JProgressBar progress = new JProgressBar();

	// sort
	private JPanel sort = new JPanel();
	private JPanel fieldsort = new JPanel(new FlowLayout()); 
	private JComboBox<String> ascdesc = new JComboBox<String>();
	private JPanel locate = new JPanel(new MigLayout());
	private JComboBox<String> locatefields = new JComboBox<String>();
	private JSlider lat = new JSlider();
	private JSlider lon = new JSlider();
	
	// refine
	private JPanel refine = new JPanel(new MigLayout());
	private JPanel price = new JPanel(new FlowLayout());
	private JTextField minPrice = new JTextField(5);
	private JTextField maxPrice = new JTextField(5);
	private JComboBox<String> fieldoptions = new JComboBox<String>();
	private ArrayList<JPanel> facetfields = new ArrayList<JPanel>();
	private JPanel facetfield = new JPanel();
	private JPanel facetprice = new JPanel(new MigLayout());
	private JButton clearfacet = new JButton("CLEAR");
	private JScrollPane refinescroll = new JScrollPane(refine);
	
	// price range box checks
	private boolean _0 = false;
	private boolean _10 = false;
	private boolean _25 = false;
	private boolean _50 = false;
	private boolean _100 = false;
	private boolean _200 = false;
	private boolean _500 = false;
	
	// fonts
	private Font headers = new Font("Serif", Font.ITALIC, 22);
	private Font labels = new Font("Serif", Font.ITALIC, 18);
	private Font labels2 = new Font("Serif", Font.PLAIN, 18);
	

	// constructor for GUI
	public GUI() throws SolrServerException, IOException {
		System.setProperty("java.awt.headless", "true");
		
		int status;
		setup();
		
		//Checks to see if SOLR server is running
		Server s = new Server();
		status = s.tryPort();
		if (status == -1) {
			JOptionPane.showMessageDialog(new JFrame("Unable to Reach Server"),
					"Unable to reach port 8900 with SOLR core \"solrservices\"\n"
							+ "at this time. Please follow the directions outlined\n in"
							+ " the README.txt file in the root directory.");
			frame.dispose();
		}
		
		ft = new FieldTracker();
		
		//init GUI functions
		setIcons();
		startup();
		management();
		menubar();
		options();
		updateFields();
		sorting();
		startup2();
		addComponents();
		initSearch();
		
		//Enter now works with search button
		search.doClick();
		
		frame.getRootPane().setDefaultButton(search);
		
		schemaEditor.clearFields(ft.getFields());
	}

	// setup for the GUI frame
	public void setup() {
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setPreferredSize(new Dimension(2000, 1310));
		frame.setMinimumSize(new Dimension(2000, 1310));
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	//creates SOLR logo and sets icon
	public void setIcons() {
		BufferedImage logo = null;
		BufferedImage image = null;
		try {
			logo = ImageIO.read(new File("images"+File.separator+"solr.jpg"));
			image = ImageIO.read(new File("images"+File.separator+"solr.png"));
			addDoc.setIcon(new ImageIcon(ImageIO.read(new File("images"+File.separator+"upload.png"))));
			refresh.setIcon(new ImageIcon(ImageIO.read(new File("images"+File.separator+"empty.png"))));
			examples.setIcon(new ImageIcon(ImageIO.read(new File("images"+File.separator+"examples.png"))));
			newfile.setIcon(new ImageIcon(ImageIO.read(new File("images"+File.separator+"new.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JLabel img = new JLabel(new ImageIcon(image));
		img.setPreferredSize(new Dimension(320, 200));
		
		img.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				searchbar.setText("");
				ft.setCategory(null);
				clearfacet.doClick();
			}
			
		});
		
		imgholder.add(img);
		frame.setIconImage(logo);
	}
	
	//startup image with progress bar
	public void startup() {
		middle.add(imgholder);
		middle.add(progress);
		frame.add(middle);
		frame.setVisible(true);
	}
	
	//runs progress bar while app boots up
	public void startup2() {
		try {
			for(int i=0; i<15; i++) {
				progress.setValue(i);
				Thread.sleep(20);
			}
			Thread.sleep(200);
			for(int i=15; i<100; i++) {
				progress.setValue(i);
				Thread.sleep(5);
			}
		}catch (InterruptedException e) {}
		
		// add content to Jframe
		frame.remove(middle);
		frame.add(west, BorderLayout.WEST);
		frame.add(north, BorderLayout.NORTH);
		frame.add(east, BorderLayout.EAST);
		frame.setVisible(true);
	}

	public void addComponents() {
		JLabel header1 = new JLabel("----------------------Sort----------------------");
		JLabel header2 = new JLabel("---------------------Refine---------------------");
		Font font1 = new Font("SansSerif", Font.PLAIN, 26);
		Font font2 = new Font("SansSerif", Font.ITALIC, 22);
		
		// setup component styling
		displayFound.setFont(new Font("SansSerif", Font.PLAIN, 18));
		displayPan.setLayout(new MigLayout());
		searchbar.setFont(font1);
		search.setFont(new Font("Serif", Font.BOLD, 24));
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(1610, 1055));
		scroll.setMinimumSize(new Dimension(1610, 1055));
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(32, 0));
		scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 20));
		refinescroll.setPreferredSize(new Dimension(355, 710));
		refinescroll.setMinimumSize(new Dimension(355, 710));
		refinescroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		refinescroll.setBorder(null);
		north.setPreferredSize(new Dimension(2000, 90));
		sort.setLayout(new MigLayout());
		west.setLayout(new MigLayout());
		east.setLayout(new MigLayout());
		west.setPreferredSize(new Dimension(370, 1250));
		sort.setPreferredSize(new Dimension(260, 250));
		progress.setPreferredSize(new Dimension(250, 20));
		progress.setForeground(Color.green);
		progress.setBackground(Color.white);
		//menu.setBorder(BorderFactory.createLineBorder(Color.black));
		menu.setPreferredSize(new Dimension(1610, 50));
		querycat.setMaximumSize(new Dimension(160, 45));
		querycat.setPreferredSize(new Dimension(160, 45));
		querycat.setForeground(Color.black);
		querycat.setBackground(Color.white);
		querycat.setFont(font2);
		header1.setFont(headers);
		header2.setFont(headers);

		// add components to panels
		pages.add(new JLabel("                                                                                                                                                       "), "cell 0 0");
		pages.add(displayFound); 
		pages.add(prevPage);
		pages.add(numRows);
		pages.add(nextPage);
		pages.add(new JLabel("                                                            "));
		pages.add(progress);
		north.add(new JLabel("        "), "cell 0 0 1 2");
		north.add(examples, "cell 1 0 1 2");
		north.add(addDoc, "cell 2 0 1 2");
		north.add(refresh, "cell 3 0 1 2");
		north.add(newfile, "cell 4 0 1 2");
		north.add(new JLabel("                                              "), "cell 5 0 1 2");
		north.add(querycat);
		north.add(searchbar);
		north.add(search); 
		north.add(new JLabel("                                "), "span");
		north.add(suggestions, "cell 7 1");
		west.add(imgholder, "span");
		west.add(header1, "span");
		west.add(sort, "span");
		west.add(header2, "span");
		west.add(refinescroll, "span");
		east.add(menu, "span");
		east.add(scroll, "span");
		east.add(pages, "span");

		// reveal window
		frame.setVisible(true);
	}

	// initializes search button
	public void initSearch() {
		search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(System.currentTimeMillis()-lastSearch>=500) {
					configSettings();
					if(reset) {
						START=0;
					}
					
					//Search for query specified
					query.updateFT(ft);
					try {
						results = query.acceptQuery(searchbar.getText(), START, ROWS);
						ft.update();
					} 
					catch (SolrServerException | IOException e1) {
						e1.printStackTrace();
					}
					//update search results
					frame.getRootPane().setDefaultButton(search);
					editer.setEnabled(false);
					editer.setText("");
					ex.setEnabled(false);
					displayResults();
					updateDisplay();
					reset=true;
					lastSearch = System.currentTimeMillis();
				}
				// disables double clicking
			}
		});
	}

	public void management() {
		
		// when add doc is clicked
		addDoc.setFont(labels2);
		addDoc.setToolTipText("Upload Documents");
		addDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startProgress();
				
				//displays file explorer for user
				final JFileChooser fc = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("SOLR compatible", "xml", "csv", "json", "pdf");
				fc.setFileFilter(filter);
				fc.setMultiSelectionEnabled(true);
				fc.showOpenDialog(fc);
				File[] f = fc.getSelectedFiles();
				
				if (f.length != 0) {
					try {
						index.acceptDocument(f);
					} catch (SolrServerException | IOException e1) {
						e1.printStackTrace();
					}
				}
				newDoc=true;
				finishProgress(1);
				search.doClick();

			}
		});

		// when refresh is clicked
		refresh.setFont(labels2);
		refresh.setToolTipText("Delete All Documents");
		//refresh.setEnabled(false);
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int answer = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you want to \ndelete all documents?");
				
				if(answer==0) {
					ft.setFacetchoice(null);
					startProgress();
					//creates refresh object to delete all docs and clear beans
					try {
						removeAll.deleteAll();
						if(results!=null) {
							results.clear();
						}
						ft.update();
						
					} catch (SolrServerException | IOException e1) {
						e1.printStackTrace();
					}
					newDoc=true;
					finishProgress(1);
					search.doClick();
				}
			}
		});

		// when Examples is clicked
		examples.setFont(labels2);
		examples.setToolTipText("Index Example Documents");
		examples.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int answer = JOptionPane.showConfirmDialog(new JFrame(), "Do you want to add the example documents?");
				
				if(answer == 0) {
					ft.setFacetchoice(null);
					startProgress();
					
					try {
	
						index.exampleDocs();
					
					} catch (SolrServerException | IOException e1) {
						e1.printStackTrace();
					}
					newDoc=true;
					finishProgress(1);
					search.doClick();
				}
			}
		});
		
		// when newFile is clicked
		newfile.setFont(labels2);
		newfile.setToolTipText("Create New Document");
		newfile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<JTextField> fls = new ArrayList<JTextField>();
				ArrayList<JTextField> vls = new ArrayList<JTextField>();
				
				// initializes JFrame
				JFrame creator = new JFrame("Create New File");
				JPanel fields = new JPanel(new MigLayout());
				JScrollPane fieldscroll = new JScrollPane(fields);
				JButton more = new JButton("More Fields");
				JButton create = new JButton("Create");
				JLabel header = new JLabel("Add fields to create a new document!");
				JLabel header1 = new JLabel("   Field");
				JLabel header2 = new JLabel("               Value");
				Font f = new Font("Serif", Font.PLAIN, 18);
				
				frame.setEnabled(false);
				creator.setSize(450, 550);
				creator.setResizable(false);
				creator.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				creator.setAlwaysOnTop(true);
				creator.getRootPane().setDefaultButton(create);
				creator.setLayout(new MigLayout());
				creator.setLocation(frame.getX()+700, frame.getY()+350);
				fieldscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				fieldscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				fieldscroll.setPreferredSize(new Dimension(450, 450));
				fieldscroll.setMaximumSize(new Dimension(450, 450));
				
				header1.setFont(f);
				header2.setFont(f);
				fields.add(header1);
				fields.add(header2, "span");
				
				// Adds space for inserting field information
				for(int i=0; i<3; i++) {
					JTextField f1 = new JTextField(10);
					JTextField v1 = new JTextField(17);
					
					if(i==0) {
						f1.setText("id");
						f1.setEditable(false);
					}
					
					fls.add(f1);
					vls.add(v1);
					f1.setFont(f);
					v1.setFont(f);
					
					fields.add(f1);
					fields.add(v1,  "wrap");
				}
				
				more.setFont(f);
				fields.add(more, "span");
				
				// Adds more space for field information
				more.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						fields.remove(more);
						
						JTextField f1 = new JTextField(10);
						JTextField v1 = new JTextField(17);
						
						fls.add(f1);
						vls.add(v1);
						f1.setFont(f);
						v1.setFont(f);
						
						fields.add(f1);
						fields.add(v1,  "wrap");
						fields.add(more);
						creator.setVisible(true);
					}
				});
				
				// Builds the new document and indexes it into the SOLR core
				create.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(vls.get(0).getText().equals("")) {
							creator.setAlwaysOnTop(false);
							JOptionPane.showMessageDialog(new JFrame(), "Must include ID!");
							creator.setAlwaysOnTop(true);
						}
						else {
							frame.setEnabled(true);
							creator.dispose();
							
							for(int i=0; i<fls.size(); i++) {
								if(fls.get(i).getText().equals("")) {fls.remove(i); vls.remove(i);}
								else if(vls.get(i).getText().equals("")) {fls.remove(i); vls.remove(i);}
							}
							
							ProductBean b = new ProductBean(vls.get(0).getText());
							for(int i=1; i<fls.size(); i++) {
								b.addField(fls.get(i).getText(), vls.get(i).getText());
							}
							
							index.newFile(b);
							newDoc=true;
							search.doClick();
						}
					}
				});
				
				header.setFont(f);
				create.setFont(f);
				creator.add(header, "span");
				creator.add(fieldscroll, "span");
				creator.add(new JLabel());
				creator.add(create, "span");
				
				creator.setVisible(true);
				
				// Returns focus to original JFrame
				creator.addWindowListener(new WindowListener() {
					@Override public void windowActivated(WindowEvent arg0) {}
					@Override
					public void windowClosed(WindowEvent arg0) {
						frame.setEnabled(true);
					}@Override public void windowClosing(WindowEvent arg0) {} @Override public void windowDeactivated(WindowEvent arg0) {} @Override public void windowDeiconified(WindowEvent arg0) {}
					@Override public void windowIconified(WindowEvent arg0) {} @Override public void windowOpened(WindowEvent arg0) {}
					
				});
			}
		});
	}
	
	
	// adds search suggestions if any are returned
	public void suggest() {
		List<String> spellcheck = query.getSpellcheck();
		suggestions.removeAll();
		
		if(spellcheck!=null) {
			ArrayList<JButton> options = new ArrayList<JButton>();
			JLabel dym = new JLabel("Did you mean? ");
			dym.setFont(new Font("SansSerif", Font.ITALIC, 16));
			
			// initializes suggestions
			for(int i=0; i<spellcheck.size(); i++) {
				JButton sug = new JButton(spellcheck.get(i));
				sug.setForeground(Color.blue);
				sug.setFont(new Font("SansSerif", Font.ITALIC, 16));
				sug.setBorder(null);
				sug.setContentAreaFilled(false);
				
				sug.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						searchbar.setText(sug.getText());
						search.doClick();
					}
				});
				options.add(sug);
			}
			
			suggestions.add(dym);
			for(JButton sug:options) {
				suggestions.add(sug);
				suggestions.add(new JLabel("  "));
			}
			
			if(options.isEmpty()) {
				suggestions.setVisible(false);
			}
		}
	}
	
	//initializes schema maintenance panel
	public void menubar() {	
		menu.removeAll();
		menu.setVisible(false);
		menu.setVisible(true);
		
		//menu-specific components
		String[] options = {"string", "float", "date", "location", "boolean"};
		JComboBox<String> dataType = new JComboBox<String>();
		JComboBox<String> deleteoptions = new JComboBox<String>();
		ArrayList<String> delop = new ArrayList<String>();
		JCheckBox cb = new JCheckBox();
		JTextField fill = new JTextField(10);
		JButton removeField = new JButton("Delete Field");
		JLabel ask2 = new JLabel("         Value:");
		JLabel filler = new JLabel("                           ");
		JLabel filler2 = new JLabel("                          ");
		JLabel ask = new JLabel("add field to search results");
		
		//component styling
		dataType.setFont(labels);
		ask.setFont(labels);
		ask2.setFont(labels);
		fill.setFont(labels);
		nfield.setFont(labels);
		addition.setFont(labels);
		editer.setFont(labels);
		removeField.setFont(labels);
		deleteoptions.setFont(labels);
		deleteoptions.setPreferredSize(new Dimension(160, 34));
		deleteoptions.setMaximumSize(new Dimension(160, 34));
		fill.setVisible(false);
		ask2.setVisible(false);
		filler.setVisible(false);
		editer.setEnabled(false);
		ex.setEnabled(false);
		ex.setFont(new Font("Serif", Font.BOLD, 12));
		ex.setBackground(Color.WHITE);
		
		//adds options to combo boxes in menu
		deleteoptions.addItem("Select Field");
		for(String s:options) dataType.addItem(s);
		for(int i=0; i<(ft.numFields()); i++) if(!ft.getField(i).equals("id") && !ft.getField(i).equals("_version_") && !ft.getField(i).equals("_text_")) delop.add(ft.getField(i));
		for(String s:delop) if(s.length()>20) deleteoptions.addItem(s.substring(0, 20)); else deleteoptions.addItem(s);
		
		//adds components to schema menu
		menu.add(nfield);
		menu.add(dataType);
		menu.add(addition);
		menu.add(cb);
		menu.add(ask);
		menu.add(ask2);
		menu.add(fill);
		menu.add(filler);
		menu.add(editer);
		menu.add(ex);
		menu.add(filler2);
		menu.add(deleteoptions);
		menu.add(removeField);
		
		//adds the user inputed field to schema.xml
		addition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!nfield.getText().trim().equals("")) {
					try {
						schemaEditor.addField(nfield.getText(), dataType.getSelectedItem().toString());
						ft.update();
						
						//returns all results from last search
						results = query.acceptQuery(searchbar.getText(), 0, (int)query.getFOUND());
						
					} catch (SolrServerException | IOException e1) { JOptionPane.showMessageDialog(new JFrame(), "Error adding new field.");}
					
					//adds field and value to docs in the current search
					if(cb.isSelected() && results!=null) {
						if(dataType.getSelectedItem().equals("date")) {
							String[] parts = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().toString().split(" ");
							for(ProductBean b: results) {
								b.addField(nfield.getText(), getDate(parts));
							}
						}
						else {
							for(ProductBean b: results) {
								b.addField(nfield.getText(), fill.getText().trim());
							}
						}
						
						try {
							index.reload(results);
						} catch (SolrServerException | IOException e1) {}
						
						ft.setFacetchoice(null);
					}
					newDoc=true;
					search.doClick();
				}
				else {
					finishProgress(-1);
				}
				
				// reset menu
				fill.setText("");
				nfield.setText("");
				cb.setSelected(false);
			}
		});
		
		//removes user selected field from schema.xml
		removeField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startProgress();
				
				// finds the field to be deleted
				String delete = "Select Field";
				if(deleteoptions.getSelectedIndex()!=0) delete = delop.get(deleteoptions.getSelectedIndex()-1);
				
				//does nothing if a field is not selected
				if(!deleteoptions.getSelectedItem().equals("Select Field")) {
					try {
						// prepares query to return only results with selected field
						ft.update();
						searchbar.setText("");
						clearfacet.doClick();
						ft.setCategory(delete);
						
						results = query.acceptQuery("", 0, (int)query.getFOUND());
						schemaEditor.deleteField(delete);
					} catch (SolrServerException | IOException e1) {JOptionPane.showMessageDialog(new JFrame(), "Error deleting field.");}
					
					// deletes field from all docs containing the field
					if(results!=null) {
						try {
							index.reload(results, delete);
						} catch (SolrServerException | IOException e1) {
							e1.printStackTrace();
						}
						ft.setFacetchoice(null);
						ft.setCategory(null);
						newDoc=true;
						search.doClick();
					}
				}
				else {
					finishProgress(-1);
				}
			}
		});
		
		//allows user to input field value after check box is checked
		cb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(dataType.getSelectedItem().equals("date")) {
					fill.setVisible(!fill.isVisible());
					ask2.setVisible(!ask2.isVisible());
					fill.setEditable(!fill.isEditable());
					
					if(e.getStateChange()==ItemEvent.SELECTED) {
						fill.setText("Today's Date");
					}
					else {
						fill.setText("");
					}
				}
				else {
					fill.setVisible(!fill.isVisible());
					ask2.setVisible(!ask2.isVisible());
				}
				dataType.setEnabled(!dataType.isEnabled());
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
		numRows.setFont(new Font("SansSerif", Font.PLAIN, 18));
		
		//refresh page results
		numRows.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				search.doClick();
			}
		});
		
		//goes to next page of results
		nextPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//only continues if there are more results to show
				if(query.getFOUND()>(START+ROWS)) {
					START+=ROWS;
					reset=false;
					search.doClick();
				}
			}
		});
		nextPage.setFont(new Font("SansSerif", Font.PLAIN, 18));
		
		//goes to previous page of results
		prevPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//only continues if there are previous results to show
				if((START-ROWS)>=0) {
					START-=ROWS;
					reset=false;
					search.doClick();
				}
				else {
					search.doClick();
				}
			}
		});
		prevPage.setFont(new Font("SansSerif", Font.PLAIN, 18));
		
		prevPage.setEnabled(false);
		nextPage.setEnabled(false);
	}
	
	//set up for sort panel on left hand side
	public void sorting() {
		//updates fields
		try {
			ft.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//refreshes the sort panel
		sort.removeAll();
		fieldsort.removeAll();
		fieldoptions.removeAllItems();
		ascdesc.removeAllItems();
		fieldoptions.setBackground(Color.white);
		fieldoptions.setFont(labels);
		fieldoptions.setMaximumRowCount(6);

		fieldoptions.addItem("   ");
		fieldoptions.addItem("id");
		fieldoptions.addItem("location");
		fieldoptions.addItem("_version_");
		for(int i=0; i<ft.getDatatypes().size(); i++) {
			if(ft.getDatatypes().get(i).equals("float") || ft.getDatatypes().get(i).equals("double")) {
				fieldoptions.addItem(ft.getField(i));
			}
		}
		
		fieldoptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<?> box = (JComboBox<?>) e.getSource();
				Object selected = box.getSelectedItem();
				
				if(selected==null) {}
				else if(selected.toString().equals("location")) {
					sortLocation();
				}
				else {
					locate.removeAll();
					updateDisplay();
				}
			}
		});
		
		ascdesc.addItem("asc");
		ascdesc.addItem("desc");
		ascdesc.setFont(labels);
		JLabel soby = new JLabel("Sort by: ");
		soby.setFont(labels);
		fieldsort.add(soby);
		fieldsort.add(fieldoptions);
		fieldsort.add(ascdesc);
		fieldsort.setAlignmentX(Component.LEFT_ALIGNMENT);
		//fieldsort.setPreferredSize(new Dimension(100,100));
		sort.add(fieldsort, "span");
		sort.add(locate, "span");
		
	}
	
	public void sortLocation() {
		locate.removeAll();
		locatefields.removeAllItems();
		
		JLabel l1 = new JLabel("       Select Field: ");
		l1.setFont(labels);
		locate.add(l1);
		
		for(int i=0; i<ft.numFields(); i++) {
			if(ft.getDatatypes().get(i).equals("location")) {
				locatefields.addItem(ft.getField(i));
			}
		}
		locate.add(locatefields, "split 2");
		
		JButton picker = new JButton();
		try {
			picker.setIcon(new ImageIcon(ImageIO.read(new File("images"+File.separator+"target2.png"))));
			
		} catch (IOException e) {}
		
		// initializes map of the United States from which to pick lat-long coordinates
		picker.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame map = new JFrame("Select Coordinates");
				
				//map.setSize(600,  600);
				map.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				map.setAlwaysOnTop(true);
				map.setLocation(frame.getX()+350,  frame.getY()+200);
				map.setResizable(false);
				frame.setEnabled(false);
				map.setCursor(Frame.CROSSHAIR_CURSOR);
				
				JLabel usa = new JLabel();
				try {
					usa.setIcon(new ImageIcon(ImageIO.read(new File("images"+File.separator+"usa.jpg"))));
				} catch (IOException e1) {}
				
				map.add(usa);
				map.setVisible(true);
				map.pack();
				map.addWindowListener(new WindowListener() {
					@Override public void windowActivated(WindowEvent arg0) {}@Override public void windowClosed(WindowEvent arg0) {frame.setEnabled(true);}
					@Override public void windowClosing(WindowEvent arg0) {}@Override public void windowDeactivated(WindowEvent arg0) {}@Override public void windowDeiconified(WindowEvent arg0) {}@Override public void windowIconified(WindowEvent arg0) {}@Override public void windowOpened(WindowEvent arg0) {}
				});
				
				// Algorithm for converting pixels to lat-long coordinates
				usa.addMouseListener(new MouseAdapter()  {  
				    public void mouseClicked(MouseEvent e)  {
				    	double height = map.getHeight();
				    	double width = map.getWidth();
				    	
				    	double lata = (55-(30*((double)e.getY()/height)));
				    	double longa = (-130+(68*((double)e.getX()/width)));
				    	longa-=(longa+95)/5;
				    	lata-=((Math.abs(longa+95)/12));
				    	
				    	lat.setValue((int) lata);
				    	lon.setValue((int) longa);
				    	
				    	//System.out.println(lata+"     "+longa);
				    	
				    	map.dispose();
				    	frame.setEnabled(true);
				    	search.doClick();
				    }  
				}); 
			}
		});
		
		// initializes Spatial Search options
		locate.add(picker, "span");
		locate.add(new JLabel(), "span");
		
		JLabel l = new JLabel("        Latitude:");
		l.setFont(labels);
		locate.add(l);
		
		lat.setFont(labels);
		lat.setMajorTickSpacing(90);
		lat.setPaintTicks(true);
		lat.setMaximum(90);
		lat.setMinimum(-90);
		lat.setPaintLabels(true);
		lat.setPreferredSize(new Dimension(180, 50));
		lat.setMinimumSize(new Dimension(180, 50));
		lat.setValue(0);
		locate.add(lat, "wrap");
		
		JLabel l2 = new JLabel("        Longitude:");
		l2.setFont(labels);
		locate.add(l2);		
		
		lon.setFont(labels);
		lon.setMajorTickSpacing(180);
		lon.setPaintTicks(true);
		lon.setMaximum(180);
		lon.setMinimum(-180);
		lon.setPaintLabels(true);
		locate.add(lon, "wrap");
		
		updateDisplay();
		
	}

	// initializes refine panel for faceting search results
	public void refine() {		
		List<FacetField> facets = query.getFacet();
		JPanel holder = new JPanel(new FlowLayout());
		facetfields.clear();
		refine.removeAll();
		facetfield.removeAll();
		facetprice.removeAll();
		holder.removeAll();
		refine.setVisible(false);
		refine.setVisible(true);
		
		// clears all facets and re-searches
		clearfacet.setFont(new Font("Serif", Font.ITALIC, 16));
		clearfacet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if((System.currentTimeMillis()-lastOccurance) > 500) {
					startProgress();
					minPrice.setText("");
					maxPrice.setText("");
					ft.setPriceQuery(false);
					ft.setFacetchoice(null);
					ft.setPricechoice("");
					facetfields.clear();
					selectedCats = "";
					pricerange = "";
					_0=false;
					_10=false;
					_25=false;
					_50=false;
					_100=false;
					_200=false;
					_500=false;
					
					query.updateFT(ft);
					search.doClick();
				}
				lastOccurance = e.getWhen();
			}
		});
		
		// Gives user fields that can be faceted
		JComboBox<String> pickFacet = new JComboBox<String>();
		pickFacet.addItem("Select Field(s)");
		pickFacet.setFont(labels);
		pickFacet.setPreferredSize(new Dimension(160, 36));
		pickFacet.setMaximumSize(new Dimension(160, 36));
		pickFacet.setBackground(Color.white);
		facetfield.setLayout(new BoxLayout(facetfield, BoxLayout.Y_AXIS));
		facetfield.setAlignmentY(Component.LEFT_ALIGNMENT);		
		
		holder.add(pickFacet);
		holder.add(new JLabel("                "));
		holder.add(clearfacet);
		refine.add(holder, "span");
		
		// INITIALIZES FACETS and HIDES the ones that are currently being used
		// Re-faceting occurs with every search so that pulling up more facet options can be in real-time
		int count = -1;
		if(facets!=null && facets.size()!=0) {
			for(int h=0; h<facets.size(); h++) {
				FacetField facet = facets.get(h);
				
				if(!facet.getValues().isEmpty()) {
					count++;
					
					// creates a new JPanel to store facets for each field
					JPanel jp = new JPanel(new MigLayout());
					jp.setVisible(false);
					facetfields.add(jp);
					if(facet.getValues().get(0).getCount()!=0) pickFacet.addItem(facet.getName());
				
					JLabel label = new JLabel(facet.getName());
					label.setFont(labels);
					facetfields.get(count).add(label, "span");
					ft.setChoices(facet.getName());
					
					// adds faceting options for each field
					for(int i=0; i<facet.getValues().size() && i<5; i++) {
						if(facet.getValues().get(i).getCount()!=0) {
							String name = facet.getValues().get(i).getName();
							JButton button;
							if(facet.getValues().get(i).getName().length()>25) {
								button = new JButton(facet.getValues().get(i).getName().substring(0, 25)+"... ("+facet.getValues().get(i).getCount()+")");
							}
							else {
								button = new JButton(facet.getValues().get(i).getName()+" ("+facet.getValues().get(i).getCount()+")");
							}
							button.setBorderPainted(false);
							button.setFont(new Font("SansSerif", Font.ITALIC, 18));
							button.setForeground(Color.blue);
							button.setOpaque(false);
							button.setContentAreaFilled(false);
							
							try {
								if(ft.getFacetchoice()!=null && ft.getFacetchoice()[count]!=null && ft.getFacetchoice()[count].contains(name)) {
									button.setForeground(Color.BLACK);
									button.setEnabled(false);
								}
							} catch(Exception e) {
								ft.setFacetchoice(null);
							}
							
							//Adds selected faceting option to query
							int index = count;
							button.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									String choice = name;
									choice = choice.split(" \\(")[0];
									if(choice.contains(" ")) {
										choice = "\""+choice+"\"";
									}
									
									if(ft.getFacetchoice()[index]!=null) {
										selectedCats = ft.getFacetchoice()[index];
										selectedCats += choice+"~~~";
									}
									else {
										selectedCats = choice+"~~~";
									}
									ft.setFacetchoice(index, selectedCats);//selectedCats);
									query.updateFT(ft);
									search.doClick();
								}
							});
							
							facetfields.get(count).add(button, "span");
						}
					}
					facetfields.get(count).setName(facet.getName().toString());
					facetfield.add(facetfields.get(count), "span");
				}
			}
			refine.add(facetfield, "span");
		}
		
		//resets the facet choices when field data may have changed
		if(ft.getFacetchoice()==null || newDoc) {
			ft.setFacetchoice(new String[count+1]);
		}
		
		// If a facet option has been selected, permanently display that field info
		for(int i=0; i<ft.getFacetchoice().length; i++) {
			if(ft.getFacetchoice()[i]!=null) {
				facetfields.get(i).setVisible(true);
			}
		}
		
		// Displays facet field options when a field is selected
		pickFacet.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED) {
					String selected = e.getItemSelectable().getSelectedObjects()[0].toString();
					for(int i=0; i<facetfields.size(); i++) {
						if(facetfields.get(i).getName().equals(selected)) {
							facetfields.get(i).setVisible(!facetfields.get(i).isVisible());
						}
					}
					pickFacet.setSelectedItem("Select Field(s)");
				}
			}
		});
		
		// spacing
		refine.add(new JLabel("  "), "span");
		
		//set up for price querying
		if(ft.getPrice()==true) {
			price.removeAll();
			price.add(new JLabel("      $"));
			minPrice.setFont(labels);
			price.add(minPrice);
			price.add(new JLabel("  to     $"));
			maxPrice.setFont(labels);
			price.add(maxPrice);
			JLabel lprice = new JLabel("   PRICE____");
			lprice.setFont(labels);
			refine.add(lprice, "span");
			refine.add(price, "span");
			
			Map<String, Integer> range = query.getRange();
		
			// displays faceting options for PRICE
			if(range!=null) {
				Iterator<Entry<String, Integer>> subset = range.entrySet().iterator();
				for(int i=0; i<range.size(); i++) {
					String format = subset.next().toString().split("\\[")[1];
					String num = format.split("]")[1].substring(1);
					format = format.split("]")[0];
					format = format.replaceAll("TO \\*", "and up");
						JCheckBox box = new JCheckBox();
						JLabel button = new JLabel(format+"   ("+num+")");
						button.setFont(new Font("Serif", Font.ITALIC, 18));
						button.setForeground(Color.black);
						box.setSelected(isSelected(i));
						
						// sends selected facet option to query
						box.addItemListener(new ItemListener() {
							@Override
							public void itemStateChanged(ItemEvent e) {
								if(e.getStateChange()==ItemEvent.SELECTED) {
									String choice = button.getText().split(" ")[0];
									pricerange += choice+" ";
									checkBox(choice);
									
									ft.setPricechoice(pricerange);
									query.updateFT(ft);
									search.doClick();
								}
								else if(e.getStateChange()==ItemEvent.DESELECTED) {
									String choice = button.getText().split(" ")[0];
									pricerange = pricerange.replaceAll(choice+" ", "");
									checkBox(choice);
									
									ft.setPricechoice(pricerange);
									query.updateFT(ft);
									search.doClick();
								}
							}
						});
						
						facetprice.add(box);
						facetprice.add(button, "wrap");
				}
				refine.add(facetprice, "span");
			}
		}
		updateDisplay();
	}
	
	// updates the list of fields used for searching (Default:ALL)
	public void updateFields() {
		Object orig = querycat.getSelectedItem();
		querycat.removeAllItems();
		querycat.addItem("All:");
		for(int i=0; i<ft.numFields(); i++) {
			querycat.addItem(ft.getField(i));
		}
		
		try {
			if(orig!=null) {
				querycat.setSelectedItem(orig);
			}
		}
		catch (Exception e) {}
	}
	
	// updates frame for new content
	public void updateDisplay() {
		frame.setVisible(true);
		displayPan.setVisible(false);
		displayPan.setVisible(true);
	}
	
	// Checks boxes that have already been selected after a refresh
	public void checkBox(String s) {
		if(s.equals("0")) {
			_0 = !_0;
		}
		else if(s.equals("10")) {
			_10 = !_10;
		}
		else if(s.equals("25")) {
			_25 = !_25;
		}
		else if(s.equals("50")) {
			_50 = !_50;
		}
		else if(s.equals("100")) {
			_100 = !_100;
		}
		else if(s.equals("200")) {
			_200 = !_200;
		}
		else if(s.equals("500")) {
			_500 = !_500;
		}
	}
	
	// returns whether or not a box has been selected
	public boolean isSelected(int i) {
		if(i==0) {
			return _0;
		}
		else if(i==1) {
			return _10;
		}
		else if(i==2) {
			return _25;
		}
		else if(i==3) {
			return _50;
		}
		else if(i==4) {
			return _100;
		}
		else if(i==5) {
			return _200;
		}
		else if(i==6) {
			return _500;
		}
		return false;
	}
	
	// returns todays date in proper SOLR date format
	public String getDate(String[] items) {
		String date = "";
		
		date+=items[5]+"-";
		String monthString = "0";
		String month= items[1];
		
		switch (month) {
           case "Jan":  monthString += "1";
                    break;
           case "Feb":  monthString += "2";
                    break;
           case "Mar":  monthString += "3'";
                    break;
           case "Apr":  monthString += "4";
                    break;
           case "May":  monthString += "5";
                    break;
           case "Jun":  monthString += "6";
                    break;
           case "Jul":  monthString += "7";
                    break;
           case "Aug":  monthString += "8";
                    break;
           case "Sep":  monthString += "9";
                    break;
           case "Oct": monthString = "10";
                    break;
           case "Nov": monthString = "11";
                    break;
           case "Dec": monthString = "12";
                    break;
           default: monthString = "Invalid month";
                    break;
		}
		
		date+=monthString+"-";
		date+=items[2]+"T";
		
		String[] time = items[3].split(":");
		date+= ((Integer.parseInt(time[0])))+":";
		date+=time[1]+":"+time[2]+"Z";
		
		return date;
	}
	
	//sets the progress bar to show that an action is being performed
	public void startProgress() {
		SwingUtilities.invokeLater(new Runnable(){
            public void run(){
            	progress.setForeground(Color.yellow);
                progress.setValue(15);
            }
       });
	}
	
	// sets the progress bar to show that an action has been completed
	public void finishProgress(int status) {
		SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                if(status==1) {
                	progress.setForeground(Color.green);
                }
                else {
                	progress.setValue(0);
                	progress.repaint();
                	try {
						Thread.sleep(600);
					} catch (InterruptedException e) {}
                	
                	progress.setForeground(Color.red);
                }
                progress.setValue(100);
            }
       });
	}

	// configures query parameters
	public void configSettings() {
		ROWS = Integer.parseInt(numRows.getSelectedItem().toString());
		
		//see if user attempted a price filter correctly
		try {
			if(!maxPrice.getText().trim().equals("")) {
				ft.setMaxPrice(Integer.parseInt(maxPrice.getText()));
				ft.setPriceQuery(true);
			}
			else {
				ft.setMaxPrice(-1);
			}
		}
		catch (Exception ex) {
			//incorrect price filter
			maxPrice.setText("");
			ft.setPriceQuery(false);
		}
		
		try {
			if(!minPrice.getText().trim().equals("")) {
				ft.setPriceQuery(true);
				ft.setMinPrice(Integer.parseInt(minPrice.getText()));
			}
			else {
				ft.setMinPrice(-1);
			}
		}
		catch( Exception ex) {
			//incorrect price filter
			minPrice.setText("");
		}
		
		// sets field with which to sort
		if(fieldoptions.getSelectedIndex()!=0) {
			ft.setSortfield(fieldoptions.getSelectedItem().toString());
			ft.setSort(ascdesc.getSelectedItem().toString());
		}
		else ft.setSortfield("");
		
		// sets field with which to search
		if(querycat.getSelectedIndex()!=0) {
			ft.setCategory(querycat.getSelectedItem().toString());
		}
		else {
			ft.setCategory("null");
		}
		
		// SETS LOCATION SORT if spatial sorting has been initialized
		try {
			ft.setLat(lat.getValue());
		}
		catch(Exception e) {
			ft.setLat(0);
			lat.setValue(0);;
		}
		
		try {
			ft.setLon(lon.getValue());
		}
		catch(Exception e) {
			ft.setLon(0);
			lon.setValue(0);
		}
		
		try {
			if(locatefields.getSelectedItem()!="") {
				ft.setLocatefield(locatefields.getSelectedItem().toString());
			}
		}
		catch(Exception e) {
			ft.setLon(0);
			lon.setValue(0);
		}
		// END of location sort
	}
	
	//displays results on the results panel
	public void displayResults() {
		//refresh results
		displayPan.removeAll();
		updateDisplay();
		
		//adds a button and text field for each result found
		for (ProductBean bean: results) {
			JPanel buttons = new JPanel(new MigLayout());
			
			// Downloads document onto computer
			JButton dload = new JButton("Download");
			dload.setBorderPainted(false);
			dload.setFont(labels2);
			dload.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					final JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setAcceptAllFileFilterUsed(false);
					fc.showOpenDialog(fc);
					File f = fc.getSelectedFile();
					
					if(f!=null) {
						download.acceptDownload(bean.getFields(), bean.getValues(), bean.getId(), f);
					}
				}
			});
			
			// Deletes document from index
			JButton dlete = new JButton("Delete");
			dlete.setBorderPainted(false);
			dlete.setFont(labels2);
			dlete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						int answer = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure?" , "Delete Field", JOptionPane.YES_NO_OPTION);
						if(answer == 0) {
							remove.acceptRemove(bean.getId());
							search.doClick();
						}
					} catch (SolrServerException | IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			// list of fields that the document has
			ArrayList<String> choicefields = new ArrayList<String>();
			JComboBox<String> choices = new JComboBox<String>();
			choices.setPreferredSize(new Dimension(120, 40));
			choices.setMaximumSize(new Dimension(120, 40));
			
			choices.setFont(labels2);
			choices.setBackground(Color.white);
			choices.addItem("Select Field");
			for(String f:bean.getFields()) {
				if(!f.equals("_version_")) {
					choicefields.add(f);
					if(f.length()>10) {
						choices.addItem(f.substring(0, 10));
					}
					else {
						choices.addItem(f);
					}
				}
			}
			choices.setVisible(false);
			
			// displays LIST OF FIELDS and SAVE options
			JButton edit = new JButton("Edit");
			edit.setBorderPainted(false);
			edit.setFont(labels2);
			
			// RE-INDEXES document that has been edited
			JButton saver = new JButton("Save");
			saver.setBorderPainted(false);
			saver.setFont(labels2);
			saver.setVisible(false);
			
			edit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					choices.setVisible(!choices.isVisible());
					saver.setVisible(!saver.isVisible());
					editer.setEnabled(false);
					editer.setText("");
					ex.setEnabled(false);
					frame.getRootPane().setDefaultButton(search);
					
					ex.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							editer.setText("");
						}
					});
				}
			});
			
			// initializes the editing text field on the menu panel
			choices.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!editer.getText().equals("")) saver.removeActionListener(saver.getActionListeners()[0]);;
					editer.setText("");
					editer.setEnabled(false);
					ex.setEnabled(false);
					saver.setEnabled(false);
					
					if(choices.getSelectedIndex()!=0) {
						editer.setText(bean.getValue(bean.getFields().indexOf(choices.getSelectedItem().toString())).toString());
						editer.setEnabled(true);
						ex.setEnabled(true);
						saver.setEnabled(true);
						frame.getRootPane().setDefaultButton(saver);
						
						saver.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									index.reload(bean, choicefields.get(choices.getSelectedIndex()-1), editer.getText());
								} catch (SolrServerException | IOException e1) {}
								
								editer.setText("");
								editer.setEnabled(false);
								ex.setEnabled(false);
								frame.getRootPane().setDefaultButton(search);
								search.doClick();
							}
						});
					}
				}
			});
			
			//adds button and textfield to the results panel
			JPanel holder = new JPanel();
			holder.setBorder(BorderFactory.createLineBorder(Color.black));
			
			// Stores all document metadata in a table with field and value
			Object[][] data = new Object[Math.max(bean.numFields(), 5)][2];
			String[] heads = {"Field", "Values"};
			int[] extraLines = new int[bean.numFields()];
			
			for(int i=0; i<bean.numFields(); i++) {
				data[i][0]=bean.getField(i);
				data[i][1]=bean.getValue(i);
				if(bean.getValue(i).toString().length()>135) {
					extraLines[i] = bean.getValue(i).toString().length()/135;
				}
			}
			//data = whitespace(data, 4);
			
			//Initializes table
			JTable tbl = new JTable(data, heads);
			
			tbl.getColumn("Field").setMaxWidth(250);
			tbl.getColumn("Field").setMinWidth(250);
			tbl.getColumn("Values").setMinWidth(1100);
			tbl.setFont(new Font("Serif", Font.PLAIN, 20));
			tbl.getColumn("Values").setCellRenderer(new TextAreaRenderer());
			tbl.setRowHeight(42);
			tbl.setEnabled(false);
			
			// adds extra room for fields with large amounts of data
			for(int i=0; i<extraLines.length; i++) {
				if(extraLines[i]!=0) {
					tbl.setRowHeight(i, 40+(26*extraLines[i]));
					
				}
			}
			
			// adds all buttons and table to the display panel
			buttons.add(dload, "span");
			buttons.add(dlete, "span");
			buttons.add(edit, "span");
			buttons.add(saver, "span");
			buttons.add(choices, "span");
			holder.add(buttons);
			holder.add(tbl);
			displayPan.add(holder, "span");
		}
		
		//disables page buttons
		if(START==0) {
			prevPage.setEnabled(false);
		}
		else {
			prevPage.setEnabled(true);
			nextPage.setEnabled(false);
		}
		if((START+ROWS)<query.getFOUND()) {
			nextPage.setEnabled(true);
		}
		else {
			nextPage.setEnabled(false);
		}
		
		//refreshes documents found and what page user is on
		displayFound.setText("Found "+query.getFOUND()+" document(s)...("+
		START+" - "+(START+results.size())+")       ");
		
		//refreshes sort panel
		if(newDoc) {
			sorting();
		}
		refine();
		updateFields();
		menubar();
		suggest();
		newDoc=false;

		// start scroll at top of results after each search
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scroll.getVerticalScrollBar().setValue(0);

            }
        });
		
		// Displays "NO DOCUMENTS FOUND" if no results match the query
		if(results.size()==0) {
			displayPan.setVisible(false);
			displayPan.add(new JLabel("NO DOCUMENTS FOUND"));
			displayPan.setVisible(true);
		}
		
		finishProgress(1);
		updateDisplay();
		
	}
}


// Configures a JTextArea into the JTable
// This enables the storing of large fields by WRAPPING DATA to fit space
@SuppressWarnings("serial")
class TextAreaRenderer extends JScrollPane implements TableCellRenderer
{
   JTextArea textarea;
  
   public TextAreaRenderer() {
      textarea = new JTextArea();
      textarea.setLineWrap(true);
      textarea.setWrapStyleWord(true);
      textarea.setFont(new Font("Serif", Font.PLAIN, 18));
      getViewport().add(textarea);
   }
  
   // inputs cell text into the text area
   public Component getTableCellRendererComponent(JTable table, Object value,
                                  boolean isSelected, boolean hasFocus,
                                  int row, int column) {
	  if(value!=null) { 
		  textarea.setText(value.toString());
	  }
	  else {
		  textarea.setText("");
	  }
	  return this;
   }
}
