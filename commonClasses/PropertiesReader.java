/** Package containing common classes of the TestLogLib Library 
 */
package commonClasses;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Class responsible for reading referenced configuration file and creates HashMap with values of corresponding properties<br>
 * Configuration file should contain records in the form <b>paramName = paramValue</b>.
 * Characters prefixed with '#' are skipped.
 * 
 * @author AKosarev
 * 
 */
public class PropertiesReader {

	private HashMap<String, String> properties = new HashMap<String, String>();

	/** Constructor creating internal HashMap with parameter-values
	 *
	 * @param fileName file path to configuration
	 * @throws IOException
	 * @throws IncorrectPropertyConfig
	 */
	public PropertiesReader(String fileName) throws IOException, IncorrectPropertyConfig{
		BufferedReader inputStream = new BufferedReader((Reader)new FileReader(fileName));

		Pattern paramCommentPattern = Pattern.compile("^(.*?)#.*$");	
		try{
			String readLine = inputStream.readLine();

			//Read all properties lines
			while(readLine != null){
					
				String paramValue;
				Matcher matcher = paramCommentPattern.matcher(readLine);
				if(matcher.find())
					paramValue = matcher.group(1);
				else
					paramValue = readLine; 

				if(paramValue == null || paramValue.equals("")){			//Skip commented lines
					readLine = inputStream.readLine();
					continue;
				}

				Pattern paramValuePattern = Pattern.compile("^(.*?)=(.*)$");	
				matcher = paramValuePattern.matcher(paramValue);
				if(!matcher.find() || matcher.groupCount() < 2)
					throw new IncorrectPropertyConfig("Error while reading parameter & value on the line: " + readLine);
					
				String parameter = matcher.group(1).trim(); String value = matcher.group(2).trim();
				properties.put(parameter, value);

				readLine = inputStream.readLine();
			}
		}catch (IOException error){
			inputStream.close();
			throw error;
		} catch (IncorrectPropertyConfig error){
			inputStream.close();
			throw error;
		}
	}

	/** Returns parameter Value
	 *
	 * @param paramName name of searched parameter
	 */
	public String GetParamValue(String paramName){return properties.get(paramName);}

	/** Adds parameter Value 
	 *
	 * @param paramName name of added parameter
	 * @param paramValue value of added parameter
	 */
	protected void AddParam(String paramName, String paramValue){properties.put(paramName, paramValue);}

}
