package ish.oncourse.services.format;

import org.apache.commons.collections.Factory;

import java.text.Format;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class FormatService implements IFormatService {

	// storing formatters map in a ThreadLocal as this is a singleton service
	// and all JDK formatters are not thread-safe
	private final ThreadLocal<Map<FormatName, Format>> formatters;

	public FormatService() {
		formatters = new ThreadLocal<>();
	}

	public String format(Object object, FormatName formatName) {
		if (object == null) {
			return "";
		}

		return formatter(formatName).format(object);
	}

	public Format formatter(FormatName formatName) {

		if (formatName == null) {
			throw new NullPointerException("Null formatName");
		}

		Map<FormatName, Format> formattersMap = formatters.get();
		if (formattersMap == null) {
			formattersMap = new HashMap<>();
			formatters.set(formattersMap);
		}

		Format format = formattersMap.get(formatName);

		if (format == null) {

			ResourceBundle formatBundle = ResourceBundle
					.getBundle(FormatBundle.class.getName());

			Factory formatFactory = (Factory) formatBundle.getObject(formatName
					.name());

			format = (Format) formatFactory.create();

			if (format == null) {
				throw new IllegalArgumentException("Unsupported format name: "
						+ formatName);
			}

			// cache format
			formattersMap.put(formatName, format);
		}

		return format;
	}
}
