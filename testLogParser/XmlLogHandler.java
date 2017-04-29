package testLogParser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import testLogParser.Errors.IncorrectTestParameter;

/** Class implementing logic of SAX parsing.
 * Specific methods (startDocument, startElement and etc) are called while parsing XML.
 * At then end (endDocument method), object attaches collected testCases to the provide parser.
 *
 * @author AKosarev
 *
 */
final class XmlLogHandler extends DefaultHandler{
	private ArrayList<TestCaseType> testCases;
	private XmlLogParser parser;					//Object performing parsing
	
	//TestCase & Test case parameter type that are processing by current XML parser
	private TestCaseType currTestCase;
	private TestParamType currParam;
	private String currParamName;
	
	public XmlLogHandler(XmlLogParser aParser){
		parser = aParser;
	}
	
	@Override
	public void startDocument() throws SAXException
	{
		testCases = new ArrayList<TestCaseType>();		//initialize testCases
		
		currTestCase = null;
		currParam = null;
	}
	
	@Override
	public void startElement(String namespaceURI, String localName, 
							 String qName, Attributes attr) throws SAXException
	{
		try{
			if(currTestCase == null){					//We haven't started parsing TestCase
				if(qName != XmlLogParser.RootElementName)		//Skip root element
					currTestCase = new TestCaseType(qName);
		
			}else{										//Currently parsing TestCase
				if(currParam != null)
					return;								//Skip parameters which are 2 levels depth

				String value = attr.getValue("value");
				if(value == null) value = "";					//Protection from name="" attribute
				currParamName = qName;
				currParam = new TestParamType(value);
			
				//load all attributes of Parameter type 
				for(int i = 0; i < attr.getLength(); i++){
					String attrName = attr.getQName(i);
					if(attrName == "value")			//value attr is used as value for HashMap
						continue;
					else if(attrName == "typ")		//resolve duplicates for parameter names  
						currParamName += "_" + attrName + ":" + attr.getValue(i);
					else
						currParam.addAttribute(attr.getQName(i), attr.getValue(i));
				}
			}
		}catch(IncorrectTestParameter error){
			throw new SAXException(error);				//map exception into SAXException
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName,
            			   String qName ) throws SAXException
    {
		try{
			if(currParam != null){						//Parsing parameters...
				String pattern_str = "^" + qName + "(_.+)*$";
				Pattern pattern = Pattern.compile(pattern_str);
				Matcher matcher = pattern.matcher(currParamName);
				if(!matcher.find()) return;				//Skip not parsed parameters
				
				currTestCase.addTestParam(currParamName, currParam);	//Closing currently parsed parameter
				currParam = null;					
			}else{										//Parsed all parameters
				if(currTestCase == null || currTestCase.getName() != qName)		
					return;								//Skip not parsed elements
					
				testCases.add(currTestCase);
				currTestCase = null;					//Closing currently parsed TestCase
			}
		}catch(IncorrectTestParameter error){
			throw new SAXException(error);
		}
    }

	@Override
	public void endDocument() throws SAXException
	{
		parser.setTestCases(testCases);	//pass created testCases to parsing object
	}
	
}