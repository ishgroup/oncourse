/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.resource;

import ish.oncourse.model.WebTemplate;
import org.apache.tapestry5.ioc.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

/**
 * Database template resource implementation. Since it is backed by db storage 
 * url, file, path etc. getters don't make sense and will return null if called.
 */
public class DatabaseTemplateResource implements org.apache.tapestry5.ioc.Resource {

	private WebTemplate template;

	public DatabaseTemplateResource(WebTemplate template) {
		this.template = template;
	}

	public WebTemplate getTemplate() {
		return template;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public InputStream openStream() throws IOException {
		return new ByteArrayInputStream(template.getContent().getBytes());
	}

	@Override
	public URL toURL() {
		return null;
	}

	@Override
	public org.apache.tapestry5.ioc.Resource forLocale(Locale locale) {
		return null;
	}

	@Override
	public org.apache.tapestry5.ioc.Resource forFile(String relativePath) {
		return null;
	}

	@Override
	public Resource withExtension(String extension) {
		return null;
	}

	@Override
	public String getFolder() {
		return null;
	}

	@Override
	public String getFile() {
		return null;
	}

	@Override
	public String getPath() {
		return null;
	}
}
