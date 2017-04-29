package testLogParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.InputSource;

import org.xml.sax.SAXException;

public class XmlLogParser extends AbstractTestCaseParser {
	private static final String MessageElementName = "CDMMessage";
	
	public static final String RootElementName = "RootElement";
	
	public XmlLogParser(String fileName) throws SAXException, IOException
	{
		File tempXmlFile = null;
		FileReader fileReader = null;
		
		try{
			tempXmlFile = createXmlFile(fileName);
			
			fileReader = new FileReader(tempXmlFile);
			InputSource inputStream = new InputSource(fileReader);
			
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(new XmlLogHandler(this));
			
			xmlReader.parse(inputStream);
			
			fileReader.close();
			tempXmlFile.delete();
		}catch(SAXException error){
			if(fileReader != null)
				fileReader.close();
			if(tempXmlFile != null)
				tempXmlFile.delete();
			throw error;
		}
		catch(IOException error){
			if(fileReader != null)
				fileReader.close();
			if(tempXmlFile != null)
				tempXmlFile.delete();
			throw error;
		}
	}
	
	/** Creates temporary file with <i>RootElementName</i>, surrounding initial file.
	 * Eliminates unnecessary lines standing after <i>MessageElementName</i> closing tag.
	 * Returns created file.
	 * 
	 * @param fileName name of input file, which needs some adoption for XML format
	 * @return created file
	 * @throws IOException
	 */
	private File createXmlFile(String fileName) throws IOException
	{
		File inputFile = new File(fileName);
		File outputFile = File.createTempFile(inputFile.getName(), ".xml");

		FileOutputStream outputStream = null;
		BufferedReader inputStream = new BufferedReader((Reader)new FileReader(fileName));
		try{
			outputStream = new FileOutputStream(outputFile);
			
			//Print Root element at the head of XML file
			outputStream.write(("<" + RootElementName + ">\n").getBytes());
			String readLine = inputStream.readLine();

			//Print all Xml Message elements
			while(readLine != null)
				readLine = printXmlMessage(inputStream, outputStream, readLine);
							
			//Print closing Root element at the bottom of XML file
			outputStream.write(("</" + RootElementName + ">").getBytes());
			outputStream.close();
		}catch (IOException error){
			inputStream.close();
			if(outputStream != null)
				outputStream.close();
			if(outputFile != null)
				outputFile.delete();
			throw error;
		}
				
		return outputFile;
	}
	
	/** Prints to the <b>outputStream</b> found Xml Message.
	 *	Returns next line from <b>inputStream</b> (after Xml Message).
	 * 
	 * @param inputStream lines are read from this stream
	 * @param outputStream Xml Message is written to this stream 
	 * @param readLine initial line read from inputStream
	 * @return next line read from <b>outputStream</b>
	 * @throws IOException
	 */
	private String printXmlMessage(BufferedReader inputStream, FileOutputStream outputStream,
									String readLine) throws IOException{
		final String startMsgEl_str = "^.*?(<(\\w+:)?" + MessageElementName + ".*?>)(.*)$";
		final Pattern startMsgEl_ptrn = Pattern.compile(startMsgEl_str);
		final String endMsgEl_str = "^(.*?)(</(\\w+:)?" + MessageElementName + ">)(.*)$";
		final Pattern endMsgEl_ptrn = Pattern.compile(endMsgEl_str);

		Matcher matcher = startMsgEl_ptrn.matcher(readLine);
		if(matcher.find()){						//Found beginning of XML Message
			//Print start element, skipping garbage before it
			outputStream.write((matcher.group(1) + "\n").getBytes());

			readLine = matcher.group(3);				//Get rest of line
			if(readLine.equals(""))						//If nothing left
				readLine = inputStream.readLine();		//Go to the next line

			//Find End Message Element
			while(readLine != null){
				matcher = endMsgEl_ptrn.matcher(readLine);
				if(matcher.find()){				//Found end of XML Message
					//Print end element, with everything before it
					outputStream.write((matcher.group(1) + 
										matcher.group(2) + "\n").getBytes());

					readLine = matcher.group(4);				//Get rest of line
					if(readLine.equals(""))							//If nothing left
						readLine = inputStream.readLine();		//Go to the next line
					
					return readLine;
				}else
					outputStream.write((readLine + "\n").getBytes());
				readLine = inputStream.readLine();
			} //End of "Find End Message Element 
		} //End of "Found beginning of XML Message
		
		return inputStream.readLine();		//Go to the next line
	}
}
