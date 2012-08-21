package ish.oncourse.mbean;

import java.io.IOException;
import java.util.Properties;

import ish.oncourse.services.persistence.ICayenneService;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ApplicationData extends NotificationBroadcasterSupport implements ApplicationDataMBean {
	private static Logger LOGGER = Logger.getLogger(ApplicationData.class);	
	private final String version;
	private final String application;
	private final Properties properties = new Properties();
	
	@Inject
	private ICayenneService cayenneService;
	
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
	public boolean isCayenneLoaded() {
		return cayenneService != null;
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
    public MBeanNotificationInfo[] getNotificationInfo() {
		String[] types = new String[] {AttributeChangeNotification.ATTRIBUTE_CHANGE};
		String name = AttributeChangeNotification.class.getName();
		String description = "An attribute of this MBean has changed";
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
		return new MBeanNotificationInfo[] {info};
    }

}
