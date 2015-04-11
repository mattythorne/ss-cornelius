import imagerecognition.ImageRecognition;

import java.io.*;

import telemetry.TelemetryCore;

/**
 * 
 */

/**
 * @author matt
 *
 */
public class TestTelemetry extends TelemetryCore {

	private ImageRecognition image = new ImageRecognition();
	
	/**
	 * Constructor
	 * 
	 * @param XMLFileName (required) Quailified filename of XML file for telemetry settings
	 */
	public TestTelemetry(String XMLFileName) {
		super(XMLFileName);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Set up a telemtry tester which sends commands to the partner
	 * as entered at the CLI
	 */
	public void telemetryMessenger(){
		while(true){
			System.out.println("Telemetry : ");
		 
			try{
				BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
				String s = bufferRead.readLine();
	 
				sendTelemetry(s);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Test the line of sight image recognition algorithm on a static file
	 * 
	 * @param imageFilename filename of the image to test the algorithm on
	 * @param boundaryPercentage dangerzone boundary as a percentage of overall image
	 * @param saveImages <tt>true</tt> to save intermediate and result images
	 */
	public void testLOSRecognition(String imageFilename, int boundaryPercentage, boolean saveImages){
		image.loadImage(imageFilename);
		image.isLOSFeatureInBoundary(boundaryPercentage);
		
		if(saveImages){
			image.saveTransformedImage(imageFilename.substring(0, imageFilename.length()-4)+"T.jpg");
			image.saveImage(imageFilename.substring(0, imageFilename.length()-4)+"R.jpg");
		}
		
	}
	
	/**
	 * Test the attitude adjustment image recognition algorithm on two static files
	 * 
	 * @param topImage qualified filename of the image containing the top alignment beacon
	 * @param bottomImage qualified filename of the image containing the bottom alignment beacon
	 * @param saveImages <tt>true</tt> to save intermediate and result images
	 */
	public void testAttitudeAdjustment(String topImage, String bottomImage, boolean saveImages){
		
		if(saveImages){
			image.loadImage(topImage);
			image.setContrast(-10);
			image.saveTransformedImage(topImage.substring(0, topImage.length()-4)+"T.jpg");
			
			image.loadImage(bottomImage);
			image.setContrast(-10);
			image.saveTransformedImage(bottomImage.substring(0, bottomImage.length()-4)+"T.jpg");
		}
		
		image.getDockAdjustment(topImage, bottomImage);
		if(saveImages)image.saveImage(topImage.substring(0, topImage.length()-4)+"R.jpg");
	}
	
}
