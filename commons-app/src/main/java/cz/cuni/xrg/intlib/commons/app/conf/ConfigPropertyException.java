package cz.cuni.xrg.intlib.commons.app.conf;

/**
 * Represents an error caused property in configuration.
 * 
 * @author Jan Vojt
 */
public abstract class ConfigPropertyException extends RuntimeException {
	
	/**
	 * Name of missing property.
	 */
	protected ConfigProperty property;

	/**
	 * Constructs an instance of
	 * <code>MissingConfigPropertyException</code> with the specified
	 * property printed in message.
	 *
	 * @param property name
	 */
	public ConfigPropertyException(ConfigProperty property) {
		this.property = property;
	}

	/**
	 * Error message.
	 * 
	 * @return 
	 */
	@Override
	public String getMessage() {
		return "Config is missing property: " + property + ".";
	}
	
}