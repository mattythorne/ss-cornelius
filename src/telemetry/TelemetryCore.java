package telemetry;
import java.io.*;
import java.net.*;

/**
* <h1>TelemetryCore</h1>
* This is the core telemetry class.
* It provide methods for reading in a telemetry XML
* configuration file and setting up a client and server.
*
* @author  Matt Thorne
* @version 1.0
* @since   2015-03-25
*/

public class TelemetryCore {
	
	private String XMLFileName;
	@SuppressWarnings("unused")
	private String serverIP;
	private String serverPort;
	private String partnerIP;
	private String partnerPort;
	private boolean configRead=false;
	protected static int LEVEL=1;
	protected static int FREQ=2;
	
	/**
	  * Constructor.
	  * 
	  * @param XMLFileName (required) Quailified filename of XML file for telemetry settings
	  */
	public TelemetryCore(String XMLFileName){
		this.XMLFileName = XMLFileName;
	}
	
	/**
	 * Parses the XML file specified in the constructor and starts the telemetry channel listening
	 * 
	 * @return <tt>true</tt> Only if successful
	 */
	public boolean run(){
		boolean result = true;
		
		result = preTelemetryConfiguration();
		
		if ((result==true)&(configRead==false)) result = readConfigFile();
		if (result==true) startTelemetryListen();
		return result;
	}
	
	/**
	 * Method to be overridden by a sub class to provide functionality 
	 * prior to starting the telemetry via the run method 
	 * 
	 * @return <tt>true</tt> Only if successful
	 */
	protected boolean preTelemetryConfiguration(){
		boolean result = true;
		
		return result;
	}
	
	/**
	 * Parses the XML file specified in the constructor
	 * 
	 * @return <tt>true</tt> Only if successful
	 */
	public boolean readConfigFile(){
		boolean result = true;
		utility.Logging.log(1, "Reading config file");
		xmlparser.XMLFile XML = new xmlparser.XMLFile(XMLFileName);
		serverIP = XML.getElementNode("telemetry", "serverip");
		serverPort = XML.getElementNode("telemetry", "serverport");		
		partnerIP = XML.getElementNode("telemetry", "partnerip");
		partnerPort = XML.getElementNode("telemetry", "partnerport");
		configRead=result;	
		return result;
	}
	
	/**
	  * Sets the telemetry config file for this instance
	  * 
	  * @param XMLFileName (required) Quailified filename of XML file for telemetry settings
	  */
	public void setConfigFile(String XMLFileName){
		this.XMLFileName = XMLFileName;
	}
	
	/**
	 * Configures objects neccesary for the telemetry server and 
	 * starts the telemetry listening for commands
	 * 
	 * @return <tt>true</tt> Only if successful
	 */
	private boolean startTelemetryListen(){
		// netstat -atp tcp | grep -i "listen"
		boolean result=true;
		utility.Logging.log(1, "starting telemetry listen");
		String telemetryRecieved;
	    String telemetryReply;
	        
	        try{
	        	ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(serverPort));
	        	utility.Logging.log(1, "listening on port " + serverPort);
	        	while(true) {
	        		Socket connectionSocket = welcomeSocket.accept();
	        		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	        		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
	        		telemetryRecieved = inFromClient.readLine();
	        		utility.Logging.log(4, telemetryRecieved);
	        		telemetryReply = telemetryAction(telemetryRecieved);
	        		outToClient.writeBytes(telemetryReply + '\n');
	        		utility.Logging.log(5, telemetryReply);
	        	}
	        }catch (Exception e) {
				e.printStackTrace();
				utility.Logging.log(2,  e.toString());
				result=false;
		    }
		return result;
	}
	
	/**
	 * Sends a command to the teletry partner specified in the XML file
	 * 
	 * @return the response from the partner
	 */
	public String sendTelemetry(String telemetryMessage){
		String result="";
		
        String telemetryReply;
        try{
        	
        	Socket clientSocket = new Socket(partnerIP, Integer.parseInt(partnerPort));
        	DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        	BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        	outToServer.writeBytes(telemetryMessage + '\n');
        	utility.Logging.log(2, telemetryMessage);
        	telemetryReply = inFromServer.readLine();
        	utility.Logging.log(3, telemetryReply);
        	result = telemetryReply;
        	
        	clientSocket.close();
        	
        }catch (Exception e) {
        	e.printStackTrace();
        	utility.Logging.log(2, e.toString());
        }
		
		return result;
	}
	
	/**
	 * Method to be overridden by a sub class to handle telemetry messages.
	 * Default behavior is to echo the message sent
	 * 
	 * @param recievedMessage Message recieved from telemetry partner
	 * @return the response to be sent to the partner
	 */
	protected String telemetryAction(String recievedMessage){
		String result = "UNKNOWN - " + recievedMessage;
		
		return result;
	}
	
	/**
	 * Retrieves the telemtry signal data component specified by the parameter
	 * 
	 * @param signal Component to be read <tt>1</tt> = Signal Level db <tt>2</tt> = Signal Frequency
	 * @return String representation of the signal data
	 */
	protected String getSignalData(int signal){
		// iwconfig wlan0 | grep -Po 'Signal level=\K.*?(?= )'
		// iwconfig wlan0 | grep -Po 'Frequency:\K.*?(?= )'
		
		String result="";
		String searchTerm="";
		
		if (signal == 1) searchTerm = "Signal level=";
		if (signal == 2) searchTerm = "Frequency:";
		
		utility.Logging.log(1, "Retrieving signal strength");
		
		Runtime r = Runtime.getRuntime();
		try{
			Process p = r.exec("iwconfig wlan0");
			p.waitFor();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";

			while ((line = buffer.readLine()) != null) {
				if (line.contains(searchTerm)) result=line;
			}

			if (signal == 1) result = result.substring(result.indexOf(searchTerm)+13,result.indexOf(searchTerm)+16);
			if (signal == 2) result = result.substring(result.indexOf(searchTerm)+10,result.indexOf(searchTerm)+15);
			
			buffer.close();
			
		}catch (Exception e) {
        	e.printStackTrace();
        	utility.Logging.log(2,  e.toString());
        }
		return result;
	} 

	/**
	   * This method is used to calculate the distance to the partner telemetry module 
	   * 
	   * @param signalLevelInDb Double precision value of the measured signal level in decibels
	   * @param freqInMHz Double precision value of the measured signal frequency
	   */
	protected Double distanceFromPartner(double signalLevelInDb, double freqInMHz) {
	    
		// distance (m) = 10 ^ ((27.55 - (20 * log10(frequency)) + signalLevel)/20)
		double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
	    
		return Math.pow(10.0, exp);    	    
	}
	
	
	/**
	   * This method is used to calculate the distance to the partner telemetry module 
	   * 
	   * @param signalLevel Double precision value of the measured signal level in decibels
	   * @param freqMhz Double precision value of the measured signal frequency
	   */
	protected String distanceFromPartner(String signalLevel, String freqMhz) {
		
		double signalLevelInDb = Double.parseDouble(signalLevel);
		double freqInMHz = Double.parseDouble(freqMhz)*1000;
		
	    return distanceFromPartner(signalLevelInDb, freqInMHz).toString();
	    
	}
}
