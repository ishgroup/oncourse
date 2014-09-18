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
import ish.oncourse.services.content.IWebContentService;
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

public class WebContentResource extends AbstractResource implements CopyableResource, DeletableResource, GetableResource, MoveableResource, PropFindableResource, ReplaceableResource {
	
	private WebContent webContent;
	
	private ICayenneService cayenneService;
	private IWebContentService webContentService;
	private ITextileConverter textileConverter;
	
	public WebContentResource(WebContent webContent, ICayenneService cayenneService, IWebContentService webContentService, 
							  ITextileConverter textileConverter, SecurityManager securityManager) {
		super(securityManager);
		
		this.webContent = webContent;
		this.cayenneService = cayenneService;
		this.webContentService = webContentService;
		this.textileConverter = textileConverter;
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
		if (webContent.getContentTextile() != null) {
			out.write(webContent.getContentTextile().getBytes());
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
		if (webContent.getContentTextile() == null) {
			return 0l;
		}

        //we should retrun amount of bytes (not chars)
        return (long) webContent.getContentTextile().getBytes().length;
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

		WebContent existingBlock = webContentService.getBlockByName(newName);

		// if there is no existing record with such name and we are not renaming current record to the same name
		// then just change name of the block
		// otherwise - replace content of existing record with the new one and delete the new record
		
		if (existingBlock == null || existingBlock.getObjectId().equals(webContent.getObjectId())) {
			WebContent localBlock = context.localObject(webContent);
			localBlock.setName(newName);
		} else {
			WebContent localBlock = context.localObject(existingBlock);
			localBlock.setContentTextile(webContent.getContentTextile());
			localBlock.setContent(webContent.getContent());

			context.deleteObjects(context.localObject(webContent));
		}

		context.commitChanges();
	}

	@Override
	public Date getCreateDate() {
		return webContent.getCreated();
	}

	@Override
	public String getName() {
		return webContent.getName();
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
			
			block.setContentTextile(writer.toString());
			block.setContent(textileConverter.convertCoreTextile(block.getContentTextile()));

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