import testLogParser.TestCaseType;

import testLogParser.XmlLogParser;
import testLogParser.ExpectedResultsParser;

import testLogParser.Visitor.OKTestCasesCollector;
import testLogParser.Visitor.TestCasesAnalyzer;

import java.io.IOException;
import org.xml.sax.SAXException;
import commonClasses.IncorrectPropertyConfig;
import jmsCommunication.IncorrectJmsConfig;

import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.ArrayList;

import javax.jms.JMSException;
import jmsCommunication.QSender;

public class LogProcessor {
	private XmlLogParser xmlParser = null;
	private ExpectedResultsParser resultsParser = null;

	/** Collects TestCases (from generated XML log and configuration file with expected examples)
	 * 
	 * @param xmlLogFileName File path to the XML Log File
	 * @param expectedResultFileName File path to the file with expected results
	 * @throws IOException
	 * @throws SAXException
	 */
	public void collectTestCases(String xmlLogFileName, String expectedResultFileName) throws IOException, SAXException{
		xmlParser = new XmlLogParser(xmlLogFileName);
		resultsParser = new ExpectedResultsParser(expectedResultFileName);
	}

	/** Compares collections of TestCases
	 * Generates statistics of comparison and prints to OutputStream
	 * 
	 * @param outputStream Stream where statistics should be written (Output file or STDOUT) 
	 * @throws IOException
	 */
	public void getTestsStatistics(OutputStream outputStream) throws IOException
	{
		TestCasesAnalyzer errorsCollector = new TestCasesAnalyzer();	//Visitor
		xmlParser.Compare(resultsParser, errorsCollector);

		ArrayList<String> results = errorsCollector.getTotalResults();
		for(String message : results)
			outputStream.write((message + "\n").getBytes());
		
		outputStream.write("=================================\n".getBytes());
		
		results = errorsCollector.getResults();
		for(String message : results)
			outputStream.write((message + "\n").getBytes());
	}

	/** Compares collections of TestCases
	 * Collects OK TestCases and sends them to JMS server configured in jmsConfigFileName
	 * 
	 * @param jmsConfigFileName Name of the configuration file for connecting to JMS server
	 * @throws IOException
	 * @throws JMSException
	 * @throws IncorrectPropertyConfig
	 * @throws IncorrectJmsConfig
	 */
	public void interactWithJMS(String jmsConfigFileName) throws IOException, JMSException, IncorrectPropertyConfig, IncorrectJmsConfig
	{
		QSender MessagesSender = new QSender(jmsConfigFileName);

		OKTestCasesCollector okTestCasesCollector = new OKTestCasesCollector();	//Visitor
		xmlParser.Compare(resultsParser, okTestCasesCollector);
		
		ArrayList<TestCaseType> okResults = okTestCasesCollector.getResults();
		for(TestCaseType testCase : okResults)
			MessagesSender.sendMessage(testCase.getTestCaseID());
		
		MessagesSender.sendMessage("quit");

		MessagesSender.closeConnection();
	}
	
	/** Performs main logic of the program (TestLogFile and Expected results files parsing, 
	 * generating statistics, interrogating with JMS server
	 * 
	 * @param args TestLogFileName TestExamplesName OutputFileName(optional) jmsConfigFileName
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if(args.length < 3){
			System.out.println("Missing input file name path(s).");
			System.out.println("Usage: LogProcessor <TestLogFileName> <TestExamplesName> [<OutputFileName>] <jmsConfigFileName>");
			return;
		}

		try {
			//Get XmlLogResults & Expected results
			LogProcessor process = new LogProcessor();
			process.collectTestCases(args[0], args[1]);
			
			OutputStream outputStream; String jmsConfigFileName;
			if(args.length > 3){								//We provided output file
				outputStream = new FileOutputStream(args[2]);
				jmsConfigFileName = args[3];
			}else{
				outputStream = System.out;						//Use default STDOUT
				jmsConfigFileName = args[2];
			}

			process.getTestsStatistics(outputStream);			//Print statistics
			process.interactWithJMS(jmsConfigFileName);			//Print OK testCases to JMS Server

		} catch (SAXException error) {
			System.out.println("Error in parsing");
			error.printStackTrace();
		} catch (IncorrectPropertyConfig error){
			System.out.println("Error in JMS Connection establishing");
			error.printStackTrace();
		} catch (JMSException error){
			System.out.println("Error in JMS Message sending");
			error.printStackTrace();
		}
	}

}
