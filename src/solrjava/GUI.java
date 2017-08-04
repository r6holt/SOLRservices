package solrjava;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;

import net.miginfocom.swing.MigLayout;

public class GUI {
	private static int START = 0;
	private  static int ROWS = 10;
	
	// Object Storage
	private ArrayList<ProductBean> results;
	private String pricerange = "";
	private String selectedCats = "";
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
	JFrame frame = new JFrame("SOLR Search Services");
	private JPanel west = new JPanel();
	private JPanel east = new JPanel();

	// search
	private JPanel north = new JPanel(new FlowLayout());
	private JTextField searchbar = new JTextField(45);
	private JButton search = new JButton("      Search      ");
	private JButton addDoc = new JButton("Upload File");
	private JButton deleteDoc = new JButton("Remove By ID");
	private JButton refresh = new JButton("Empty");
	private JButton examples = new JButton("Examples");
	private JComboBox<String> querycat = new JComboBox<String>();
	
	// schema management
	private JPanel menu = new JPanel(new FlowLayout());
	private JTextField nfield = new JTextField(12);
	private JButton addition = new JButton("Add Field");
	

	// display area
	private JPanel displayPan = new JPanel();
	private JScrollPane scroll = new JScrollPane(displayPan);
	private JPanel pages = new JPanel(new FlowLayout());
	private JButton nextPage = new JButton("Next");
	private JButton prevPage = new JButton("Prev");
	private JComboBox<Integer> numRows = new JComboBox<Integer>();
	private JLabel displayFound = new JLabel("Found ____ document(s)...       ");
	private JPanel docpanel = new JPanel(new MigLayout());
	private JProgressBar progress = new JProgressBar();

	// sort
	private JPanel sort = new JPanel();
	private JPanel fieldsort = new JPanel(new FlowLayout()); 
	private JComboBox<String> ascdesc = new JComboBox<String>();
	private JPanel locate = new JPanel(new MigLayout());
	JTextField lat = new JTextField(5);
	JTextField lon = new JTextField(5);
	
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
	
	
	

	// constructor for GUI
	public GUI() throws SolrServerException, IOException, InterruptedException {
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
		setImage();
		management();
		menubar();
		options();
		updateFields();
		sorting();
		addComponents();
		initSearch();
		//refine();
		
		//Enter now works with search button
		frame.getRootPane().setDefaultButton(search);
		frame.setVisible(true);
		
		Thread.sleep(500);
		search.doClick();
	}

	// setup for the GUI frame
	public void setup() {
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setMaximumSize(new Dimension(1790, 1190));
		frame.setExtendedState(frame.MAXIMIZED_BOTH);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setVisible(true);
	}
	
