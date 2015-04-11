package imagerecognition;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

import com.jhlabs.image.*;

/**
* <h1>ImageRecognition</h1>
* This class provides the image recognition algorithms needed
* to recognise features of the satellite from the imaging sensor on the dock
* 
* @author  Matt Thorne
* @version 2.1
* @since   2015-04-08
*/
public class ImageRecognition {
	
	private BufferedImage image = null;
	private BufferedImage transformedImage = null;
	private ContrastFilter contrastFilter = new ContrastFilter();
	
	/**
	 * Apply contrast to the instance image
	 * 
	 * @param contrast level of contrast to apply
	 */
	public void setContrast(int contrast){	
		
		contrastFilter.setContrast(contrast);
		transformedImage = deepCopy(image);
		contrastFilter.filter(image, transformedImage);		
	}
	
	
	/**
	 * Algorithm to find the blue LOS beacon from the 
	 * instance image
	 */
	private Feature searchForLOSFeature(){
		Color pixelColour = new Color(0);
		int red = 0;
		int red2 = 0;
		int green = 0;
		int blue = 0;
		int foundRed = 0;
		Feature feature = null;
		
		setContrast(-10);
		
		for(int x=0; x<transformedImage.getWidth(); x++){
			for(int y=0; y<transformedImage.getHeight(); y++){	
				pixelColour = new Color(transformedImage.getRGB(x, y));
				red = pixelColour.getRed();
				green = pixelColour.getGreen();
				blue = pixelColour.getBlue();
				if ((red>foundRed)&(green==0)&(blue==0)){
					pixelColour = new Color(transformedImage.getRGB(x+20, y));
					red2 = pixelColour.getRed();
					green = pixelColour.getGreen();
					blue = pixelColour.getBlue();
					if ((red2==0)&(green==0)&(blue==0)){
						feature = new Feature();
						feature.setOrigin(x,y);
						foundRed = red;
					}
				}
			}
		}
		
		
		return feature;
	}
	
	/**
	 * Algorithm to find the top green dock alignment beacon from the 
	 * instance image
	 */
	private Feature searchForTopAttitudeFeature(){
		Color pixelColour = new Color(0);
		int red = 0;
		int blue2 = 0;
		int green = 0;
		int blue = 0;
		int foundBlue = 0;
		Feature feature = null;
		
		setContrast(-10);
		
		for(int x=0; x<transformedImage.getWidth(); x++){
			for(int y=0; y<transformedImage.getHeight(); y++){	
				pixelColour = new Color(transformedImage.getRGB(x, y));
				red = pixelColour.getRed();
				green = pixelColour.getGreen();
				blue = pixelColour.getBlue();
				
				if ((blue>foundBlue)&(green==0)&(red==0)){
					pixelColour = new Color(transformedImage.getRGB(x+20, y));
					red = pixelColour.getRed();
					green = pixelColour.getGreen();
					blue2 = pixelColour.getBlue();
					if ((blue2==0)&(green==0)&(red==0)){
						feature = new Feature();
						feature.setOrigin(x,y);
						foundBlue = blue;
					}
				}
			}
		}
		
		return feature;
	}
	
	/**
	 * Algorithm to find the Bottom red dock alignment beacon from the 
	 * instance image
	 */
	private Feature searchForBottomAttitudeFeature(){
		Color pixelColour = new Color(0);
		int red = 0;
		int green2 = 0;
		int green = 0;
		int blue = 0;
		int foundGreen = 0;
		Feature feature = null;
		
		setContrast(-10);
		
		for(int x=0; x<transformedImage.getWidth(); x++){
			for(int y=0; y<transformedImage.getHeight(); y++){	
				pixelColour = new Color(transformedImage.getRGB(x, y));
				red = pixelColour.getRed();
				green = pixelColour.getGreen();
				blue = pixelColour.getBlue();
				
				if ((green>foundGreen)&(blue==0)&(red==0)){
					pixelColour = new Color(transformedImage.getRGB(x+20, y));
					red = pixelColour.getRed();
					green2 = pixelColour.getGreen();
					blue = pixelColour.getBlue();
					if ((blue==0)&(green2==0)&(red==0)){
						feature = new Feature();
						feature.setOrigin(x,y);
						foundGreen = green;
					}
				}
			}
		}
		
		return feature;
	}
	
