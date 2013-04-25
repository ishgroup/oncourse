package ish.oncourse.mbean;

import ish.oncourse.model.PaymentIn;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.jar.Manifest;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.services.ApplicationGlobals;

public class ApplicationData extends NotificationBroadcasterSupport implements ApplicationDataMBean {
	private static final String ESCAPE_QUERY_STRING = "';";
	private static final String SELECT_PROBLEM_IN_TRANSACTION_ENROLMENT_QUERY = "select count(*) from Enrolment where status=%s and modified<='";
	private static final String FULL_SQL_TIME_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String ATTRIBUTE_MBEAN_CHANGED_NOTIFICATION_MESSAGE = "An attribute of this MBean has changed";
	private static final String CANCELED_TYPE_MESSAGE = "canceled";
	private static final String REFUNDED_TYPE_MESSAGE = "refunded";
	private static final String IN_TRANSACTION_TYPE_MESSAGE = "in transaction";
	private static final String SUCCESS_TYPE_MESSAGE = "success";
	private static final String ENROLMENT_CANCELED_STATUS = "8";
	private static final String ENROLMENT_REFUND_STATUS = "9";
	private static final String ENROLMENT_IN_TRANSACTION_STATUS = "2";
	private static final String ENROLMENT_SUCCESS_STATUS = "3";
	private static final String FAILED_TO_LOAD_ENROLMENTS_COUNT_MESSAGE = "Failed to load %s enrolments count due the errors";
	private static final String SELECT_ENROLMENTS_BY_STATUS_STRING = "select count(*) from Enrolment where status=%s;";
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
		return getEnrolmentsCountForStatus(ENROLMENT_SUCCESS_STATUS, SUCCESS_TYPE_MESSAGE);
	}
	
	@Override
	public String getProblemInTransactionEnrolments() {
		Calendar cal = Calendar.getInstance();
		int problemEnrollmentDelay = PaymentIn.EXPIRE_INTERVAL + 10;
		cal.add(Calendar.MINUTE, -problemEnrollmentDelay);
		StringBuilder selectQuery = new StringBuilder(SELECT_PROBLEM_IN_TRANSACTION_ENROLMENT_QUERY);
		selectQuery.append(new SimpleDateFormat(FULL_SQL_TIME_DATE_FORMAT).format(cal.getTime())).append(ESCAPE_QUERY_STRING);
		final String enrolmentsCount = evaluateEnrolmentsCountByStatus(ENROLMENT_IN_TRANSACTION_STATUS, selectQuery.toString());
		return enrolmentsCount != null ? enrolmentsCount : String.format(FAILED_TO_LOAD_ENROLMENTS_COUNT_MESSAGE, IN_TRANSACTION_TYPE_MESSAGE);
	}

	@Override
	public String getTotalRefundedEnrolments() {
		return getEnrolmentsCountForStatus(ENROLMENT_REFUND_STATUS, REFUNDED_TYPE_MESSAGE);
	}

	@Override
	public String getTotalCanceledEnrolments() {
		return getEnrolmentsCountForStatus(ENROLMENT_CANCELED_STATUS, CANCELED_TYPE_MESSAGE);
	}
	
	private String getEnrolmentsCountForStatus(String status, String enrolmentType) {
		final String enrolmentsCount = evaluateEnrolmentsCountByStatus(status, SELECT_ENROLMENTS_BY_STATUS_STRING);
		return enrolmentsCount != null ? enrolmentsCount : String.format(FAILED_TO_LOAD_ENROLMENTS_COUNT_MESSAGE, enrolmentType);
	}
		
	private Connection getConnection() throws SQLException {
		if (dataSource != null) {
			Connection connection = dataSource.getConnection();
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
		}
		return null;
	}
	
	private String evaluateEnrolmentsCountByStatus(String status, String selectQuery) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			if (connection != null && !connection.isClosed()) {
				statement = connection.createStatement();
			} else {
				LOGGER.warn("Failed to evaluate enrolment count because connection already closed or pool limit reached.");
				return null;
			}
			if (statement == null) {
				LOGGER.warn("Failed to evaluate enrolment count because not able to create statement.");
				return null;
			}
			ResultSet result = null;
			try {
				result = statement.executeQuery(String.format(selectQuery, status));
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
		} catch (SQLException e) {
			LOGGER.warn("Failed to evaluate enrolment count because connection already closed", e);
		} finally {
			try {
				if (statement != null && !statement.isClosed()) {
					statement.close();
				}
			} catch (SQLException e) {
				LOGGER.warn("Exception occurs when try to close the statement", e);
			}
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.warn("Exception occurs when try to close the connection", e);
			}
		}
		return null;
	}
	
	@Override
    public MBeanNotificationInfo[] getNotificationInfo() {
		String[] types = new String[] {AttributeChangeNotification.ATTRIBUTE_CHANGE};
		String name = AttributeChangeNotification.class.getName();
		String description = ATTRIBUTE_MBEAN_CHANGED_NOTIFICATION_MESSAGE;
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
		return new MBeanNotificationInfo[] {info};
    }

}
