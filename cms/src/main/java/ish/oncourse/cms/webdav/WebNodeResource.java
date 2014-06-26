/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.common.ContentTypeUtils;
import io.milton.http.Auth;
import io.milton.http.Range;
import io.milton.http.SecurityManager;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.*;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.textile.ITextileConverter;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

public class WebNodeResource extends AbstractResource implements CopyableResource, DeletableResource, GetableResource, MoveableResource, PropFindableResource, ReplaceableResource {

	private WebNode webNode;
	
	private ICayenneService cayenneService;
	private IWebNodeService webNodeService;
	private ITextileConverter textileConverter;

	public WebNodeResource(WebNode webNode, ICayenneService cayenneService, IWebNodeService webNodeService, ITextileConverter textileConverter, SecurityManager securityManager) {
		super(securityManager);

		this.webNode = webNode;
		
		this.cayenneService = cayenneService;
		this.webNodeService = webNodeService;
		this.textileConverter = textileConverter;
	}
	
	private WebContent getWebContent() {
		return webNode.getWebContentVisibility().get(0).getWebContent();
	}

	@Override
	public void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		// delete does nothing due to the Cyberduck way of inline editing, see moveTo method for details
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
		String content = getWebContent().getContentTextile();
		
		if (content != null) {
			out.write(content.getBytes());
			out.flush();
		}
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
		String content = getWebContent().getContentTextile();
		
		if (content == null) {
			return 0l;
		}

		return (long) content.length();
	}

	@Override
	public void moveTo(CollectionResource rDest, String newName) throws ConflictException, NotAuthorizedException, BadRequestException {

		// this logic is a bit tricky and is there for the purpose of working around odd Cyberduck behavior when
		// editing records. When editing record Cyberduck's actions are:
		//
		//		1. create new block/page with changed content and temporary name
		//		2. delete existing block/page
		//		3. rename new block/page to its real name
		//
		// To avoid losing block/page relationships and other non WebDAV editable fields during the step 2
		// we do the following:
		//
		// 		- if the rename does not overwrite anything, then just perform the rename of the name 
		// 		  of the record in the db leaving all relations intact
		// 		- if the rename overwrites something, then just copy the content and delete the old record permanently
		
		ObjectContext context = cayenneService.newContext();

		WebNode existingNode = webNodeService.getNodeForName(newName);

		// if there is no existing record with such name and we are not renaming current record to the same name
		// then just change name of the page
		// otherwise - replace content of existing record with the new one and delete the new record
		
		if (existingNode == null || existingNode.getObjectId().equals(webNode.getObjectId())) {
			WebNode localNode = context.localObject(webNode);
			localNode.setName(newName);
		} else {
			WebNode localNode = context.localObject(existingNode);
			WebContent webContent = localNode.getWebContentVisibility().get(0).getWebContent();
			
			webContent.setContentTextile(getWebContent().getContentTextile());
			webContent.setContent(getWebContent().getContent());
			
			context.deleteObjects(context.localObject(webNode));
		}
		
		context.commitChanges();
	}

	@Override
	public Date getCreateDate() {
		return webNode.getCreated();
	}

	@Override
	public String getName() {
		return webNode.getName();
	}

	@Override
	public Date getModifiedDate() {
		return webNode.getModified();
	}

	@Override
	public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
		try {
			ObjectContext context = cayenneService.newContext();

			WebContent block = context.localObject(getWebContent());

			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer);

            String contentTextile = writer.toString();
			block.setContentTextile(contentTextile);
			block.setContent(textileConverter.convertCoreTextile(contentTextile));

			context.commitChanges();
		} catch (Exception e) {
			throw new BadRequestException("Can't replace block content.", e);
		}
	}

	@Override
	public String getUniqueId() {
		return null;
	}
}
