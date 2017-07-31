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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;

import net.miginfocom.swing.MigLayout;

public class GUI {
	// query vars
	private static int START = 0;
	private  static int ROWS = 10;
	
	// Object Storage
	private ArrayList<ProductBean> results;
	private String pricerange = "";
	private String selectedCats = "";
	private FieldTracker ft;
	private Remove remove = new Remove();
	private Refresh removeAll = new Refresh();
	private Query query = new Query(ft);
	private Index index = new Index();
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

	// display area
	private JPanel displayPan = new JPanel();
	private JScrollPane scroll = new JScrollPane(displayPan);
	private JPanel pages = new JPanel(new FlowLayout());
	private JButton nextPage = new JButton("Next");
	private JButton prevPage = new JButton("Prev");
	private JComboBox<Integer> numRows = new JComboBox<Integer>();
	private JLabel displayFound = new JLabel("Found ____ document(s)...       ");

	// sort
	private JPanel sort = new JPanel();
	private JPanel fieldsort = new JPanel(new FlowLayout()); 
	private JComboBox<String> ascdesc = new JComboBox<String>();
	private JPanel locate = new JPanel(new MigLayout());
	
	// refine
	private JPanel refine = new JPanel(new MigLayout());
	private JPanel price = new JPanel(new FlowLayout());
	private JTextField minPrice = new JTextField(5);
	private JTextField maxPrice = new JTextField(5);
	private JComboBox<String> fieldoptions = new JComboBox<String>();
	private JPanel facetfield = new JPanel(new MigLayout());
	private JPanel facetprice = new JPanel(new MigLayout());
	
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
		setImage();
		management();
		options();
		sorting();
		addComponents();
		initSearch();
		//refine();
		
		//Enter now works with search button
		frame.getRootPane().setDefaultButton(search);
		frame.setVisible(true);
		
	}

	// setup for the GUI frame
	public void setup() {
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(1790, 1150);
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		frame.setVisible(true);
		frame.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				//scroll.setPreferredSize(new Dimension((int)frame.getSize().getWidth()-315, (int)frame.getSize().getHeight()-150));
				//scroll.setMinimumSize(new Dimension((int)frame.getSize().getWidth()-315, (int)frame.getSize().getHeight()-150));
				//System.out.println(scroll.getSize());
				
			}
			@Override
			public void componentHidden(ComponentEvent e) {}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentShown(ComponentEvent e) {}
		});
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
		scroll.setPreferredSize(new Dimension(1450, 950));
		scroll.setMinimumSize(new Dimension(1450, 950));
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

		// add components to the frame
		pages.add(displayFound);
		pages.add(prevPage);
		pages.add(numRows);
		pages.add(nextPage);
		north.add(examples);
		north.add(addDoc);
		//north.add(deleteDoc);
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
		JLabel header2 = new JLabel("--------Search Results---------------");
		header2.setFont(headers);
		east.add(header2);
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
				
				//displays file explorer for user
				final JFileChooser fc = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("SOLR compatible", "xml", "csv", "json");
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
			}
		});

		// when Index Examples is clicked:
		examples.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					index.exampleDocs();
					newDoc=true;
				
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
				
				if(selected==null) {
					System.out.println("null");
				}
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
		
		JTextField lat = new JTextField(5);
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
		
		JTextField lon = new JTextField(5);
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
		refine.removeAll();
		facetfield.removeAll();
		facetprice.removeAll();
		holder.removeAll();
		
		// clear
		JButton clearfacet = new JButton("CLEAR");
		clearfacet.setFont(new Font("Serif", Font.ITALIC, 16));
		clearfacet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ft.setPricechoice("");
				ft.setFacetchoice("");
				minPrice.setText("");
				maxPrice.setText("");
				ft.setPriceQuery(false);
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
		
		JLabel lfacet = new JLabel("CATEGORIES___");
		lfacet.setFont(labels);
		holder.add(lfacet);
		holder.add(new JLabel("                       "));
		holder.add(clearfacet);
		refine.add(holder, "span");
		
		
		if(facets!=null) {
			FacetField facet = facets.get(0);
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
					
					button.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String choice = button.getText();
							choice = choice.split(" \\(")[0];
							if(choice.contains(" ")) {
								choice = "\""+choice+"\"~0";
							}
							
							selectedCats += choice+", ";
							ft.setFacetchoice(selectedCats);
							query.updateFT(ft);
							search.doClick();
						}
					});
					
					facetfield.add(button, "span");
				}
			}
			refine.add(facetfield, "span");
		}
		
		//spacing
		refine.add(new JLabel(), "span");
		
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
	}

	//displays results on the results panel
	public void displayResults() {
		//refresh results
		displayPan.removeAll();
		
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
			
			for(int i=0; i<bean.numFields(); i++) {
				p1.append(" :"+bean.getField(i)+"\n");
				p2.append("  "+bean.getValue(i)+"\n");
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
		scroll.setLocation(1000,  1000);
		
		//refreshes sort panel
		if(newDoc) {
			sorting();
		}
		refine();
		newDoc=false;
		
	}
}
