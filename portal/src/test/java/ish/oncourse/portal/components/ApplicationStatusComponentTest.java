package ish.oncourse.portal.components;

import ish.common.types.ApplicationStatus;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * Created by anarut on 12/2/16.
 */
public class ApplicationStatusComponentTest {
	
	private static final String PROP_FILE_PATH = "ish/oncourse/portal/components/ApplicationStatusComponent.properties";

	@Test
	public void testRevoked() throws IOException {
		InputStream resourceAsStream = ApplicationStatusComponent.class.getClassLoader().getResourceAsStream(PROP_FILE_PATH);

		Properties prop = new Properties();
		prop.load(resourceAsStream);

		assertNotNull(prop.get(ApplicationStatusComponent.EXPIRED_APPLICATION_MESSAGE_KEY));
		for (ApplicationStatus status : ApplicationStatus.values()) {
			assertNotNull(prop.get(status.name()));
		}
	}
}
