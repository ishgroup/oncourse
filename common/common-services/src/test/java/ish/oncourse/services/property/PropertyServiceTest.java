package ish.oncourse.services.property;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyServiceTest {

	@Test
	public void serviceInit() throws Exception {
		PropertyService service = new PropertyService("junit");
		assertEquals("/a/b/c", service.string(Property.CustomComponentsPath));
	}
}
