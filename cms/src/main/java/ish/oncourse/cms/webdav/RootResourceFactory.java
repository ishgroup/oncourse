/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.common.Path;
import io.milton.http.*;
import io.milton.http.SecurityManager;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.fs.*;
import io.milton.resource.Resource;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.ContextUtil;
import org.apache.tapestry5.ioc.Registry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RootResourceFactory implements ResourceFactory {
	
	private static final String WEBDAV_PATH_PREFIX = "/cms/webdav";
	
	private static final String BLOCKS = "blocks";
	private static final String PAGES = "pages";
	private static final String S = "s";
	private static final String TEMPLATES = "templates";
	
	private static final String[] WEBDAV_DIRS = new String[] { BLOCKS, PAGES, S, TEMPLATES };
	
	private IWebSiteService webSiteService;
	
	private Registry registry;
	
	private BlockResourceFactory blockResourceFactory;
	private PageResourceFactory pageResourceFactory;
	private TemplateResourceFactory templateResourceFactory;
	
	private FileContentService fileContentService;
	private SecurityManager securityManager;
	
	private String sRoot;
	
	public RootResourceFactory(Registry registry, io.milton.http.SecurityManager securityManager, FileContentService fileContentService) {
		this.registry = registry;
		this.securityManager = securityManager;
		
		this.webSiteService = registry.getService(IWebSiteService.class);
		
		this.blockResourceFactory = registry.autobuild(BlockResourceFactory.class);
		this.pageResourceFactory = registry.autobuild(PageResourceFactory.class);
		this.templateResourceFactory = registry.autobuild(TemplateResourceFactory.class);
		
		this.blockResourceFactory.setSecurityManager(securityManager);
		this.pageResourceFactory.setSecurityManager(securityManager);
		this.templateResourceFactory.setSecurityManager(securityManager);
		
		this.templateResourceFactory.initDefaultResources();
		
		this.fileContentService = fileContentService;
		
		this.sRoot = ContextUtil.getSRoot();
	}

	@Override
	public Resource getResource(final String host, String url) throws NotAuthorizedException, BadRequestException {

		Path rawPath = Path.path(url);
		
		if (rawPath.toPath().startsWith(WEBDAV_PATH_PREFIX)) {
			final Path path = rawPath.getStripFirst().getStripFirst();
			
			if (path.isRoot()) {
				return new DirectoryResource("webdav", securityManager) {
					@Override
					public Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
						return null;
					}

					@Override
					public Resource child(String childName) throws NotAuthorizedException, BadRequestException {
						return getDirectoryByName(childName, host, path.getStripFirst().toPath());
					}

					@Override
					public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
						List<Resource> resources = new ArrayList<>();
						
						for (String resName : WEBDAV_DIRS) {
							resources.add(getDirectoryByName(resName, host, path.getStripFirst().toPath()));
						}
						
						return resources;
					}
				};
			}
			
			return getDirectoryByName(path.getFirst(), host, path.getStripFirst().toPath());
		}
		
		return null;
	}
	
	private Resource getDirectoryByName(String name, String host, String url) throws NotAuthorizedException, BadRequestException {
		switch (name) {
			case PAGES:
				return pageResourceFactory.getResource(host, url);
			case BLOCKS:
				return blockResourceFactory.getResource(host, url);
			case TEMPLATES:
				return templateResourceFactory.getResource(host, url);
			default:
				if (sRoot == null) {
					return null;
				}
				
				String siteKey = webSiteService.getCurrentWebSite().getSiteKey();
				String rootDirName = String.format("%s/%s", sRoot, siteKey);

				FileSystemResourceFactory fsResourceFactory = new FileSystemResourceFactory(new File(rootDirName), this.securityManager, sRoot);
				fsResourceFactory.setContentService(fileContentService);
				
				return fsResourceFactory.getResource(host,  url);
		}
	}
	
}
