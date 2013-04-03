package ish.oncourse.mbean;

public interface ApplicationDataMBean {
	static final String FAILED_TO_LOAD_BUILD_INFO_PROPERTIES_FILE_MESSAGE = "Failed to load build info properties file!";
	static final String BUILD_INFO_PROPERTIES_FILE = "buildInfo.properties";
	static final String RELEASE_TIME_PROPERTY = "releaseTime";
	static final String VERSION_PROPERTY = "version";
	static final String UNKNOWN_VERSION_VALUE = "unknown version";
	static final String DEFAULT_PROJECT_VERSION = "${currentBuildVersion}";
	public String getVersion();
	public String getApplication();
	public String getTotalSuccessEnrolments();
	public String getProblemInTransactionEnrolments();
	public String getTotalRefundedEnrolments();
	public String getTotalCanceledEnrolments();
}
