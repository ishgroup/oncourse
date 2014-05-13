/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.http.*;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.CollectionResource;
import io.milton.resource.FolderResource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public abstract class DirectoryResource extends AbstractResource implements FolderResource {
	
	private String name;
	
	public DirectoryResource(String name, io.milton.http.SecurityManager securityManager) {
		super(securityManager);
		this.name = name;
	}

	@Override
	public void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return null;
	}

	@Override
	public String getContentType(String accepts) {
		return null;
	}

	@Override
	public Long getContentLength() {
		return null;
	}

	@Override
	public CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
		return null;
	}

	@Override
	public void moveTo(CollectionResource rDest, String name) throws ConflictException, NotAuthorizedException, BadRequestException {
	}

	@Override
	public Date getCreateDate() {
		return null;
	}

	@Override
	public String getUniqueId() {
		return name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Date getModifiedDate() {
		return null;
	}
	
}