	/**
	 * Return the top attitude feature of the docking plate
	 * 
	 * @return Feature representing the origin and size of the detected feature
	 */
	public Feature getTopAttitudeFeature(){
		Feature result = null;
		Feature feature = searchForTopAttitudeFeature();
		utility.Logging.log(1, "Top Found at " + feature.getOriX() + " " + feature.getOriY());
		
		feature.setSize(80);
		feature.setOriX(feature.getOriX() -30);
		feature.setOriY(feature.getOriY() -45);
		
		result = feature;
		return result;
	}
	
	/**
	 * Return the bottom attitude feature of the docking plate
	 * 
	 * @return Feature representing the origin and size of the detected feature
	 */
	public Feature getBottomAttitudeFeature(){
		Feature result = null;
		Feature feature = searchForBottomAttitudeFeature();
		utility.Logging.log(1, "Bottom Found at " + feature.getOriX() + " " + feature.getOriY());
		
		feature.setSize(80);
		feature.setOriX(feature.getOriX() -30);
		feature.setOriY(feature.getOriY() -45);
		
		result = feature;
		return result;
	}
	
	/**
	 * Get the adjustment required for the docking face based on the instance image
	 * 
	 * @return Double representing the offset in degrees required to align the docking plates 
	 */
	public double getDockAdjustment(){
		double result=0;
		
		Feature topFeature = getTopAttitudeFeature();
		Feature bottomFeature = getBottomAttitudeFeature();
		
		result = calculateOffset(topFeature, bottomFeature);
		
		drawFeature(topFeature);
		drawFeature(bottomFeature);
		
		utility.Logging.log(1, "Offset required " + result + " degrees");
		
		return result;
	}
	
	/**
	 * Get the adjustment required for the docking face based on two specified images
	 * 
	 * @param topImage qualified filename of the image containing the top alignment beacon
	 * @param bottomImage qualified filename of the image containing the bottom alignment beacon
	 * @return Double representing the offset in degrees required to align the docking plates
	 */
	public double getDockAdjustment(String topImage, String bottomImage){
		double result=0;
		
		loadImage(topImage);
		Feature topFeature = getTopAttitudeFeature();
		
		loadImage(bottomImage);
		Feature bottomFeature = getTopAttitudeFeature();
		
		result = calculateOffset(topFeature, bottomFeature);
		
		drawFeature(topFeature);
		drawFeature(bottomFeature);
		
		utility.Logging.log(1, "Offset requied " + result + " degrees");
		
		return result;
	}
	
	/**
	 * Calculate the angular difference between two features in degrees
	 * 
	 * @param topFeature
	 * @param bottomFeature
	 * @return Double representing the difference in degrees between the specified feature origins
	 */
	public double calculateOffset(Feature topFeature, Feature bottomFeature){
		double result = 0.0;
		
		double deltaX = 0.0;
		double deltaY = 0.0;

		//Calculate the offset between the two detected points
		deltaY = (double)topFeature.getOriY() - (double)bottomFeature.getOriY();
		deltaX = (double)topFeature.getOriX() - (double)bottomFeature.getOriX();
		result = Math.toDegrees(Math.atan2(deltaY,deltaX));
		
		return result;
	}
	
	
	/**
	 * Check if the satellite is still within boundary
	 * 
	 * @param boundaryPercentage dangerzone boundary as a percentage of overall image
	 * @return <tt>true</tt> if satellite is within boundary <tt>false</tt> if it is lost or out of bounds
	 */
	public boolean isLOSFeatureInBoundary(int boundaryPercentage){
		boolean result=true;
		Feature feature = searchForLOSFeature();
		
		if(feature != null){
			// Satellite in view
			utility.Logging.log(1, "Found at " + feature.getOriX() + " " + feature.getOriY());
			
			
				feature.setSize(100);
				feature.setOriX(feature.getOriX() -25);
				feature.setOriY(feature.getOriY() -50);
				drawFeature(feature);
			
			// Check if feature is outside boundary
			if (checkBoundary(feature, boundaryPercentage)==false){
				utility.Logging.log(1, "Boundary breached " + feature.getOriX() + " " + feature.getOriY()); 
				drawBoundary(boundaryPercentage);
				result=false;
			}
		}else{
			// Satellite is lost
			result=false;
		}
		return result;
	}
	
	/**
	 * Check if the satellite has been lost
	 * 
	 * @return <tt>true</tt> if satellite is lost <tt>false</tt> if it is still in LOS
	 */
	public boolean isSatelliteLost(){
		boolean result=true;
		Feature feature = searchForLOSFeature();
		
		if(feature != null){
			// Satellite is not lost
			result=false;
		}
		return result;
	}
	
