package ish.oncourse.mbean;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.jar.Manifest;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.services.ApplicationGlobals;

public class ApplicationData extends NotificationBroadcasterSupport implements ApplicationDataMBean {
	private static final String MANIFEST_FILE_PATH = "/META-INF/MANIFEST.MF";
	private static final String HUDSON_RELEASE_VERSION = "Implementation-Version";
	private static Logger LOGGER = Logger.getLogger(ApplicationData.class);	
	private final String version;
	private final String application;
	private final ApplicationGlobals applicationGlobals;
	private final DataSource dataSource;
		
	public ApplicationData(final String application, final ApplicationGlobals applicationGlobals, final DataSource dataSource) {
		super();
		this.application = application;
		this.applicationGlobals = applicationGlobals;
		this.version = getCiVersion();
		this.dataSource = dataSource;
	}
	
	private synchronized String getCiVersion() {
		String ciVersion = StringUtils.EMPTY;
		Manifest manifest = null;
		if (applicationGlobals != null && applicationGlobals.getServletContext() != null) {
			try {
				InputStream is = applicationGlobals.getServletContext().getResourceAsStream(MANIFEST_FILE_PATH);
				if (is != null) {
					try {
						manifest = new Manifest(is);
						ciVersion = manifest.getMainAttributes().getValue(HUDSON_RELEASE_VERSION);
					} finally {
						is.close();
					}
				}
			} catch (Exception e) {
				LOGGER.error("Failed to load build version", e);
			}
		} else {
			LOGGER.error("No application globals or servlet context available to get the Ci version.");
		}
		return ciVersion;
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
		final String enrolmentsCount = evaluateEnrolmentsCount();
		return enrolmentsCount != null ? enrolmentsCount : "Failed to load success enrolments count due the errors";
	}
	
	private Statement takeStatement() throws NamingException, SQLException {
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
					result.beforeFirst();
					result.next();
					long count = result.getLong(1);
					return count + StringUtils.EMPTY;
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
