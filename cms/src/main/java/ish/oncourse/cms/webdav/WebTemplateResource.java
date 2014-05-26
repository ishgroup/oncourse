/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.common.ContentTypeUtils;
import io.milton.common.Path;
import io.milton.http.*;
import io.milton.http.SecurityManager;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.*;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebTemplate;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

public class WebTemplateResource extends AbstractResource implements CopyableResource, DeletableResource, GetableResource, MoveableResource, PropFindableResource, ReplaceableResource {

	private WebTemplate webTemplate;
	private ICayenneService cayenneService;
	
	private WebSiteLayout layout;
	private String templateName;
	
	public WebTemplateResource(String templateName, WebSiteLayout layout, ICayenneService cayenneService, SecurityManager securityManager) {
		super(securityManager);
		
		this.templateName = templateName;
		this.layout = layout;
		this.cayenneService = cayenneService;
		
		this.webTemplate = getTemplateByName(getName(), layout);
	}

	private WebTemplate getTemplateByName(String name, WebSiteLayout layout) {
		ObjectContext context = cayenneService.newContext();

		SelectQuery query = new SelectQuery(WebTemplate.class);

		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.LAYOUT_PROPERTY, layout));
		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.NAME_PROPERTY, name));

		return (WebTemplate) Cayenne.objectForQuery(context, query);
	}
	
	@Override
	public void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		if (webTemplate != null) {
			ObjectContext context = cayenneService.newContext();
			WebTemplate templateToDelete = context.localObject(webTemplate);
			context.deleteObjects(templateToDelete);
			context.commitChanges();
		}
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
		if (webTemplate != null) {
			out.write(webTemplate.getContent().getBytes());
		} else {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateName);
			IOUtils.copy(is, out);
		}
		out.flush();
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return null;
	}

	@Override
	public String getContentType(String accepts) {
		return ContentTypeUtils.findAcceptableContentType("text/html", accepts);
	}

	@Override
	public Long getContentLength() {
		if (webTemplate != null) {
			return (long) webTemplate.getContent().length();
		}
		
		return null;
	}

	@Override
	public void moveTo(CollectionResource rDest, String name) throws ConflictException, NotAuthorizedException, BadRequestException {
	}

	@Override
	public Date getCreateDate() {
		if (webTemplate != null) {
			return webTemplate.getCreated();
		}
		
		return null;
	}

	@Override
	public String getName() {
		return Path.path(templateName).getName();
	}

	@Override
	public Date getModifiedDate() {
		if (webTemplate != null) {
			return webTemplate.getModified();
		}
		
		return null;
	}

	@Override
	public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {

		
			try {
				ObjectContext context = cayenneService.newContext();

				WebTemplate template;
				
				if (webTemplate != null) {
					template = context.localObject(webTemplate);
				} else {
					template = createNewTemplate(context, layout, getName());
				}

				StringWriter writer = new StringWriter();
				IOUtils.copy(in, writer);

				template.setContent(writer.toString());

				context.commitChanges();
			} catch (Exception e) {
				throw new BadRequestException("Can't replace template's content.", e);
			}
	}

	private WebTemplate createNewTemplate(ObjectContext context, WebSiteLayout layout, String name) {
		WebSiteLayout localLayout = context.localObject(layout);

		WebTemplate template = context.newObject(WebTemplate.class);
		template.setName(name);
		template.setLayout(localLayout);
		
		return template;
	}

	@Override
	public String getUniqueId() {
		return null;
	}
}
