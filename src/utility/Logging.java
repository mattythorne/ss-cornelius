package utility;

/**
* <h1>Logging</h1>
* Class to handle logging of modal system messages
*
* @author  Matt Thorne
* @version 1.0
* @since   2015-03-25
*/
public class Logging {
	
	
	/**
	 * Enter a message in the log
	 * 
	 * @param messageClass class of the message to log 
	 * @param message message to be logged
	 */
	public static void log(int messageClass, String message){
		System.out.println(utility.DateTime.getTime() + getClass(messageClass) + message);
	}

	
	/**
	 * Return a string representation of a message class
	 * 
	 * @param messageClass class to be decoded
	 * @return string representation of the class
	 */
	private static String getClass(int messageClass){
		String result="";
		
		switch (messageClass){
		case 0: result = " : ERROR     : ";
				break;
		case 1: result = " : INFO      : ";
				break;
		case 2: result = " : T_SEND <- : ";
				break;
		case 3: result = " : T_REPL -> : ";
				break;
		case 4: result = " : T_RECV -> : ";
				break;
		case 5: result = " : T_REPL <- : ";
				break;
		}
		
		return result;
	}
}
