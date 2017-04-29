package testLogParser.Errors;

import java.io.IOException;

public class IncorrectTestConfig extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3058781347577897503L;
	
	public IncorrectTestConfig(String error)
	{
		super(error);
	}

}
