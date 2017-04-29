package testLogParser.Visitor;

import testLogParser.TestCaseType;
import testLogParser.TestParamType;

public interface AbstractVisitor {

	void VisitNewTestCase(TestCaseType testCase);

	void VisitNoID(TestCaseType testCase);
	void VisitDoubleID(TestCaseType testCase, String ID1, String ID2);

	void VisitMissingTestCase(TestCaseType testCase);
	void VisitIncorrectTestCaseNames(TestCaseType testCase, TestCaseType example);
	void VisitIncorrectParametersAmount(TestCaseType testCase, TestCaseType example);
	
	void VisitMissingParameter(String key);
	void VisitExtraParameter(String key);
	void VisitIncorrectParameterValue(TestParamType testParam, TestParamType example, String key);
	void VisitIncorrectParameterAttributes(TestParamType testParam, TestParamType example, String key);
}
