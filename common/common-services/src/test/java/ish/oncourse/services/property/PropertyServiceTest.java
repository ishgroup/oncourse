package ish.oncourse.services.property;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertyServiceTest {

	@Test
	public void serviceInit() throws Exception {
		PropertyService service = new PropertyService("junit");
		assertEquals("/a/b/c", service.string(Property.CustomComponentsPath));
	}
}
