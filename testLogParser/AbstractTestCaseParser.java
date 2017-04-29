package testLogParser;

import java.io.IOException;
import java.util.ArrayList;

import testLogParser.Visitor.AbstractVisitor;

/** This class should be extended by appropriate parser (either XML or ExpectedResults).
 * @author AKosarev
 *
 */
public abstract class AbstractTestCaseParser {
	private TestCaseType[] testCases;
	
	public AbstractTestCaseParser(){
		testCases = null;
	}
	
	final public void setTestCases(ArrayList<TestCaseType> theTestCases)
	{
		testCases = new TestCaseType[theTestCases.size()];
		theTestCases.toArray(testCases);
	}

	/** Performs different comparison of the testCases stored in <b>examplesParser</b>.
	 * Uses different accumulation based on <b>visitor</b> object.
	 *  
	 * @param examplesParser contains example testCases
	 * @param visitor used for different accumulation type
	 * @throws IOException
	 */
	final public void Compare(AbstractTestCaseParser examplesParser, AbstractVisitor visitor)
		throws IOException
	{
		if(testCases == null){
			String msg = "Result test cases weren't passed to " + this.getClass().toString();
			throw new IOException(msg);
		}
		
		int i = 0;
		//Visit Test cases present in both XML and expected results log
		while(i < testCases.length && i < examplesParser.testCases.length){
			testCases[i].Compare(examplesParser.testCases[i], visitor);
			
			i++;
		}
		
		//Visit Test cases for which expected test cases were not found
		for(; i < testCases.length; i++)
				testCases[i].Compare(null, visitor);
	}

}
