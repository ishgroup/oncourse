package ish.oncourse.services.property;

import ish.oncourse.services.property.Property;
import ish.oncourse.services.property.PropertyService;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyServiceTest {

	@Test
	public void serviceInit() throws Exception {
		PropertyService service = new PropertyService("test");
		assertEquals("/a/b/c", service.string(Property.CustomComponentsPath));
	}
}
