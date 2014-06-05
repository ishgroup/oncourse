/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.http.*;
import io.milton.http.SecurityManager;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.fs.*;
import io.milton.resource.CollectionResource;
import io.milton.resource.Resource;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.ContextUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StaticResourceFactory implements ResourceFactory {
	
	private static final Logger logger = Logger.getLogger(StaticResourceFactory.class);
	
	private IWebSiteService webSiteService;
	private IAuthenticationService authenticationService;
	
	private FileSystemResourceFactory fsResourceFactory;
	private String sRoot;
	
	public StaticResourceFactory(String sRoot, IWebSiteService webSiteService, 
								 IAuthenticationService authenticationService, SecurityManager securityManager) {
		this.webSiteService = webSiteService;
		this.authenticationService = authenticationService;
		this.sRoot = sRoot;
		
		this.fsResourceFactory = new FileSystemResourceFactory(new File(sRoot), securityManager, sRoot);
	}

	@Override
	public Resource getResource(String host, String path) throws NotAuthorizedException, BadRequestException {
		String siteKey = webSiteService.getCurrentWebSite().getSiteKey();
		String rootDirName = String.format("%s/%s", sRoot, siteKey);
		
		fsResourceFactory.setRoot(new File(rootDirName));
		
		return getFsResource(host, path);
	}

	/**
	 * Executes editFile.sh script passing specified file as a parameter.
	 * E.g.:
	 * 		/var/onCourse/scripts/editFile.sh -p {file.getAbsolutePath()}	
	 */
	private void executeEditFileScript(File file) {
		String scriptPath = ContextUtil.getCmsEditScriptPath();

		if (StringUtils.trimToNull(scriptPath) == null) {
			logger.error("Edit file script is not defined! Resources haven't been updated!");
			return;
		}

		List<String> scriptCommand = new ArrayList<>();
		
		scriptCommand.add(scriptPath);
		scriptCommand.add("-p");
		scriptCommand.add(String.format("\"%s\"", file.getAbsolutePath()));
		
		String userEmail = authenticationService.getUserEmail();
		
		if (userEmail != null) {
			scriptCommand.add("-e");
			scriptCommand.add(userEmail);
		}

		ProcessBuilder processBuilder = new ProcessBuilder(scriptCommand);

		try {
			processBuilder.start();
		} catch (Exception e) {
			logger.error(String.format("Error executing script '%s'", scriptPath), e);
		}
	}
	
	private Resource getFsResource(String host, String url) {
		url = stripContext(url);
		File requested = fsResourceFactory.resolvePath(fsResourceFactory.getRoot(), url);
		return resolveFile(host, requested);
	}

	public FsResource resolveFile(String host, File file) {
		FsResource r;
		if (!file.exists()) {
			return null;
		} else if (file.isDirectory()) {
			r = new CmsFsDirectoryResource(host, fsResourceFactory, file, fsResourceFactory.getContentService());
		} else {
			r = new CmsFsFileResource(host, fsResourceFactory, file, fsResourceFactory.getContentService());
		}
		
		return r;
	}

	private String stripContext(String url) {
		String contextPath = fsResourceFactory.getContextPath();
		if (contextPath != null && contextPath.length() > 0) {
			url = url.replaceFirst('/' + contextPath, "");
			return url;
		} else {
			return url;
		}
	}
	
	private class CmsFsFileResource extends FsFileResource {
		
		public CmsFsFileResource(String host, FileSystemResourceFactory factory, File file, FileContentService contentService) {
			super(host, factory, file, contentService);
		}

		@Override
		public void moveTo(CollectionResource newParent, String newName) {
			super.moveTo(newParent, newName);
			
			executeEditFileScript(getFile());
		}

		@Override
		public void replaceContent(InputStream in, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
			super.replaceContent(in, length);
			
			executeEditFileScript(getFile());
		}

		@Override
		protected void doCopy(File dest) {
			super.doCopy(dest);
			
			executeEditFileScript(dest);
		}
	}
	
	private class CmsFsDirectoryResource extends FsDirectoryResource {
		
		public CmsFsDirectoryResource(String host, FileSystemResourceFactory factory, File dir, FileContentService contentService) {
			super(host, factory, dir, contentService);
		}

		@Override
		public Resource createNew(String name, InputStream in, Long length, String contentType) throws IOException {
			FsResource resource = (FsResource) super.createNew(name, in, length, contentType);
			
			executeEditFileScript(resource.getFile());
			
			return resource;
		}
	}
}
