import imagerecognition.ImageRecognition;
import telemetry.TelemetryCore;

/**
* <h1>DockTelemetry</h1>
* This is the core dock telemetry class.
* It provide methods for reading in a telemetry XML
* configuration file and setting up a client and server.
* It has methods specific to the satellite dock
* It provides an interface to the host probe
*
* @author  Matt Thorne
* @version 1.0
* @since   2015-04-10
*/
public class DockTelemetry extends TelemetryCore {

	private ImageRecognition image = new ImageRecognition();
	
	/**
	  * Constructor.
	  * 
	  * @param XMLFileName (required) Quailified filename of XML file for telemetry settings
	  */
	public DockTelemetry(String XMLFileName) {
		super(XMLFileName);
	}
	
	/**
	 * Command received from host to launch satellite
	 */
	public void satelliteLaunch(){
		
		// Distance in metres to launch satellite to
		double DIST = 100.0;
		
		// Line of sight boundary in percent
		int LOSBOUNDARY = 30;
		
		// Variable for tracking current distance
		double currentDistance = 0.0;
		
		// Variable for parsing telemetry response
		String response = null;
		
		// Activate the LOS beacon
		sendTelemetry("BLED ON");
		
		while(currentDistance<DIST){
			
			// PSEUDO: Launch actuator out
			
			// Get satellites distance from dock
			response = sendTelemetry("DISTANCE?");
			currentDistance = Double.parseDouble(response);
			
			// PSEUDO: Capture image from dock camera
			
			// Is the satellite drifting out of line of sight?
			if(image.isLOSFeatureInBoundary(LOSBOUNDARY)==false){
				// Apply the satellite "brake"
				sendTelemetry("BRAKE");
				break;
				
				// ToDo : add some contingency checks
			}
		}
	}
		
		
		/**
		 * Command received from host to take photo
		 */
		public void takePhoto(){
			// Take a photo from the satellite
			sendTelemetry("TAKEPHOTO");
			
			// PSEUDO: Transfer photo from satellite
		}
		
		/**
		 * Command received from host to take video
		 */
		public void takeVideo(){
			
			// Video length in seconds
			int VIDLEN = 10;
			
			// Take a video from the satellite
			sendTelemetry("TAKEVIDEO " + VIDLEN);
			
			// PSEUDO: Transfer video from satellite
		}
		
		/**
		 * Command from host to recall satellite
		 */
		public void recallSatellite(){
			
			// Distance in metres to bring satellite to
			double DIST = 1.0;
			
			// Variable for tracking current distance
			double currentDistance = 0.0;
			
			// PSUEDO : Satellite is docked
			boolean docked = false;
			
			// Variable for parsing telemetry response
			String response = null;
			
			// Start propulsion
			sendTelemetry("PROPULSION ON");
			
			while(currentDistance>DIST){
				// Get satellites distance from dock
				response = sendTelemetry("DISTANCE?");
				currentDistance = Double.parseDouble(response);
			}
			
			// Stop propulsion
			sendTelemetry("PROPULSION ON");
			
			// Deactivate LOS Beacon
			sendTelemetry("BLED OFF");
			
			while(docked==false){
				
				// Activate top attitude beacon
				sendTelemetry("GLED ON");
				// PSEUDO: Capture image from dock camera
				// Deactivate top attitude beacon
				sendTelemetry("GLED OFF");
				
				// Activate bottom attitude beacon
				sendTelemetry("RLED ON");
				// PSEUDO: Capture image from dock camera
				// Deactivate bottom attitude beacon
				sendTelemetry("RLED OFF");
			
				// get the attitude adjustment needed and send it to the satellite
				sendTelemetry("ATTADJ" + image.getDockAdjustment());
				
			}
			
		}
		

}
