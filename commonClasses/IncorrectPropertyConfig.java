/**
 * 
 */
package commonClasses;

/** Class containing information about Incorrect Property read inside PropertiesReader class
 * 
 * @author AKosarev
 */
public class IncorrectPropertyConfig extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3816159185731629290L;
	
	protected IncorrectPropertyConfig(String reason){super(reason);}

}
