/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.scripting.api;

import com.google.inject.Inject;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import ish.oncourse.entity.services.ContactService;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.EmailTemplate;
import ish.oncourse.server.document.DocumentService;
import ish.util.DateFormatter;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.TimeZone;

public class TemplateService {

	private static final String RENDER_PLAIN_FUNCTION =
			"<% render = { tmpl, extraBindings = [:] -> extraBindings.putAll(bindings) \n Template.renderPlain(Template.loadTemplate(tmpl), extraBindings) } %>";
	private static final String RENDER_HTML_FUNCTION =
			"<% render = { tmpl, extraBindings = [:] -> extraBindings.putAll(bindings) \n Template.renderHtml(Template.loadTemplate(tmpl), extraBindings) } %>";

	public static final String LEGACY_PREFIX = "legacy.";
	public static final String BINDINGS = "bindings";
	public static final String SUBJECT = "subject";
	public static final String RECORD = "record";
	public static final String TO = "to";
	public static final String TEMPLATE_BINDING = "Template";
	public static final String COLLEGE_PREFERENCE_SERVICE = "Preference";
	public static final String PREFERENCES_BINDING = "Preferences";
	public static final String CONTACT_SERVICE_BINDING = "ContactService";
	public static final String DATE_FORMATTER = "DateFormatter";
	public static final String IMAGE = "image";
	private static final String JAVA_POUND = "\u00A3";
	private static final String HTML_POUND = "&pound;";
	private static final String JAVA_EURO = "\u20AC";
	private static final String HTML_EURO = "&euro;";

	private ICayenneService cayenneService;
	private CollegePreferenceService preferenceService;
	private ContactService contactService;
	private DocumentService documentService;

	private SimpleTemplateEngine templateEngine;

	@Inject
	public TemplateService(ICayenneService cayenneService,
						   CollegePreferenceService preferenceService,
						   ContactService contactService, DocumentService documentService) {
		this.cayenneService = cayenneService;
		this.templateEngine = new SimpleTemplateEngine();
		this.preferenceService = preferenceService;
		this.contactService = contactService;
		this.documentService = documentService;
	}

	public Template createHtmlTemplate(EmailTemplate template) {
		if (template.getBodyHtml() == null) {
			return null;
		}
		return createTemplate(prepareHtmlTemplate(template.getBodyHtml()));
	}

	public String renderHtml(EmailTemplate template, Map<String, Object> bindings) {
		if (template.getBodyHtml() == null) {
			return null;
		}
		template.getOptions().forEach(opt ->
				bindings.put(opt.getName(), opt.getValue())
		);
		Template htmlTemplate = createHtmlTemplate(template);
		var html = htmlTemplate.make(putBaseBindings(bindings)).toString();
		MetaclassCleaner.clearGroovyCache(htmlTemplate);
		
		return html
				.replaceAll(JAVA_POUND, HTML_POUND)
				.replaceAll(JAVA_EURO, HTML_EURO);
	}

	public Template createPlainTemplate(EmailTemplate template) {
		if (template.getBodyPlain() == null) {
			return null;
		}
		return createTemplate(preparePlainTemplate(template.getBodyPlain()));
	}

	public String renderPlain(EmailTemplate template, Map<String, Object> bindings ) {
		if (template.getBodyPlain() == null) {
			return null;
		}
		template.getOptions().forEach(opt ->
				bindings.put(opt.getName(), opt.getValue())
		);
		Template htmlTemplate = createPlainTemplate(template);
		String result =  htmlTemplate.make(putBaseBindings(bindings)).toString();
		MetaclassCleaner.clearGroovyCache(htmlTemplate);

		return result;
	}

	public Template createSubjectTemplate(EmailTemplate template) {
		if (template.getSubject() == null) {
			return null;
		}
		return createTemplate(template.getSubject());
	}

	public String addSubject(EmailTemplate template, Map<String, Object> plainBindings, Map<String, Object> htmlBindings) {
		Template subjectTemplate = createSubjectTemplate(template);
		if (subjectTemplate == null) {
			return null;
		}
		putBaseBindings(plainBindings);
		String subject = subjectTemplate.make(plainBindings).toString();
		MetaclassCleaner.clearGroovyCache(subjectTemplate);

		plainBindings.put(SUBJECT, subject);
		if (htmlBindings != null) {
			htmlBindings.put(SUBJECT, subject);
		}
		return subject;
	}

	public String renderSubject(String templateName, Map<String, Object> bindings) {
		var emailTemplate = loadTemplate(templateName);
		return addSubject(emailTemplate, bindings, null);
	}

	public String renderSubject(EmailTemplate template, Map<String, Object> bindings) {
		return createSubjectTemplate(template).make(putBaseBindings(bindings)).toString();
	}

	public String renderTemplate(String template, Map<String, Object> bindings) {
		return createTemplate(template).make(putBaseBindings(bindings)).toString();
	}

	public EmailTemplate loadTemplate(String search) {
		ObjectContext context = cayenneService.getNewContext();
		return ObjectSelect.query(EmailTemplate.class)
				.where(EmailTemplate.KEY_CODE.eq(search))
				.or(EmailTemplate.NAME.eq(search).andExp(EmailTemplate.KEY_CODE.startsWith(LEGACY_PREFIX))).selectOne(context);
	}

	public Map<String, Object> putBaseBindings(Map<String, Object> bindings) {
		bindings.put(TEMPLATE_BINDING, this);
		bindings.put(PREFERENCES_BINDING, preferenceService);
		bindings.put(CONTACT_SERVICE_BINDING, contactService);
		bindings.put(IMAGE, documentService.getImageClosure());
		bindings.put(CollegePreferenceService.PREFERENCE_ALIAS, preferenceService.getPrefHelper());
		bindings.put(COLLEGE_PREFERENCE_SERVICE, preferenceService);
		bindings.put(DATE_FORMATTER, new DateFormatter(TimeZone.getDefault()));
		bindings.put(BINDINGS, bindings);

		return bindings;
	}

	private Template createTemplate(String template) {
		try {
			return templateEngine.createTemplate(template);
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException("Can't create template.", e);
		}
	}

	private String preparePlainTemplate(String templateBody) {
		// TODO: probably we can come up with something more nice to inject
		// custom render function definition into templates

		return RENDER_PLAIN_FUNCTION + templateBody;
	}

	private String prepareHtmlTemplate(String templateBody) {
		// TODO: probably we can come up with something more nice to inject
		// custom render function definition into templates

		return RENDER_HTML_FUNCTION + templateBody;
	}
}
