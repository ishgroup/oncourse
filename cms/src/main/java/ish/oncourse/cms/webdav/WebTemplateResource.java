/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.common.ContentTypeUtils;
import io.milton.http.*;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.*;
import ish.oncourse.model.WebTemplate;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
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

	public WebTemplateResource(WebTemplate webTemplate, ICayenneService cayenneService, io.milton.http.SecurityManager securityManager) {
		super(securityManager);

		this.webTemplate = webTemplate;
		this.cayenneService = cayenneService;
	}

	@Override
	public void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		ObjectContext context = cayenneService.newContext();

		WebTemplate template = context.localObject(webTemplate);
		context.deleteObjects(template);

		context.commitChanges();
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
		out.write(webTemplate.getContent().getBytes());
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
		return (long) webTemplate.getContent().length();
	}

	@Override
	public void moveTo(CollectionResource rDest, String name) throws ConflictException, NotAuthorizedException, BadRequestException {
		ObjectContext context = cayenneService.newContext();

		WebTemplate template = context.localObject(webTemplate);
		template.setName(name);

		context.commitChanges();
	}

	@Override
	public Date getCreateDate() {
		return webTemplate.getCreated();
	}

	@Override
	public String getName() {
		return webTemplate.getName();
	}

	@Override
	public Date getModifiedDate() {
		return webTemplate.getModified();
	}

	@Override
	public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {

		try {
			ObjectContext context = cayenneService.newContext();

			WebTemplate template = context.localObject(webTemplate);

			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer);

			template.setContent(writer.toString());

			context.commitChanges();
		} catch (Exception e) {
			throw new BadRequestException("Can't replace block content.", e);
		}
	}

	@Override
	public String getUniqueId() {
		return webTemplate.getObjectId().toString();
	}
}
