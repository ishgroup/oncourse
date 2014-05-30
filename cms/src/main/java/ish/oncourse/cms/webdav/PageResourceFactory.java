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
import io.milton.resource.Resource;
import ish.oncourse.model.*;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class PageResourceFactory implements ResourceFactory {

	private static final String PAGE_DIR_NAME = "pages";
	
	@Inject
	private IWebNodeService webNodeService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;
	
	@Inject
	private IWebNodeTypeService webNodeTypeService;
	
	private SecurityManager securityManager;
	
	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}
	
	@Override
	public Resource getResource(String host, String url) throws NotAuthorizedException, BadRequestException {

		Path path = Path.path(url);
		
		if (path.isRoot()) {
			return new DirectoryResource(PAGE_DIR_NAME, securityManager) {
				@Override
				public Resource child(String childName) throws NotAuthorizedException, BadRequestException {
					return getWebNodeResource(childName);
				}

				@Override
				public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
					return listPages();
				}

				@Override
				public Resource createNew(String newName, InputStream inputStream, Long length, String contentType)
						throws IOException, ConflictException, NotAuthorizedException, BadRequestException {

					StringWriter writer = new StringWriter();
					IOUtils.copy(inputStream, writer);

					String content = writer.toString();

					// check if there is an existing page with similar name
					WebNode page = webNodeService.getNodeForName(newName);

					if (page != null) {
						return changePage(page, newName, content);
					}

					return createNewPage(newName, content);
				}
			};
		} else if (path.getLength() == 1) {
			String name = path.getName();

			return getWebNodeResource(name);
		}
		
		return null;
	}

	public List<WebNodeResource> listPages() {
		List<WebNodeResource> pages = new ArrayList<>();

		for (WebNode page : webNodeService.getNodes()) {
			pages.add(new WebNodeResource(page, cayenneService, webNodeService, securityManager));
		}

		return pages;
	}

	public WebNodeResource getWebNodeResource(String name) {
		
		WebNode page = webNodeService.getNodeForName(name);
		
		if (page == null) {
			return null;
		}
		
		return new WebNodeResource(page, cayenneService, webNodeService, securityManager);
	}

	public WebNodeResource changePage(WebNode pageToChange, String name, String content) {

		ObjectContext context = cayenneService.newContext();

		WebNode page = context.localObject(pageToChange);
		page.setName(name);
		
		WebContent block = page.getWebContentVisibility().get(0).getWebContent();
		block.setContent(content);

		context.commitChanges();

		return new WebNodeResource(page, cayenneService, webNodeService, securityManager);
	}

	public WebNodeResource createNewPage(String name, String content) {

		ObjectContext ctx = cayenneService.newContext();

		WebSiteVersion webSiteVersion = ctx.localObject(webSiteVersionService.getCurrentVersion(webSiteService.getCurrentWebSite()));
		WebNodeType webNodeType = ctx.localObject(webNodeTypeService.getDefaultWebNodeType());
		
		WebNode webNode = webNodeService.createNewNodeBy(webSiteVersion, webNodeType, name, content, webNodeService.getNextNodeNumber());
		
		ctx.commitChanges();

		return new WebNodeResource(webNode, cayenneService, webNodeService, securityManager);
	}
}
