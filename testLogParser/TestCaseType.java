package testLogParser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import testLogParser.Errors.IncorrectTestParameter;
import testLogParser.Errors.IncorrectVisitOrder;
import testLogParser.Visitor.AbstractVisitor;

public final class TestCaseType{
	private final String prefix;
	private final String name;
	private final String longName;
	
	private Map<String, TestParamType> testParameters;
	
	public TestCaseType(String aName)
	{
		longName = aName;
		String pattern_str = "^(.*?:)(.*)$";
		Pattern pattern = Pattern.compile(pattern_str);
		Matcher matcher = pattern.matcher(aName);
		if(matcher.find()){							//We have prefix before name
			prefix = matcher.group(1);
			name = matcher.group(2);
		}else{
			prefix = "";
			name = aName;
		}

		testParameters = new HashMap<String, TestParamType>();
	}

	private String getShortName(){return name;}
	public String getName(){return longName;}
	public String getTestCaseID(){
		String ID1 = this.getParamValue("TradeID");
		String ID2 = this.getParamValue("TID");
		
		String result;
		if(ID1 != null && ID2 != null)
			result = ID1 + " " + ID2;
		else if(ID1 == null && ID2 == null)
			result = "NoID";
		else if(ID1 != null)
			result = ID1;
		else
			result = ID2;
		
		return result;
	}
	
	public int getParametersAmount() {return testParameters.size();}
	public String getParamValue(String paramName)
	{
		if(!testParameters.containsKey(paramName))
			return null;
		
		return testParameters.get(paramName).getValue();
	}
	public String getAttrValue(String paramName, String attrName){return testParameters.get(paramName).getAttrValue(attrName);}
	
	public void addTestParam(String paramName, TestParamType parameter)
		throws IncorrectTestParameter
	{
		if(parameter == null)
			throw new IncorrectTestParameter("Null parameter is passed to Test case");
		
		String pattern_str = "^" + prefix + "(.*)$";
		Pattern pattern = Pattern.compile(pattern_str);
		Matcher matcher = pattern.matcher(paramName);
		if(matcher.find()){								//We have prefix before name
			paramName = matcher.group(1);
		}
		if(testParameters.put(paramName, parameter) != null)
			throw new IncorrectTestParameter("Duplicate parameter " + paramName + " is passed to Test case");
	}
	
	/** Compares current object to <b>example</b> testCases,
	 * collecting different information with the help of <b>visitor</b> object.
	 * 
	 * @param example contains testCases with expected results 
	 * @param visitor collects different statistics
	 * @throws IncorrectVisitOrder
	 */
	public void Compare(TestCaseType example, AbstractVisitor visitor)
		throws IncorrectVisitOrder
	{
		visitor.VisitNewTestCase(this);
		
		if(!testParameters.containsKey("TradeID") && !testParameters.containsKey("TID"))
			visitor.VisitNoID(this);
		else if(testParameters.containsKey("TradeID") && testParameters.containsKey("TID"))
			visitor.VisitDoubleID(this, this.getParamValue("TradeID"), this.getParamValue("TID"));
		
		if(example == null){
			visitor.VisitMissingTestCase(this);
			return;
		}

		if(!getShortName().equals(example.getShortName()) && !getName().equals(example.getName()) &&
			!getShortName().equals(example.getName()) && !getName().equals(example.getShortName()))
			visitor.VisitIncorrectTestCaseNames(this, example);
		
		if(getParametersAmount() != example.getParametersAmount())
			visitor.VisitIncorrectParametersAmount(this, example);

		//Run through all parameters of example TestCase
		for(Map.Entry<String, TestParamType> exampleEntry: example.testParameters.entrySet()){
			if(!testParameters.containsKey(exampleEntry.getKey()))
				visitor.VisitMissingParameter(exampleEntry.getKey());
			else
				testParameters.get(exampleEntry.getKey()).Compare(exampleEntry.getValue(), visitor, exampleEntry.getKey());
		}
			
		//Run through all parameters of Test Case which don't exist in example TestCase
		for(Map.Entry<String, TestParamType> testCaseEntry: testParameters.entrySet())
			if(!example.testParameters.containsKey(testCaseEntry.getKey()))
					visitor.VisitExtraParameter(testCaseEntry.getKey());
	}
	
	@Override
	public String toString()
	{
		String result = "<TestCase name=\"" + name + "\">\n";
		
		for(Map.Entry<String, TestParamType> entry : testParameters.entrySet())
			result += "<parameter name=\"" + entry.getKey() + 
						"\" value=" + entry.getValue() + "/>\n";
		
		result += "</TestCase>";
		
		return result;
	}
	
}