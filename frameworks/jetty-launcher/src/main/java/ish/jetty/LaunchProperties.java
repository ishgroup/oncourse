package ish.jetty;

/**
 * Defines System properties. Each one of the properties can be passed
 * optionally on the command line as -DPROPERTY_NAME=value
 */
public enum LaunchProperties {

	ISH_WEB_PORT("8080"),

	ISH_WEB_CONTEXT("/"),

	ISH_DS_NAME("jdbc/oncourse"),

	ISH_DS_FACTORY(MySQLDataSourceFactory.class.getName()),

	ISH_DS_URL("jdbc:mysql://localhost:3306/willow_college"),

	ISH_DS_USER_NAME("root"),

	ISH_DS_PASSWORD("test");

	private String defaultValue;

	private LaunchProperties(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Returns property value.
	 */
	public String getValue() {
		return System.getProperty(name(), defaultValue);
	}
}
