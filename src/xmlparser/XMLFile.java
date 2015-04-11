package xmlparser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;

/**
* <h1>XMLFile</h1>
* Generic XML file parser with element searching
*
* @author  Matt Thorne
* @version 1.0
* @since   2015-03-25
*/
public class XMLFile {
	
	private String filename;
	private File fXmlFile;
	private Document doc;
	private boolean fileread=false;
	
	/**
	 * Constructor.
	 * 
	 * @param filename qualified name of XML file to read
	 */
	public XMLFile(String filename){
		setFileName(filename);
	}
	
	/**
	 * Set the filename of the xml file to be parsed
	 * 
	 * @param filename qualified name of XML file to read
	 */
	public void setFileName(String filename){
		this.filename = filename;
		readFile();
	}
	
	/**
	 * Return the XML document root node
	 * @return string representation of the XML root node
	 */
	public String getRootNode(){
		return doc.getDocumentElement().getNodeName();
	}
	
	/**
	 * Get the value of the specified element node
	 * 
	 * @param element to be searched for
	 * @param node for element to be returned
	 * @return string value of the element node
	 */
	public String getElementNode(String element, String node){
		
		String result = "";
		
		NodeList nList = doc.getElementsByTagName(element);
		utility.Logging.log(1, "elements found for '" + element + "' = " + String.valueOf( nList.getLength()));
		
		try{
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				 
				Node nNode = nList.item(temp);		
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE){
					utility.Logging.log(1, "found element node '" + element + "'");
					Element eElement = (Element) nNode;
					result = eElement.getElementsByTagName(node).item(0).getTextContent();
					if (result==""){
						utility.Logging.log(1, "node '" + node + "' not found");
					}else{
						utility.Logging.log(1, "node '" + element + "." + node + "' = " + result);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			utility.Logging.log(2,  e.toString());
	    }
		
		if(result=="") utility.Logging.log(2,"node '" + node + "' not found");
		
		return result;
	}
	
	
	/**
	 * Is the document ready to be searched
	 * 
	 * @return <tt>true</tt> only if document is ready
	 */
	public boolean isReady(){
		return fileread;
	}
	
	/**
	 * Read in the XML file for parsing
	 * 
	 * @return <tt>true</tt> Only if successful
	 */
	private boolean readFile(){
		boolean result=true;
		try{
	
			fXmlFile = new File(this.filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			fileread=true;
			utility.Logging.log(1, "XML File '" + this.filename + "' loaded");
			utility.Logging.log(1, "root node is " + getRootNode());
			
		}catch (Exception e) {
			e.printStackTrace();
			utility.Logging.log(2,  e.toString());
			fileread=false;
			result=false;
	    }
		
		return result;
	}

}
