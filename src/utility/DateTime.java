package utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* <h1>DateTime</h1>
* Utility class for managing date and time representations.
*
* @author  Matt Thorne
* @version 1.0
* @since   2015-03-25
*/
public class DateTime {
		
	/**
	 * @return current time as a string with millisecond accuracy hh:mm:ss:ms
	 */
	public static String getTime(){
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
		Date date = new Date();
		
		return dateFormat.format(date);
	}
	
	/**
	 * @return current date as a string formatted as dd/mm/yyyy
	 */
	public static String getDate(){
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		
		return dateFormat.format(date);
	}
}
