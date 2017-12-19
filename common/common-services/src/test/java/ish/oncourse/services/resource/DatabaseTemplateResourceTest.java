/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.resource;

import ish.oncourse.model.WebTemplate;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * User: akoiro
 * Date: 19/12/17
 */
public class DatabaseTemplateResourceTest {

	@Test
	public void test() throws Exception {

		WebTemplate template = Mockito.mock(WebTemplate.class);
		Mockito.when(template.getContent()).thenReturn(
				IOUtils.toString(Objects.requireNonNull(
						DatabaseTemplateResourceTest.class.getClassLoader().getResource("ish/oncourse/services/resource/DatabaseTemplateResource.tml")),
						Charset.defaultCharset()));

		DatabaseTemplateResource databaseTemplateResource = new DatabaseTemplateResource(template);

		XMLReader reader = XMLReaderFactory.createXMLReader();
		InputStream stream = databaseTemplateResource.openStream();
		reader.parse(new InputSource(stream));
		stream.close();
	}

}
