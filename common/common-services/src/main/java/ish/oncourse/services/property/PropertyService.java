package ish.oncourse.services.property;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyService implements IPropertyService {

	private static final String RUNTIME_CONFIG_PROPERTY = "runtime.config.name";

	private Map<String, String> properties;

	public PropertyService() throws IOException {
		this(System.getProperty(RUNTIME_CONFIG_PROPERTY));
	}

	PropertyService(String runtimeConfig) throws IOException {

		properties = new HashMap<>();

		String propertyFileResource = runtimeConfig != null ? "properties/runtime."
				+ runtimeConfig + ".properties"
				: "properties/runtime.properties";

		Enumeration<URL> propertyFiles = Thread.currentThread()
				.getContextClassLoader().getResources(propertyFileResource);

		while (propertyFiles.hasMoreElements()) {
			URL url = propertyFiles.nextElement();

			Properties nextProperties = new Properties();
			try (InputStream in = url.openStream()) {
				nextProperties.load(in);
			}

			for (Map.Entry<Object, Object> entry : nextProperties.entrySet()) {
				properties.put((String) entry.getKey(), (String) entry
						.getValue());
			}
		}
	}

	public String string(Property property) {
		if (property == null) {
			throw new NullPointerException("Null property");
		}

		return properties.get(property.value());
	}

}
