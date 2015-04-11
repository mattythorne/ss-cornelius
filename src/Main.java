
/**
* <h1>SS Cornelius</h1>
* The SS Cornelius program contains the code to
* run the satellite and docking software modules.
* This class contains only one method which is
* used as an object factory to build the software
* for the specified target.
* <p>
* <b>Note:</b> This is the competition entry for
* NASA's Space Apps Challenge 2015 - London Venue
*
* @author  Matt Thorne
* @version 2.1
* @since   2015-04-10
*/

public class Main {

	/**
	   * This method is used as the main object factory for the
	   * target it runs on. The target is either determined by
	   * the current user or via command line options.
	   * The target can be specified by supplying either <i>-cube</i> 
	   * or <i>-dock</i> followed by the XML configuration file for
	   * the target. 
	   * @param args The arguments supplied by the command line
	   */
	
	public static void main(String[] args) {
		
		// Three booleans to specify the target, one ring to rule them all!
		boolean blnCube=false;
		boolean blnDock=false;
		boolean blnTest=false;
		
		// Try and determine if this is the cube or the dock via the logged in username 
		if(System.getProperty("user.name").equals("cube")) blnCube=true;
		if(System.getProperty("user.name").equals("dock")) blnDock=true;
		if(System.getProperty("user.name").equals("matt")) blnTest=true;
		
		utility.Logging.log(1, "application launch " + utility.DateTime.getDate());
		
		// Use the command line arguments to determine if this is the cube or the docking module
		if((args.length==2)&(blnCube==false)&(blnDock==false)&(blnTest==false)){
			utility.Logging.log(1, "starting with " + args[0] + " " + args[1]);
			
			// Command line arguments for cube
			if(args[0].equals("-cube")) {
				blnCube=true;
				utility.Logging.log(1, "configuring as cube");
			}
			
			// Command line arguments for dock
			if(args[0].equals("-dock")) {
				blnDock=true;
				utility.Logging.log(1, "configuring as dock");
			}
		
		// Target could not be determined, display usage information
		}else{
			if (args.length==1) System.out.println("Error : You must specify an XML configuration file");
			System.out.println("Usage : java cornelius -cube config.xml");
			System.out.println("        java cornelius -dock config.xml");
		}
		
		
		// Configure the target for either a cube or dock
		if((blnCube)||(blnDock)){
			
			// Default XML file if none is specified
			String XMLFileName="config.xml";
			
			// If XML file is specified use that instead
			if(args.length==2){
				XMLFileName = args[1];
			}
			
			// This is the main execution object for the target system
			telemetry.TelemetryCore main = null;
			
			// If we're targetting a cube, create the appropriate objects
			if(blnCube) {
				utility.Logging.log(1, "configuring cube");
				main = new CubeTelemetry(XMLFileName);
			}
			
			// If we're targetting a dock, create the appropriate objects
			if(blnDock) {
				utility.Logging.log(1, "configuring dock");
				main = new DockTelemetry(XMLFileName);
			}
			
			// Set the software running
			main.run();
		}
		
		// Provision for a test system or supervisory system
		if(blnTest){
			utility.Logging.log(1, "configuring test");
			TestTelemetry main = new TestTelemetry("dist/test.xml");
			main.readConfigFile();
			
			// Set up a messenger to send telemetry commands from the CLI
			main.telemetryMessenger();
			
			// Test the LOS algorithm
			//main.testLOSRecognition("testimg/cube1.jpg", 30, true);
		
			// Test attitude adjustment algorithm
			//main.testAttitudeAdjustment("testimg/cube4t.jpg", "testimg/cube4b.jpg",true);
			
			
		}
		
	}
	

}
