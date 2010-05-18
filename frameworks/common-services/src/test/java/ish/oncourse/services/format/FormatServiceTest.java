package ish.oncourse.services.format;

import ish.oncourse.services.format.FormatName;
import ish.oncourse.services.format.FormatService;
import java.text.Format;

import org.junit.Assert;
import org.junit.Test;

public class FormatServiceTest extends Assert {

	@Test
	public void formatter() {
		FormatService service = new FormatService();

		Format format = service.formatter(FormatName.TO_STRING);
		assertNotNull(format);

		Object object = new Object();
		assertEquals(object.toString(), format.format(object));
	}

	@Test
	public void format() {
		FormatService service = new FormatService();

		Object object = new Object();
		assertEquals(object.toString(), service.format(object,
				FormatName.TO_STRING));
	}
}
