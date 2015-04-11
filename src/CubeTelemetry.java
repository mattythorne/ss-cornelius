import telemetry.TelemetryCore;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
* <h1>CubeTelemetry</h1>
* This is the core cube telemetry class.
* It provide methods for reading in a telemetry XML
* configuration file and setting up a client and server.
* It has methods specific to the satellite cube
*
* @author  Matt Thorne
* @version 1.0
* @since   2015-03-25
*/
public class CubeTelemetry extends TelemetryCore {

	// Variables required for controller GPIO
	private GpioController gpio = null;
	private GpioPinDigitalOutput blueLED = null;
	private GpioPinDigitalOutput redLED = null;
	private GpioPinDigitalOutput greenLED = null;
	
	/**
	  * Constructor.
	  * 
	  * @param XMLFileName (required) Quailified filename of XML file for telemetry settings
	  */
	public CubeTelemetry(String XMLFileName) {
		super(XMLFileName);
	}
	
	/**
	 * Set up the preTelemetry configuration of the cube sat.
	 * Calls the GPIO configuration
	 * 
	 * @return <tt>true</tt> Only if successful
	 */
	protected boolean preTelemetryConfiguration(){
		boolean result = true;
		configureGPIO();
		return result;
	}
	
	/**
	 * Method to handle cube telemetry messages.
	 * Message is parsed and the appropriate action taken
	 * 
	 * @param recievedMessage Message recieved from telemetry partner
	 * @return the response to be sent to the partner
	 */
	protected String telemetryAction(String recievedMessage){
		String result = "UNKNOWN COMMAND\n";
		
		if(recievedMessage!=null) result = "UNKNOWN - " + recievedMessage + '\n';
		
		if(recievedMessage.equals("BLED ON")){
			blueLED.high();
			result = "Blue LED on" + '\n';
		}
		
		if(recievedMessage.equals("BLED OFF")){
			blueLED.low();
			result = "Blue LED off" + '\n';
		}
		
		if(recievedMessage.equals("RLED ON")){
			redLED.high();
			result = "Red LED on" + '\n';
		}
		
		if(recievedMessage.equals("RLED OFF")){
			redLED.low();
			result = "Red LED off" + '\n';
		}
		
		if(recievedMessage.equals("GLED ON")){
			greenLED.high();
			result = "Green LED on" + '\n';
		}
		
		if(recievedMessage.equals("GLED OFF")){
			greenLED.low();
			result = "Green LED off" + '\n';
		}
		
		if(recievedMessage.equals("BRAKE")){
			// PSEUDO: Apply propulsive brake
			result = "Brake Applied - Motion stopped" + '\n';
		}
		
		if(recievedMessage.equals("PROPULSION ON")){
			// PSEUDO: TURN PROPULSION ON
			result = "Propulsion started" + '\n';
		}
		
		if(recievedMessage.equals("PROPULSION OFF")){
			// PSEUDO: TURN PROPULSION OFF
			result = "Propulsion stopped" + '\n';
		}
		
		if(recievedMessage.startsWith("ATTADJ")){
			// PSEUDO: Adjust attitude
			result = "Attitude adjusted";
		}
		
		if(recievedMessage.startsWith("PANIC!")){
			// PSEUDO: Emergency recovery procedure
			result = "PANICING!";
		}
		
		if(recievedMessage.equals("TAKEPHOTO")){
			Runtime r = Runtime.getRuntime();
			try{
				r.exec("raspistill -o image.jpg");
				result = "Still Capture Complete" + '\n';
			}catch (Exception e) {
	        	e.printStackTrace();
	        	utility.Logging.log(2,  e.toString());
	        }	
		}
		
		if(recievedMessage.startsWith("TAKEVIDEO")){
			Runtime r = Runtime.getRuntime();
			if(recievedMessage.length()>10){
				try{
					r.exec("raspivid -o video.h264 -t " + recievedMessage.substring(10));
					result = "Video Capture Complete" + '\n';
				}catch (Exception e) {
					e.printStackTrace();
					utility.Logging.log(2,  e.toString());
				}
			}else{
				result = "No Video Length Supplied" + '\n';
				utility.Logging.log(2, "Bad Command no Video Length Supplied");
			}
		}
		
		if(recievedMessage.equals("SIGNALLEVEL?")){
			result = getSignalData(1);
		}
		
		if(recievedMessage.equals("SIGNALFREQ?")){
			result = getSignalData(2);
		}
		
		if(recievedMessage.equals("DISTANCE?")){
			result = distanceFromPartner(super.getSignalData(LEVEL), super.getSignalData(FREQ));
		}
		
		return result;
	}

	/**
	 * Set up the GPIO of the raspberry pi for the satellite
	 */
	private void configureGPIO(){
		utility.Logging.log(1, "configuring cube IO");
		gpio = GpioFactory.getInstance();
		blueLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, "blueLED", PinState.LOW);
		redLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "redLED", PinState.LOW);
		greenLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09, "greenLED", PinState.LOW);
	}
	
}
