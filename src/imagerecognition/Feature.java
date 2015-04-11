package imagerecognition;

/**
* <h1>Feature</h1>
* This describes a found feature in an image recognition.
*
* @author  Matt Thorne
* @version 1.0
* @since   2015-03-25
*/


public class Feature {
	private int oriX = 0;
	private int oriY = 0;
	private int size = 0;
	
	/**
	 * Sets the x and y origin of a feature where x and y 
	 * represent the top left corner of the recognition rectangle
	 * 
	 * @param x the horizontal origin of the feature
	 * @param y the vertical origin of the feature
	 */
	public void setOrigin(int x, int y){
		oriX = x;
		oriY = y;
	}

	/**
	 * @return the x origin of a feature
	 */
	public int getOriX() {
		return oriX;
	}

	/**
	 * @param oriX the x origin to set
	 */
	public void setOriX(int oriX) {
		this.oriX = oriX;
	}

	/**
	 * @return the y origin of a feature
	 */
	public int getOriY() {
		return oriY;
	}

	/**
	 * @param oriY the y origin to set
	 */
	public void setOriY(int oriY) {
		this.oriY = oriY;
	}

	/**
	 * @return the size of the feature
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size of the feature to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	
	
}
