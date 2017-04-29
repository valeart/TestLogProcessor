package testLogParser.Visitor;

import java.util.ArrayList;

import testLogParser.TestCaseType;
import testLogParser.TestParamType;

public class OKTestCasesCollector implements AbstractVisitor {
	private ArrayList<TestCaseType> OKTestCases = new ArrayList<TestCaseType>();
	private TestCaseType lastTestCase = null;

	@Override
	public void VisitNewTestCase(TestCaseType testCase) {
		if(lastTestCase != null)
			OKTestCases.add(lastTestCase);
		
		lastTestCase = testCase;
	}


	@Override
	public void VisitNoID(TestCaseType testCase) {
		lastTestCase = null;	
	}
	
	@Override
	public void VisitDoubleID(TestCaseType testCase, String ID1, String ID2) {
		lastTestCase = null;	
	}

	
	@Override
	public void VisitMissingTestCase(TestCaseType testCase) {
		lastTestCase = null;
	}

	@Override
	public void VisitIncorrectTestCaseNames(TestCaseType testCase, TestCaseType example) {
		lastTestCase = null;
	}

	
	@Override
	public void VisitIncorrectParametersAmount(TestCaseType testCase, TestCaseType example) {
		lastTestCase = null;
	}

	@Override
	public void VisitMissingParameter(String key) {
		lastTestCase = null;
	}

	@Override
	public void VisitExtraParameter(String key) {
		lastTestCase = null;		
	}
	
	@Override
	public void VisitIncorrectParameterValue(TestParamType testParam, TestParamType example, String key) {
		lastTestCase = null;
	}

	@Override
	public void VisitIncorrectParameterAttributes(TestParamType testParam, TestParamType example, String key) {
		lastTestCase = null;
	}
	
	public ArrayList<TestCaseType> getResults(){
		if(lastTestCase != null){
			OKTestCases.add(lastTestCase);
			lastTestCase = null;
		}
			
		return OKTestCases;
	}

}
