/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.parser.TemplateToken;
import org.apache.tapestry5.internal.services.TemplateParser;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.Resource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: akoiro
 * Date: 21/11/17
 */
public class ParseTemplate {

	private final ComponentTemplate missingTemplate = new ComponentTemplate() {
		public Map<String, Location> getComponentIds() {
			return Collections.emptyMap();
		}

		public Resource getResource() {
			return null;
		}

		public List<TemplateToken> getTokens() {
			return Collections.emptyList();
		}

		public boolean isMissing() {
			return true;
		}

		public List<TemplateToken> getExtensionPointTokens(String extensionPointId) {
			return null;
		}

		public boolean isExtension() {
			return false;
		}

		public boolean usesStrictMixinParameters() {
			return false;
		}
	};

	private TemplateParser parser;
	private Resource resource;


	public ComponentTemplate parse() {
		ComponentTemplate result = missingTemplate;
		if (resource.exists()) {
			result = parser.parseTemplate(resource);
		}
		return result;
	}

	public ParseTemplate parser(TemplateParser parser) {
		this.parser = parser;
		return this;
	}

	public ParseTemplate resource(Resource resource) {
		this.resource = resource;
		return this;
	}

	public static ParseTemplate instance() {
		return new ParseTemplate();
	}
}
