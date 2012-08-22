package ish.oncourse.mbean;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ApplicationData extends NotificationBroadcasterSupport implements ApplicationDataMBean {
	private static Logger LOGGER = Logger.getLogger(ApplicationData.class);	
	private final String version;
	private final String application;
	private final Properties properties = new Properties();
		
	public ApplicationData(final String application) {
		super();
		this.application = application;
		loadProperties();
		String version = properties.getProperty(VERSION_PROPERTY);
		this.version  = isCorrectVersion(version) ? version : UNKNOWN_VERSION_VALUE;
	}
	
	private boolean isCorrectVersion(final String version) {
		return StringUtils.trimToNull(version) != null && !DEFAULT_PROJECT_VERSION.equalsIgnoreCase(version);
	}
	
	private boolean isPropertiesCorrectlyInited() {
		return !properties.isEmpty() && properties.size() == 2;
	}
	
	private void loadProperties() {
		if (!isPropertiesCorrectlyInited()) {
			try {
				properties.clear();
				properties.load(getClass().getClassLoader().getResourceAsStream(BUILD_INFO_PROPERTIES_FILE));
			} catch (IOException e) {
				LOGGER.warn(FAILED_TO_LOAD_BUILD_INFO_PROPERTIES_FILE_MESSAGE, e);
				properties.put(VERSION_PROPERTY, UNKNOWN_VERSION_VALUE);
			}
		}
	}
	
	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getApplication() {
		return application;
	}
	
	@Override
	public String getTotalSuccessEnrolments() {
		return StringUtils.EMPTY;
		//final String enrolmentsCount = evaluateEnrolmentsCount();
		//return enrolmentsCount != null ? enrolmentsCount : "Failed to load success enrolments count due the errors";
	}
	
	private Statement takeStatement() throws NamingException, SQLException {
		Context context = (Context) new InitialContext().lookup("java:comp/env/jdbc");
		DataSource dataSource = (DataSource) context.lookup("/oncourse");
		if (dataSource != null && !dataSource.getConnection().isClosed()) {
			return dataSource.getConnection().createStatement();
		}
		return null;
	}
	
	private String evaluateEnrolmentsCount() {
		try {
			Statement statement = takeStatement();
			if (statement == null) {
				return null;
			}
			ResultSet result = null;
			try {
				result = statement.executeQuery("select count(*) from Enrolment where status=3;");
				if (result != null) {
					System.out.println(result.getLong(0));
					return result.getLong(0) + "";
				}
			} finally {
				if (result != null && !result.isClosed()) {
					result.close();
				}
			}
			
			
		} catch (NamingException e) {
			LOGGER.warn("Failed to load lookup datasource", e);
		} catch (SQLException e) {
			LOGGER.warn("Failed to evaluate enrolment count because connection already closed", e);
		}
		return null;
	}
	
	@Override
    public MBeanNotificationInfo[] getNotificationInfo() {
		String[] types = new String[] {AttributeChangeNotification.ATTRIBUTE_CHANGE};
		String name = AttributeChangeNotification.class.getName();
		String description = "An attribute of this MBean has changed";
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
		return new MBeanNotificationInfo[] {info};
    }

}
