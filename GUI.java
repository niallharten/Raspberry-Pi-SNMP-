package ie.ait.networksProject;

import java.awt.EventQueue;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JTextField;


public class GUI {

	private JFrame frame;
	private JTextField deleteTextField;
	private JTextField addAgentTextField;
	private JTextField runAgentTextField;  //declare the textfields 
	private JTextField viewChartTextField;
	private JButton btnViewAllData;
	public static ArrayList<String> agents = new ArrayList<String>(); //create an arraylist to store the agents
	
	

	
	
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI(); //create a new instance of the GUI
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws UnknownHostException 
	 */
	public GUI() throws UnknownHostException {
		
		Thread t = new Poller(); //calle the poller thread
		t.start(); //start the thread
		initialize(); //call the method to create the gui
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws UnknownHostException 
	 */
	private void initialize() throws UnknownHostException {
		//add the standard IP addrress to the list
		agents.add("192.168.1.10");
		
	
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().setForeground(Color.BLACK);
		frame.setBounds(100, 100, 450, 300);               
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
	
		frame.getContentPane().setLayout(gridBagLayout);
		
		/**
		 So here the JFrame is created. It is set so it can't be resized. Set the colour and create
		 the layout to be gridbag layout. 
		 * */
		
		JButton btnRunAgent = new JButton("Run agent by IP"); //add the button run the agent
		GridBagConstraints gbc_btnRunAgent = new GridBagConstraints();
		gbc_btnRunAgent.insets = new Insets(0, 0, 5, 0);
		gbc_btnRunAgent.gridx = 1;
		gbc_btnRunAgent.gridy = 0;
		frame.getContentPane().add(btnRunAgent, gbc_btnRunAgent);
	
		btnRunAgent.addActionListener(new ActionListener() { //this is the action listener for the run agent button
			public void actionPerformed(ActionEvent e) {
				try {
				
				String searchIP = runAgentTextField.getText(); //read in the textfield
				 int searchColumnIndex = 0; //this the column we want
				    
				 String resultRow = null;
				 
				 BufferedReader br = new BufferedReader(new FileReader("metrics.csv")); //read in the metrics file
				     
				 String line;
				 
				 while ( (line = br.readLine()) != null ) {
				         String[] values = line.split(",");
				         if(values[searchColumnIndex].equals(searchIP)) {
				             resultRow = line;
				             break;
				             /*
				              Here it will loop forever, read the next line, at the searchColumnIndex column, which is zero
				              this is done because the IP is needed. It will check if the data is equal to the required IP and make 
				              it equal to resultRow. This will store all the information in the row and let it to resultsRow.
				               */
				         }
				     }
				 
				     br.close();
				    
				     String cvsSplitBy = ",";

				     String[] runAgentData = resultRow.split(cvsSplitBy); //split our ip data at every ",".

		    System.out.println("IP: " + runAgentData[0] + ", Date:" + runAgentData[1]+", Time:" + runAgentData[2]+", Temp:" + runAgentData[4]
		                		+", Humidity:" + runAgentData[6]+", Pressure:" + runAgentData[8]);
				     
				 //So here print out each elements in the row that was split. The way our csv file is, there is a few spaces. We had to make
		    	//equal to the correct column length, hence it jumps in twos.
			}catch(Exception z) {
				JOptionPane.showMessageDialog(frame,
					    "Please enter a correct IP address"); //if not valid ip, it will pop up this
					String error = "Incorrect IP address entered "+Instant.now().toString(); //pass this into the error function in poller with time and date
					Poller.saveError(error);
				}
			}
			
		});
		
		runAgentTextField = new JTextField(); //text field for the run agent textfield
		GridBagConstraints gbc_runAgentTextField = new GridBagConstraints();
		gbc_runAgentTextField.insets = new Insets(0, 0, 5, 0);
		gbc_runAgentTextField.gridx = 1;
		gbc_runAgentTextField.gridy = 1;
		frame.getContentPane().add(runAgentTextField, gbc_runAgentTextField);
		runAgentTextField.setColumns(10);
		
		
		
		
		JButton btnAddAgent = new JButton("Add Agent "); //add agent button
		GridBagConstraints gbc_btnAddAgent = new GridBagConstraints();
		gbc_btnAddAgent.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddAgent.gridx = 1;
		gbc_btnAddAgent.gridy = 5;
		frame.getContentPane().add(btnAddAgent, gbc_btnAddAgent);
		
		
		
		
		btnAddAgent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String addIP = addAgentTextField.getText();
				 try {
				        if ( addIP == null || addIP.isEmpty() ) {
				        	JOptionPane.showMessageDialog(frame,
								    "Please enter a valid IP");
				        }
				        												// here we are checking to see if the input is legit.
				        String[] parts = addIP.split( "\\." );			// it will check if its empty, it will split the text
				        if ( parts.length != 4 ) {						//if the string is not equal to 4 it will fail
				        	JOptionPane.showMessageDialog(frame,
								    "Please enter a valid IP");
				        }

				        for ( String s : parts ) {
				            int i = Integer.parseInt( s );
				            if ( (i < 0) || (i > 255) ) {    //parses the string to an int and check if it is in IP range
				            	JOptionPane.showMessageDialog(frame,
									    "Please enter a valid IP");
				            }
				        }
				        if ( addIP.endsWith(".") ) { //if it ends with a . it will fail
				        	JOptionPane.showMessageDialog(frame,
								    "Please enter a valid IP");
				        }

				        agents.add(addIP);
				        JOptionPane.showMessageDialog(frame,
							    addIP+" Added succesfully");//if the string passes all these tests it will add it to the list
				    } catch (NumberFormatException nfe) {
				    	String error = "Failed to add IP: "+addIP; //pass this into the error function in poller with time and date
				    	Poller.saveError(error);
				        
				    }
				 
				}
			}	
		);
		
		addAgentTextField = new JTextField();  //text field for add agent
		GridBagConstraints gbc_addAgentTextField = new GridBagConstraints();
		gbc_addAgentTextField.insets = new Insets(0, 0, 5, 0);
		gbc_addAgentTextField.gridx = 1;
		gbc_addAgentTextField.gridy = 6;
		frame.getContentPane().add(addAgentTextField, gbc_addAgentTextField);
		addAgentTextField.setColumns(10);
		
		
		JButton btnDeleteAgent = new JButton("Delete Agent "); //the delete button for agents
		GridBagConstraints gbc_btnDeleteAgent = new GridBagConstraints();
		gbc_btnDeleteAgent.insets = new Insets(0, 0, 5, 0);
		gbc_btnDeleteAgent.gridx = 1;
		gbc_btnDeleteAgent.gridy = 7;
		frame.getContentPane().add(btnDeleteAgent, gbc_btnDeleteAgent);
		
		
		btnDeleteAgent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String remove = deleteTextField.getText();
				
				if(agents.contains(remove)) {
				agents.remove(remove);                  //here if the arraylist contains the input
				JOptionPane.showMessageDialog(frame,
					    remove+" removed from the list");
				}else {									//it will remove it. If not it will prompt an error dialogue 
					JOptionPane.showMessageDialog(frame,
						    remove+" is not an active agent");
							
				}
			}
		});
		
		deleteTextField = new JTextField(); //text field for the delete
		GridBagConstraints gbc_deleteTextField = new GridBagConstraints();
		gbc_deleteTextField.insets = new Insets(0, 0, 5, 0);
		gbc_deleteTextField.gridx = 1;
		gbc_deleteTextField.gridy = 8;
		frame.getContentPane().add(deleteTextField, gbc_deleteTextField);
		deleteTextField.setColumns(10);
		
		btnViewAllData = new JButton("View all Data in text File"); //create button to view all the data
		GridBagConstraints gbc_btnViewAllData = new GridBagConstraints();
		gbc_btnViewAllData.gridx = 1;
		gbc_btnViewAllData.gridy = 9;
		frame.getContentPane().add(btnViewAllData, gbc_btnViewAllData);
		

		btnViewAllData.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame("Storage of Agents Details");
				final JTextArea textArea = new JTextArea(30, 60); //here we are created a text area
																 //here this action listener will load in the text file into the textArea
			    frame.getContentPane().add( new JScrollPane(textArea), BorderLayout.NORTH );
			    //creating the frame and a scrollpane with the TextArea
			    
			    textArea.setEditable(false);
			    //set the editable to false so it can't be edited
			    
			    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    frame.pack();
			    frame.setLocationRelativeTo( null );
			    frame.setVisible(true);
			    
				try {
				
				FileReader reader = new FileReader("Agents.txt");
				textArea.read(reader, "Agents.txt"); //here it will read in the Agents.txt file and load it
				}catch (Exception f) {
					JOptionPane.showMessageDialog(frame,
						    "Failed to load Data");
						String error = "Failed to load Agents.txt at: "+Instant.now().toString(); //pass this into the error function in poller with time and date
						Poller.saveError(error);
					}
			}
		});	
	}	
}