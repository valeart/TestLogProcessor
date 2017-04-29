package jmsCommunication;

import java.io.IOException;

import commonClasses.IncorrectPropertyConfig;
import commonClasses.PropertiesReader;

/** Class extending reading of JMS configuration with additional checks
 *
 * @author AKosarev
 *
 */
public class JmsConfigReader extends PropertiesReader {
	
	/** Constructor extending Properties Reader constructor with necessary checks
	 *
	 * @param fileName file path to configuration
	 * @throws IOException
	 * @throws IncorrectPropertyConfig
	 */
	public JmsConfigReader(String fileName) throws IOException, IncorrectPropertyConfig, IncorrectJmsConfig {
		super(fileName);
		
		//Check some necessary assumptions about JMS configuration
		if(GetParamValue("jmsServerURL") == null || GetParamValue("queueName") == null)
			throw new IncorrectJmsConfig("Missing necessary parameters (either \"jmsServerURL\" or \"queueName\")");
		
		if(GetParamValue("repeat") == null)
			AddParam("repeat", "1");
		
		if(GetParamValue("userName") != null && GetParamValue("password") == null)
			throw new IncorrectJmsConfig("If \"userName\" parameter is defined, \"password\" parameter should also be present");
		
	}

}
