package ish.oncourse.mbean;

import ish.oncourse.services.persistence.ICayenneService;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;

import org.apache.tapestry5.ioc.annotations.Inject;

public class ApplicationData extends NotificationBroadcasterSupport implements ApplicationDataMBean {

	private final String version;
	private final String application;
	
	@Inject
	private ICayenneService cayenneService;
	
	public ApplicationData(final String version, final String application) {
		super();
		this.version = version;
		this.application = application;
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
