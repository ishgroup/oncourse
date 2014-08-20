/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.webdav;

import io.milton.common.Path;
import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.ResourceFactory;
import io.milton.http.SecurityManager;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.Resource;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.textile.ITextileConverter;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class PageResourceFactory implements ResourceFactory {

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
	
	@Inject
	private ITextileConverter textileConverter;
	
	private SecurityManager securityManager;
	
	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}
	
	@Override
	public Resource getResource(String host, String url) throws NotAuthorizedException, BadRequestException {

		Path path = Path.path(url);
		
		if (path.isRoot()) {
			return new DirectoryResource(TopLevelDir.pages.name(), securityManager) {
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

                @Override
                public boolean authorise(Request request, Request.Method method, Auth auth) {
                    return super.authorise(request,method,auth) && ArrayUtils.contains(TopLevelDir.pages.getAllowedMethods(), method);
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
			pages.add(new WebNodeResource(page, cayenneService, webNodeService, textileConverter, securityManager));
		}

		return pages;
	}

	public WebNodeResource getWebNodeResource(String name) {
		
		WebNode page = webNodeService.getNodeForName(name);
		
		if (page == null) {
			return null;
		}
		
		return new WebNodeResource(page, cayenneService, webNodeService, textileConverter, securityManager);
	}

	public WebNodeResource changePage(WebNode pageToChange, String name, String content) {

		ObjectContext context = cayenneService.newContext();

		WebNode page = context.localObject(pageToChange);
		page.setName(name);
		
		WebContent block = page.getWebContentVisibility().get(0).getWebContent();
		block.setContentTextile(content);
		block.setContent(textileConverter.convertCoreTextile(content));

		context.commitChanges();

		return new WebNodeResource(page, cayenneService, webNodeService, textileConverter, securityManager);
	}

	public WebNodeResource createNewPage(String name, String content) {

		ObjectContext ctx = cayenneService.newContext();

		WebSiteVersion webSiteVersion = ctx.localObject(webSiteVersionService.getCurrentVersion());
		WebNodeType webNodeType = ctx.localObject(webNodeTypeService.getDefaultWebNodeType());
		
		WebNode webNode = webNodeService.createNewNodeBy(webSiteVersion, webNodeType, name, content, webNodeService.getNextNodeNumber());
		
		ctx.commitChanges();

		return new WebNodeResource(webNode, cayenneService, webNodeService, textileConverter, securityManager);
	}
}