	/**
	 * Draw the feature marker on the image
	 * 
	 * @param feature the feature to outline on the image
	 */
	private void drawFeature(Feature feature) {
		
		int y = feature.getOriY();
		int x = feature.getOriX();
		Color col = new Color(0,255,0);
		
		for(int n=0;n<4;n++){
			for(x=feature.getOriX()+n; x<feature.getOriX()+feature.getSize()+n; x++) image.setRGB(x, y, col.getRGB());
			for(y=feature.getOriY()+n; y<feature.getOriY()+feature.getSize()+n; y++) image.setRGB(x, y, col.getRGB());
			for(x=feature.getOriX()+n + feature.getSize(); x>feature.getOriX()+n; x--) image.setRGB(x, y, col.getRGB());
			for(y=feature.getOriY()+n + feature.getSize(); y>feature.getOriY()+n; y--) image.setRGB(x, y, col.getRGB());
		}
		
	}
	
	/**
	 * Draw the dangerzone boundary marker onto the image
	 * 
	 * @param boundaryPC dangerzone boundary as a percentage of overall image
	 */
	private void drawBoundary(int boundaryPC) {
		
		int oy = (image.getHeight()/100)*boundaryPC;
		int oy2 = (image.getHeight()-(image.getHeight()/100)*boundaryPC);
		int ox = (image.getWidth()/100)*boundaryPC;
		int ox2 = (image.getWidth()-(image.getWidth()/100)*boundaryPC);
		 int x = 0;
		 int y = 0;
		 
		Color col = new Color(255,0,0);
		
		for(int n=0;n<4;n++){
			for(x=ox+n; x<ox2+n; x++) image.setRGB(x, y, col.getRGB());
			for(y=oy+n; y<oy2+n; y++) image.setRGB(x, y, col.getRGB());
			for(x=ox2+n; x>ox+n; x--) image.setRGB(x, y, col.getRGB());
			for(y=oy2+n; y>oy+n; y--) image.setRGB(x, y, col.getRGB());
		}
		
	}

	/**
	 * Load the specified image to use for image recognition.
	 * This method is provided for convenience and simulation purposes
	 * 
	 * @param filename qualified image filename
	 */
	public void loadImage(String filename){
		try 
		{
			File imageFile = new File(filename);
		    image = ImageIO.read(imageFile);
			
		    if(image !=null) utility.Logging.log(1, "Image loaded : " + image.getWidth() + "," + image.getHeight()); 
		}catch (Exception e) {
			e.printStackTrace();
			utility.Logging.log(0,  e.toString());
		}
	}
	
	/**
	 * Save the instance image to a file.
	 * Useful for storing the result of an algorithm for development purposes
	 * 
	 * @param filename qualified image filename
	 */
	public void saveImage(String filename){
		try 
		{
			ImageIO.write(image, "jpg", new File(filename));   
		}catch (Exception e) {
			e.printStackTrace();
			utility.Logging.log(0,  e.toString());
		}
	}
	
	/**
	 * Save the intermediate working instance image to a file.
	 * Useful for storing the results of interim processing actions 
	 * for development purposes
	 * 
	 * @param filename qualified image filename
	 */
	public void saveTransformedImage(String filename){
		try 
		{
			ImageIO.write(transformedImage, "jpg", new File(filename));   
		}catch (Exception e) {
			e.printStackTrace();
			utility.Logging.log(0,  e.toString());
		}
	}
	
	/**
	 * Provide an exact copy of a buffered image
	 * 
	 * @param bi buffered image to copy
	 * @return an exact copy of the specified buffered image
	 */
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	/**
	 * Check if the detected feature is in the dangerzone
	 * 
	 * @param feature feature to be boundary checked
	 * @param boundaryPC dangerzone boundary as a percentage of overall image
	 * @return <tt>true</tt> feature is in the safe zone <tt>false</tt> feature is in the dangerzone
	 */
	private boolean checkBoundary(Feature feature, int boundaryPC){
		boolean result=true;
			if(feature.getOriX() < ((image.getWidth()/100)*boundaryPC)) result=false;
			if(feature.getOriX() > (image.getWidth()-(image.getWidth()/100)*boundaryPC)) result=false;
			if(feature.getOriY() < ((image.getHeight()/100)*boundaryPC)) result=false;
			if(feature.getOriY() > (image.getHeight()-(image.getHeight()/100)*boundaryPC)) result=false;
		return result;
	}
	
}