	public void setImage() {
		
		BufferedImage logo = null;
		try {
			logo = ImageIO.read(new File("images"+File.separator+"solr.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JLabel img = new JLabel(new ImageIcon(logo));
		//img.setBorder(BorderFactory.createLineBorder(Color.gray));
		img.setPreferredSize(new Dimension(320, 200));
		img.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		
		west.add(img);
	}

	public void addComponents() {
		Font font1 = new Font("SansSerif", Font.PLAIN, 26);
		Font font2 = new Font("SansSerif", Font.ITALIC, 22);
		
		// setup components
		displayPan.setLayout(new MigLayout());
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(1830, 1160));
		scroll.setMinimumSize(new Dimension(1830, 1160));
		scroll.getVerticalScrollBar().setPreferredSize(new Dimension(32, 0));
		scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 20));
		searchbar.setFont(font1);
		querycat.setFont(font2);
		search.setFont(new Font("Serif", Font.BOLD, 20));
		sort.setLayout(new MigLayout());
		west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
		east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
		sort.setBorder(BorderFactory.createLineBorder(Color.black));
		refine.setBorder(BorderFactory.createLineBorder(Color.black));
		displayFound.setFont(new Font("SansSerif", Font.PLAIN, 18));
		JLabel header2 = new JLabel("--------Search Results---------------");
		header2.setFont(headers);
		progress.setPreferredSize(new Dimension(150, 15));
		progress.setForeground(Color.green);
		progress.setBackground(Color.white);

		// add components to the frame 
		pages.add(progress);
		pages.add(new JLabel("                                   "));
		pages.add(displayFound); 
		pages.add(prevPage);
		pages.add(numRows);
		pages.add(nextPage);
		pages.add(new JLabel("                                                                                                                                          "));
		north.add(examples);
		north.add(addDoc);
		north.add(refresh);
		north.add(new JLabel("                              "));
		north.add(querycat);
		north.add(searchbar);
		north.add(search); 
		north.add(new JLabel("                    "));
		JLabel header1 = new JLabel("               --------Sort--------");
		header1.setFont(headers);
		west.add(header1);
		west.add(sort);
		JLabel lrefine = new JLabel("              ------Refine------");
		lrefine.setFont(headers);
		west.add(lrefine, "span");
		west.add(refine);
		east.add(header2);
		east.add(menu);
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
				if(reset) {
					START=0;
				}
				
				//Search for query specified
				query.updateFT(ft);
				try {
					results = query.acceptQuery(searchbar.getText(), START, ROWS);
				} 
				catch (SolrServerException | IOException e1) {
					e1.printStackTrace();
				}
				//update search results
				displayResults();
				updateDisplay();
				reset=true;
			}
		});
	}

	public void management() {
		// when add doc is clicked:
		addDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				progress.setValue(50);
				
				//displays file explorer for user
				final JFileChooser fc = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("SOLR compatible", "xml", "csv", "json", "pdf");
				fc.setFileFilter(filter);
				fc.showOpenDialog(fc);
				File f = fc.getSelectedFile();
				
				//index selected file if it is correct file type
				if (f == null) {}
				else {
					try {
						index.acceptDocument(f);
						newDoc=true;
					} catch (SolrServerException | IOException e1) {
						e1.printStackTrace();
					}
				}
				progress.setValue(100);

			}
		});

		// when delete doc is clicked:
		deleteDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//creates remover object to remove by ID
				try {
					remove.acceptRemove();
				} catch (SolrServerException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// when refresh is clicked:
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				progress.setValue(50);
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
				progress.setValue(100);
			}
		});

		// when Index Examples is clicked:
		examples.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				progress.setValue(50);
				
				try {

					index.exampleDocs();
					newDoc=true;
				
				} catch (SolrServerException | IOException e1) {
					e1.printStackTrace();
				}
				progress.setValue(100);
			}
		});
	}
	
	//initializes schema maintenance menu bar
	public void menubar() {	
		menu.setBorder(BorderFactory.createLineBorder(Color.black));
		
		String[] options = {"string", "float", "date", "location", "boolean"};
		JComboBox<String> dataType = new JComboBox<String>();
		dataType.setFont(labels);
		JCheckBox cb = new JCheckBox();
		JTextField fill = new JTextField(10);
		JButton removeField = new JButton("Delete Field");
		JLabel ask2 = new JLabel("         Value:");
		JLabel filler = new JLabel("                                 ");
		JLabel filler2 = new JLabel("                                           ");
		JLabel filler3 = new JLabel("                                           ");
		JLabel fillerd = new JLabel("    Today's Date                           ");
		
		ask2.setFont(labels);
		fill.setFont(labels);
		nfield.setFont(labels);
		addition.setFont(labels);
		removeField.setFont(labels);
		fill.setVisible(false);
		ask2.setVisible(false);
		filler.setVisible(false);
		filler3.setVisible(false);
		fillerd.setVisible(false);
		
		for(String s:options) dataType.addItem(s);
		
		JLabel ask = new JLabel("add field to search results");
		ask.setFont(labels);
		menu.add(filler);
		menu.add(dataType);
		menu.add(nfield);
		menu.add(addition);
		menu.add(cb);
		menu.add(ask);
		menu.add(ask2);
		menu.add(fill);
		menu.add(fillerd);
		menu.add(filler2);
		menu.add(new JLabel("                                         "));
		menu.add(removeField);
		menu.add(filler3);
		
		
		addition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!nfield.getText().trim().equals("")) {
					try {
						schemaEditor.addField(nfield.getText(), dataType.getSelectedItem().toString());
						ft.update();
						results = query.acceptQuery(searchbar.getText(), 0, (int)query.getFOUND());
					} catch (SolrServerException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
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
							index.reload(results, "");
						} catch (SolrServerException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						search.doClick();
					}
				}
				
				
				fill.setText("");
				nfield.setText("");
				cb.setSelected(false);
			}
		});
		
		removeField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				progress.setValue(15);
				String delete = null;
				try {
					delete = schemaEditor.deleteField(ft);
					ft.update();
					searchbar.setText("");
					ft.setCategory("null");
					clearfacet.doClick();
					
					results = query.acceptQuery("", 0, (int)query.getFOUND());
				} catch (SolrServerException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if(results!=null && delete!=null) {
					try {
						index.reload(results, delete);
					} catch (SolrServerException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					search.doClick();
				}
			}
		});
		
		cb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(dataType.getSelectedItem().equals("date")) {
					fillerd.setVisible(!fillerd.isVisible());
					filler.setVisible(!filler.isVisible());
					filler2.setVisible(!filler2.isVisible());
					filler3.setVisible(!filler3.isVisible());
				}
				else {
					fill.setVisible(!fill.isVisible());
					ask2.setVisible(!ask2.isVisible());
					filler.setVisible(!filler.isVisible());
					filler2.setVisible(!filler2.isVisible());
					filler3.setVisible(!filler3.isVisible());
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
		
		querycat.setMaximumSize(new Dimension(60, 30));
		querycat.setForeground(Color.black);
		querycat.setBackground(Color.white);
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

		fieldoptions.addItem("id");
		fieldoptions.addItem("location");
		fieldoptions.addItem("_version_");
		
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
		JLabel lsort = new JLabel("   FIELD____");
		lsort.setFont(labels);
		sort.add(lsort, "span");
		sort.add(fieldsort, "span");
		sort.add(locate, "span");
		
	}
	
	public void sortLocation() {
		locate.removeAll();
		
		JLabel l = new JLabel("        Latitude:");
		l.setFont(labels);
		locate.add(l);
		
		lat.setFont(labels);
		lat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ft.setLat(Double.parseDouble(lat.getText()));
			}
		});
		locate.add(lat, "wrap");
		
		JLabel l2 = new JLabel("        Longitude:");
		l2.setFont(labels);
		locate.add(l2);
		
		lon.setFont(labels);
		lon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ft.setLon(Double.parseDouble(lon.getText()));
			}
		});
		locate.add(lon, "wrap");
		updateDisplay();
		
	}

	
	public void refine() {		
		//faceting
		List<FacetField> facets = query.getFacet();
		JPanel holder = new JPanel(new FlowLayout());
		facetfields.clear();
		refine.removeAll();
		facetfield.removeAll();
		facetprice.removeAll();
		holder.removeAll();
		
		// clear
		clearfacet.setFont(new Font("Serif", Font.ITALIC, 16));
		clearfacet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ft.setPricechoice("");
				//ft.setFacetchoice("");
				minPrice.setText("");
				maxPrice.setText("");
				ft.setPriceQuery(false);
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
		});
		
		JComboBox<String> pickFacet = new JComboBox<String>();
		pickFacet.setFont(labels);
		facetfield.setLayout(new BoxLayout(facetfield, BoxLayout.Y_AXIS));
		facetfield.setAlignmentY(Component.LEFT_ALIGNMENT);		
		
		holder.add(pickFacet);
		holder.add(new JLabel("                                        "));
		holder.add(clearfacet);
		refine.add(holder, "span");
		
		int count = -1;
		if(facets!=null && facets.size()!=0) {
			for(int h=0; h<facets.size(); h++) {
				FacetField facet = facets.get(h);
				
				if(!facet.getName().equals("id") && !facet.getName().equals("_version_") && !facet.getValues().isEmpty()) {
					count++;
					JPanel jp = new JPanel(new MigLayout());
					jp.setVisible(false);
					facetfields.add(jp);
					pickFacet.addItem(facet.getName());
				
					JLabel label = new JLabel(facet.getName());
					label.setFont(labels);
					facetfields.get(count).add(label, "span");
					ft.setChoices(facet.getName());
					for(int i=0; i<facet.getValues().size() && i<5; i++) {
						if(facet.getValues().get(i).getCount()!=0) {
							JButton button = new JButton(facet.getValues().get(i).getName()+" ("+facet.getValues().get(i).getCount()+")");
							button.setBorderPainted(false);
							button.setFont(new Font("SansSerif", Font.ITALIC, 18));
							button.setForeground(Color.blue);
							button.setOpaque(false);
							button.setContentAreaFilled(false);
							
							if(selectedCats.contains(button.getText().split(" ")[0])) {
								button.setForeground(Color.BLACK);
								button.setEnabled(false);
							}
							
							int index = count;
							button.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									String choice = button.getText();
									choice = choice.split(" \\(")[0];
									if(choice.contains(" ")) {
										choice = "\""+choice+"\"~0";
									}
									
									selectedCats += choice+", ";
									ft.setFacetchoice(index, choice);//selectedCats);
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
		ft.setFacetchoice(new String[count]);
		
		pickFacet.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String selected = e.getItemSelectable().getSelectedObjects()[0].toString();
				for(int i=0; i<facetfields.size(); i++) {
					if(facetfields.get(i).getName().equals(selected)) {
						facetfields.get(i).setVisible(true);//!facetfields.get(i).isVisible());
					}
				}
			}
		});
		
		//spacing
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
		
			if(range!=null) {
				Iterator<Entry<String, Integer>> subset = range.entrySet().iterator();
				for(int i=0; i<range.size(); i++) {
					String format = subset.next().toString().split("\\[")[1];
					String num = format.split("]")[1].substring(1);
					format = format.split("]")[0];
					format = format.replaceAll("TO \\*", "and up");
					//if(!num.equals("0")) {
						JCheckBox box = new JCheckBox();
						JLabel button = new JLabel(format+"   ("+num+")");//+facet.next().getEnd());//facet.getValues().get(i).getName()+" ("+facet.getValues().get(i).getCount()+")");
						button.setFont(new Font("SansSerif", Font.ITALIC, 18));
						button.setForeground(Color.black);
						box.setSelected(isSelected(i));
						
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
					//}
				}
				refine.add(facetprice, "span");
			}
		}
		updateDisplay();
	}
	
	public void updateFields() {
		//fields update
		querycat.removeAllItems();
		querycat.addItem("All:");
		for(int i=0; i<ft.numFields(); i++) {
			querycat.addItem(ft.getField(i));
		}
	}
	
	// updates frame for new content
	public void updateDisplay() {
		frame.setVisible(true);
	}
	
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
		
		ft.setSortfield(fieldoptions.getSelectedItem().toString());
		ft.setSort(ascdesc.getSelectedItem().toString());
		
		if(querycat.getSelectedIndex()!=0) {
			ft.setCategory(querycat.getSelectedItem().toString());
		}
		else {
			ft.setCategory("null");
		}
		
		try {
			ft.setLat(Double.parseDouble(lat.getText()));
		}
		catch(Exception e) {
			ft.setLat(0);
			lat.setText("0");
		}
		try {
			ft.setLon(Double.parseDouble(lon.getText()));
		}
		catch(Exception e) {
			ft.setLon(0);
			lon.setText("0");
		}
	}
	
	//displays results on the results panel
	public void displayResults() {
		//refresh results
		displayPan.removeAll();
		updateDisplay();
		
		//adds a button and text field for each result found
		for (ProductBean bean: results) {
			JButton b = new JButton("Download");
			b.setBorderPainted(false);
			b.addActionListener(new ActionListener() {
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

			Font f1 = new Font("Serif", Font.ITALIC, 22);
			Font f2 = new Font("Serif", Font.PLAIN, 20);
			
			//adds button and textfield to the results panel
			JPanel holder = new JPanel();
			holder.setBorder(BorderFactory.createLineBorder(Color.black));
			
			JTextArea p1 = new JTextArea();
			JTextArea p2 = new JTextArea();
			p1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			
			int j=0;
			for(int i=0; i<bean.numFields(); i++) {
				p1.append(" :"+bean.getField(i)+"\n");
				
				String val = "";
				for(j=100; j<(bean.getValue(i).toString().replaceAll("\n", "").length()-1); j+=100) {
					val+=bean.getValue(i).toString().replaceAll("\n", "").substring(j-100, j)+"\n";
					p1.append("\n");
				}
				val+=bean.getValue(i).toString().replaceAll("\n", "").substring(j-100); //bean.getValue(i).toString().replaceAll("\n", "").length());
				p2.append("  "+val+"\n");//bean.getValue(i).toString().replaceAll("\n", "")+"\n");
			}
			
			p1.setFont(f1);
			p1.setEditable(false);
			p2.setFont(f2);
			p2.setEditable(false);
			holder.add(b);
			holder.add(p1);
			holder.add(p2);
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
		newDoc=false;

		// start scroll at top of results after each search
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scroll.getVerticalScrollBar().setValue(0);

            }
        });
		
		if(results.size()==0) {
			displayPan.removeAll();
			displayPan.add(new JLabel("NO DOCUMENTS FOUND"));
		}
		
		progress.setValue(100);
		updateDisplay();
		displayPan.setVisible(true);
		
	}
}
