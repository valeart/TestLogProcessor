package testLogParser;

import java.util.HashMap;
import java.util.Map;

import testLogParser.Errors.IncorrectTestParameter;
import testLogParser.Errors.IncorrectVisitOrder;
import testLogParser.Visitor.AbstractVisitor;

public final class TestParamType {
	private String paramValue;
	private Map<String, String> attributes;
	
	public TestParamType(String aParamValue)
		throws IncorrectTestParameter
	{
		if(aParamValue == null)
			throw new IncorrectTestParameter("Null parameter value is used");
		
		paramValue = aParamValue;
		attributes = new HashMap<String, String>();
	}
	
	public String getValue(){return paramValue;}
	public String getAttrValue(String attrName){return attributes.get(attrName);}
	
	public void addAttribute(String attrName, String attrValue)
		throws IncorrectTestParameter
	{
		if(attrName == null || attrValue == null)
			throw new IncorrectTestParameter("Null attribute name or value is used");
		if(attrName == "value")
			throw new IncorrectTestParameter("Attribute \"value\" is used in attribute list");
		
		if(attributes.put(attrName, attrValue) != null)
			throw new IncorrectTestParameter("Duplicate attribute name " + attrName + " is used");
	}

	public boolean equalValues(TestParamType otherObject)
	{
		if(!paramValue.equals(otherObject.paramValue))
			return false;
		
		return true; 
	}

	public boolean equalAttributes(TestParamType otherObject)
	{
		if(attributes.size() != otherObject.attributes.size())
			return false;
		
		for(Map.Entry<String, String> attribute : attributes.entrySet())
		{
			if(!(attribute.getValue().equals(otherObject.attributes.get(attribute.getKey()))))
				return false;
		}
		
		return true;
	}

	/** Compares current object to <b>example</b> testParam,
	 * collecting different information with the help of <b>visitor</b> object.
	 * 
	 * @param example contains testParam with expected result 
	 * @param visitor collects different statistics
	 * @throws IncorrectVisitOrder
	 */
	public void Compare(TestParamType example, AbstractVisitor visitor, String key)
		throws IncorrectVisitOrder
	{
		if(example == null)
			throw new IncorrectVisitOrder();

		if(!equalValues(example))
			visitor.VisitIncorrectParameterValue(this, example, key);
		else if(!equalAttributes(example))
			visitor.VisitIncorrectParameterAttributes(this, example, key);

	}
	
	@Override
	public String toString()
	{
		String result = "\"" + paramValue + "\"";

		if(attributes.size() > 0)
			result += ",";
		for(Map.Entry<String, String> attribute : attributes.entrySet())
			result += " " + attribute.getKey() + "=\"" + attribute.getValue() + "\"";
		
		return result;
	}

}
