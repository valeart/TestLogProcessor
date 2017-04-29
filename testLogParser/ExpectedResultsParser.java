package testLogParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import testLogParser.Errors.IncorrectTestConfig;
import testLogParser.Errors.IncorrectTestParameter;

/**Class used for parsing File with Test cases containing expected results.
 * 
 * @author AKosarev
 *
 */
public class ExpectedResultsParser extends AbstractTestCaseParser {
	
	public ExpectedResultsParser(String inputFileName)
		throws IOException
	{
		BufferedReader inputStream = new BufferedReader((Reader)new FileReader(inputFileName));
		try {
			ArrayList<TestCaseType> testCases = new ArrayList<TestCaseType>();
			TestCaseType testCase = readTestCase(inputStream);
			while(testCase != null){
				testCases.add(testCase);
				testCase = readTestCase(inputStream);
			}
			
			super.setTestCases(testCases);						//Set read testCases
			inputStream.close();
		} catch (IOException error) {
			inputStream.close();
			throw error;
		}
	}
	
	private TestCaseType readTestCase(BufferedReader inputStream)
		throws IOException
	{
		String readLine = inputStream.readLine();
		while(isEmptyLine(readLine)){								//If EOF or empty line
			if(readLine == null)									//Reached end of file
				return null;	
			readLine = inputStream.readLine();						//Skip empty line
		}

		//Extract name of TestCase (either whole line (without spaces) or first word on the line  
		Pattern pattern = Pattern.compile("^\\s*(.*?)((\\s.*)*)$");	
		Matcher matcher = pattern.matcher(readLine);
		TestCaseType testCase;
		if(matcher.find() && matcher.groupCount() >= 1)				//Found TestCase name
			testCase = new TestCaseType(matcher.group(1));
		else
			throw new IncorrectTestConfig("Error while reading name on the line: " + readLine);
		
		while(readTestParam(inputStream, testCase))			//While we can read parameters
			;
		
		return testCase;
	}

	private boolean readTestParam(BufferedReader inputStream, TestCaseType testCase)
		throws IOException
	{
		String readLine = inputStream.readLine();
		if(isEmptyLine(readLine))											//Reached end of test case
			return false;

		Pattern pattern = Pattern.compile("^\\s*(.*?)=(.*?)((,?\\s+)(.*))?$");	//Decode name=value pattern
		Matcher matcher = pattern.matcher(readLine);

		if (matcher.find() && matcher.groupCount() >= 2){					//parameter matched
			TestParamType testParam = new TestParamType(matcher.group(2));
			String attributes = matcher.group(5);
			if(attributes != null && attributes.length() > 0){				//We have attributes
				try{
					readTestParamAttr(attributes, testParam);
				}catch(IncorrectTestParameter error){
					throw new IncorrectTestParameter("Error " + error.getMessage() + " on the line: " + readLine);
				}
			}
			testCase.addTestParam(matcher.group(1), testParam);
			return true;
		}else
			throw new IncorrectTestParameter("Error while reading parameters on the line: " + readLine);
	}

	private void readTestParamAttr(String attributes, TestParamType testParam)
		throws IncorrectTestParameter
	{
		while(attributes != null && attributes.length() > 0){
			Pattern pattern = Pattern.compile("^\\s*(.*?)=(.*?)((,?\\s+)(.*))?$");	//Decode attName=attValue pattern
			Matcher matcher = pattern.matcher(attributes);

			if (matcher.find() && matcher.groupCount() >= 2){					//parameter matched
				attributes = matcher.group(5);
				testParam.addAttribute(matcher.group(1), matcher.group(2));
			}else
				throw new IncorrectTestParameter("Error while reading attributes");
		}
	}
	
	private boolean isEmptyLine(String readLine)
	{
		if(readLine == null || readLine == "")
			return true;

		return readLine.matches("^\\s*$");
	}
}
