package ish.oncourse.services.resource;

import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class ResourceServiceTest extends Assert {

	@Test
	public void getWebResource() throws Exception {
		String packagePath = getClass().getPackage().getName()
				.replace('.', '/');

		URL placeholderUrl = getClass().getClassLoader().getResource(
				packagePath + "/placeholder.txt");

		final File root = new File(placeholderUrl.toURI()).getParentFile();

		IPropertyService propertyService = new IPropertyService() {

			public String string(Property property) {
				switch (property) {
				case CustomComponentsPath:
					return root.getAbsolutePath();
				default:
					throw new IllegalArgumentException(String.valueOf(property));
				}
			}
		};
		
		ResourceService resourceService = new ResourceService(null, null);

		Resource r1 = resourceService.getWebResource("x/some.css");
		assertNotNull(r1);
		assertEquals("/s/x/some.css", r1.getPublicUrl());
	}

}
