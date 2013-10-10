
package virtuoso;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Customized <code>DataSource</code> for ODCS application configurable with
 * {@link AppConfig} and with prefilled Virtuoso JDBC Driver.
 * 
 * @author Jan Vojt
 */
public class ConfigurableDataSource extends BasicDataSource {
	
	/**
	 * Class name to be used as JDBC driver.
	 */
	public static final String DRIVER_CLASS_NAME = "virtuoso.jdbc4.Driver";

	/**
	 * <code>DataSource</code> constructed from configuration.
	 * 
	 * @param config application configuration
	 */
	public ConfigurableDataSource(AppConfig config) {
		setUrl(buildUrl(config));
		setUsername(config.getString(ConfigProperty.VIRTUOSO_USER));
		setPassword(config.getString(ConfigProperty.VIRTUOSO_PASSWORD));
		setDriverClassName(DRIVER_CLASS_NAME);
		setDefaultAutoCommit(false);
	}
	
	/**
	 * Connection URL factory.
	 * 
	 * @param config
	 * @return 
	 */
	private static String buildUrl(AppConfig config) {
		String url = "jdbc:virtuoso://%s:%s/charset=%s";
		String host = config.getString(ConfigProperty.VIRTUOSO_HOSTNAME);
		String port = config.getString(ConfigProperty.VIRTUOSO_PORT);
		String charset = config.getString(ConfigProperty.VIRTUOSO_CHARSET);
		return String.format(url, host, port, charset);
	}

}
