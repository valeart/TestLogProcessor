package testLogParser.Visitor;

import java.util.ArrayList;

import testLogParser.TestCaseType;
import testLogParser.TestParamType;

/** Visitor collecting information about TestCases.
 * contains result of TestCases comparison in string Array:
 * 1 string - test is OK, >1 strings - test is NOK.
 * 
 * @author AKosarev
 *
 */
public class TestCasesAnalyzer implements AbstractVisitor {
	
	/** Class containing result in string form for TestCase comparison
	 * 
	 * @author AKosarev
	 *
	 */
	private class TestCaseResultType {
		final private String name;
		public ArrayList<String> errors;
		
		public TestCaseResultType(String aName){
			name = aName;
			errors = new ArrayList<String>();
			errors.add("TestCase " + name + " OK");			//Currently, there is no errors
			succeeded++;									//field of TestCasesAnalyzer
		}
		
		public void addError(String error){
			if(errors.size() == 1){				
				errors.set(0, "TestCase " + name + " NOK");	//We found first error
				succeeded--;								//fields of TestCasesAnalyzer
				failed++;
			}
			errors.add(error);
		}
	}
	
	private ArrayList<TestCaseResultType> testCaseResults;
	private TestCaseResultType currResult;
	private int succeeded;
	private int failed;

	public TestCasesAnalyzer(){
		testCaseResults = new ArrayList<TestCaseResultType>();
		currResult = null;
		succeeded = 0;
		failed = 0;
	}

	@Override
	public void VisitNewTestCase(TestCaseType testCase){
		currResult = new TestCaseResultType(testCase.getTestCaseID());
		testCaseResults.add(currResult);
	}
	

	@Override
	public void VisitNoID(TestCaseType testCase) {
		currResult.addError("\tTest Case contains no ID values");		
	}

	@Override
	public void VisitDoubleID(TestCaseType testCase, String ID1, String ID2) {
		currResult.addError("\tTest Case contains double ID values: " + ID1 + " " + ID2);		
	}
	
	
	@Override
	public void VisitMissingTestCase(TestCaseType testCase){
		currResult.addError("Missing expected Test Case:\n" + testCase.toString());
	}

	@Override
	public void VisitIncorrectTestCaseNames(TestCaseType testCase, TestCaseType example){
		currResult.addError("Name: " + testCase.getName() + "!=" + example.getName());
	}

	@Override
	public void VisitIncorrectParametersAmount(TestCaseType testCase, TestCaseType example){
		currResult.addError("\tParameters amount: " + testCase.getParametersAmount() +
				"!=" + example.getParametersAmount());
	}


	@Override
	public void VisitMissingParameter(String key){
		currResult.addError("\tMissing parameter " + key);
	}
	
	@Override
	public void VisitExtraParameter(String key){
		currResult.addError("\tExtra parameter " + key);
	}
	
	@Override
	public void VisitIncorrectParameterValue(TestParamType testParam, TestParamType example, String key){
		String message = "\tIncorrect parameter value: " + key + 
		"(" + testParam + "). ";
		message += "Expected: (" + example + ")";
		currResult.addError(message);
	}

	@Override
	public void VisitIncorrectParameterAttributes(TestParamType testParam, TestParamType example, String key){
		String message = "\tIncorrect parameter attributes: " + key + 
		"(" + testParam + "). ";
		message += "Expected: (" + example + ")";
		currResult.addError(message);
	}
	
	public ArrayList<String> getTotalResults(){
		ArrayList<String> result = new ArrayList<String>();
		result.add("Total amount of test cases: " + testCaseResults.size());
		
		result.add("Tests passed: " + succeeded);
		result.add("Tests failed: " + failed);

		return result;
	}
	
	public ArrayList<String> getResults(){
		ArrayList<String> result = new ArrayList<String>();
		for(TestCaseResultType testResult : testCaseResults)
		{
			result.addAll(testResult.errors);
			result.add("");
		}
		
		return result;
	}

}
