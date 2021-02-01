package ie.ait.networksProject;
	
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
	
	public class Poller extends Thread {
		
	public void run(){ //start of the thread
		
		while(true) { //run forever
			
			try {
				
				 InetAddress hostname = null; 
			
				 for(String agnt : GUI.agents) { //loop through our arraylist in the GUI class
					try {
						hostname = InetAddress.getByName(agnt); //make the hostname equal to the current element in the arraylist
					}catch (UnknownHostException e1) {
						
						String fail = "Invalid host of IP: "+hostname+" at "+Instant.now().toString();
						saveError(fail);
						
					}
					
				}
			
			String hostAddress = hostname.getHostName();
			String port = "161";
			String community = "public";
			int version = SnmpConstants.version2c;
			
		
			String getValues = ".1.3.6.1.4.1.8072.1.3.2.3.1.2.9.103.101.116.86.97.108.117.101.115"; 
			//our OID to get the values
			
			
			
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(community));
			comtarget.setVersion(version);
			comtarget.setAddress(new UdpAddress(hostAddress + "/" + port));
			comtarget.setRetries(2);
			comtarget.setTimeout(1000);
			
			
			
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID(getValues))); //name of the files on the pi
			
			
			pdu.setType(PDU.GET);
			
			Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
			snmp.listen();
			

			ResponseEvent response = snmp.get(pdu, comtarget);
			PDU responsePDU = response.getResponse();
			
			if(response.getResponse() == null) {
				System.out.println("Response timed out...");
			}else {
			
				VariableBinding[] varBinds = responsePDU.toArray();
					//loop through our varbinds which we have recieved from the pi
				for(int i=0; i<varBinds.length; i++) {
						
				int errorStatus = responsePDU.getErrorStatus();
				
						if(errorStatus == PDU.noError) { //if we have no errors, we will contniue
					
							try {
								
								String values = varBinds[i].getVariable().toString(); //save our values at position i to a string values
									
									writeToTextFile(values, hostname); //we will pass in our values and the hostname, into both our methods
									writeToCSVFile(values, hostname);
							
									Thread.sleep(5000); //sleep for 5 seconds
								
								}catch(Exception e) {
									String fail = "Error: Response Timed Out at: "+Instant.now().toString();
									saveError(fail); //pass this into the error function in poller with time and date
									}
		
								}
						}
				}
			}catch(Exception e) {
				String fail = "Error: Response Timed Out at: "+Instant.now().toString();
				saveError(fail); //pass this into the error function in poller with time and date
			}
			
		}
	
				
	}//end run 
			
			
			
			
	public static void writeToCSVFile(String values, InetAddress agent) throws IOException {
			
			String[] currentValues = values.split(" "); //split our values string at every space and store it in our array
			String hostIP = agent.getHostAddress();  //store the ip as hostIP for the current ip in the arrraylist
			
		
	    try (PrintWriter writer = new PrintWriter(new FileWriter("metrics.csv", true))) { //create new csv file

			      StringBuilder sb = new StringBuilder(); //create instance of our stringbuilder
			    
			      sb.append(hostIP);
			      sb.append(',');
			      sb.append(currentValues[0]);
			      sb.append(',');
			      sb.append(currentValues[1]);
			      sb.append(',');
			      sb.append(currentValues[2]);
			      sb.append(',');
			      sb.append(currentValues[3]);
			      sb.append(',');
			      sb.append(currentValues[4]);
			      sb.append('\n');
			      
			      /**
			        Here this is appending the hostIp etc. It will put a comma to separate the values.
			        It will write out each value at the index specified.
			       */
		
			      writer.write(sb.toString());
	    	} catch (FileNotFoundException e) {
	    			String fail = "File not found for csv at: "+Instant.now().toString();
	    			saveError(fail); //pass this into the error function in poller with time and date
	    		}

	  }
	 
	
		
		
		
	
		public static void writeToTextFile(String values, InetAddress agent2) throws IOException {
				
			String[] currentValues = values.split(" "); //very similar to the CSV function. Split the values
			
			String hostIP = agent2.getHostAddress(); 
			currentValues[0]   = "The date is: "+ currentValues[0]; //overwrite our data in the array with this new data
	        currentValues[1] = "The time is: "+currentValues[1];	//this is done so we can read the data more clearly rather than 
	        currentValues[2] = "Temp was: "+currentValues[2];		//just the values
	        currentValues[3] = "Humidity was: "+currentValues[3];
	        currentValues[4] = "Pressure was: "+currentValues[4];
	        
	        
			BufferedWriter outputWriter = null;
			  outputWriter = new BufferedWriter(new FileWriter("Agents.txt", true)); //create new file
			  
			  for (int i = 0; i < currentValues.length; i++){
			    
					    outputWriter.write(hostIP); 
					    outputWriter.newLine();
						outputWriter.write(currentValues[i]+"");	//for each element in the array loop through
					    outputWriter.newLine();						//it will then write out the IP and each values at index i
					 
					    outputWriter.newLine();
			  
			  			}
			 
						  outputWriter.write("===="); //done this to make it more readable
						  outputWriter.newLine();
						  outputWriter.flush();  
						  outputWriter.close();  
			}
		
		public static void saveError(String error) {
			File file = new File("log.txt"); //create log file 
			FileWriter fw = null;
			try {
				fw = new FileWriter(file, true);
				fw.write(error + System.getProperty("line.separator")); //seperate the lines
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// close resources
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
