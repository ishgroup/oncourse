/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.common.*;
import io.milton.http.*;
import io.milton.http.SecurityManager;
import io.milton.http.exceptions.*;
import io.milton.resource.*;
import ish.oncourse.model.WebContent;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Date;
import java.util.Map;

public class WebContentResource extends AbstractResource implements CopyableResource, DeletableResource, GetableResource, MoveableResource, PropFindableResource, ReplaceableResource {
	
	private WebContent webContent;
	private ICayenneService cayenneService;
	
	private String displayName = null;
	
	public WebContentResource(WebContent webContent, ICayenneService cayenneService, SecurityManager securityManager) {
		super(securityManager);
		
		this.webContent = webContent;
		this.cayenneService = cayenneService;
	}

	public WebContentResource(WebContent webContent, String displayName, ICayenneService cayenneService, SecurityManager securityManager) {
		this(webContent, cayenneService, securityManager);
		
		this.displayName = displayName;
	}

	@Override
	public void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		ObjectContext context = cayenneService.newContext();
		
		WebContent block = context.localObject(webContent);
		context.deleteObjects(block);

		context.commitChanges();
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
		out.write(webContent.getContent().getBytes());
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
		return (long) webContent.getContent().length();
	}

	@Override
	public void moveTo(CollectionResource rDest, String name) throws ConflictException, NotAuthorizedException, BadRequestException {
	}

	@Override
	public Date getCreateDate() {
		return webContent.getCreated();
	}

	@Override
	public String getName() {
		return displayName != null ? displayName : webContent.getName();
	}

	@Override
	public Date getModifiedDate() {
		return webContent.getModified();
	}

	@Override
	public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
		
		try {
			ObjectContext context = cayenneService.newContext();

			WebContent block = context.localObject(webContent);

			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer);
			
			block.setContent(writer.toString());

			context.commitChanges();
		} catch (Exception e) {
			throw new BadRequestException("Can't replace block content.", e);
		}
	}

	@Override
	public String getUniqueId() {
		return webContent.getObjectId().toString();
	}
}
