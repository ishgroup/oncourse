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
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class TemplateResourceFactory implements ResourceFactory {
	
	private static final String TEMPLATE_DIR_NAME = "templates";

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IWebSiteVersionService webSiteVersionService;

	private io.milton.http.SecurityManager securityManager;

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	@Override
	public Resource getResource(String host, String url) throws NotAuthorizedException, BadRequestException {

		Path path = Path.path(url);

		if (path.isRoot()) {
			return new DirectoryResource(TEMPLATE_DIR_NAME, securityManager) {
				@Override
				public Resource child(String childName) throws NotAuthorizedException, BadRequestException {
					return getLayoutResourceByName(childName);
				}

				@Override
				public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
					return listLayouts();
				}

				@Override
				public Resource createNew(String newName, InputStream inputStream, Long length, String contentType)
						throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
					return null;
				}
			};
		} else if (path.getLength() == 1) {
			String name = path.getName();

			return getLayoutResourceByName(name);
		} else {
			String layoutKey = path.getFirst();
			String templateName = path.getStripFirst().getName();
			
			return getTemplateResourceByName(templateName, getLayoutByName(layoutKey));
		}
	}
	
	public List<DirectoryResource> listLayouts() {
		ObjectContext context = cayenneService.newContext();

		SelectQuery query = new SelectQuery(WebSiteLayout.class);
		
		query.andQualifier(ExpressionFactory.matchExp(WebSiteLayout.WEB_SITE_VERSION_PROPERTY, 
				webSiteVersionService.getCurrentVersion(webSiteService.getCurrentWebSite())));
		
		List<DirectoryResource> directoryResources = new ArrayList<>();
		
		List<WebSiteLayout> layouts = context.performQuery(query);
		
		for (WebSiteLayout layout : layouts) {
			directoryResources.add(new LayoutDirectoryResource(layout.getLayoutKey(), layout, securityManager));
		}
		
		return directoryResources;
	}
	
	public List<WebTemplateResource> listTemplates(WebSiteLayout layout) {
		List<WebTemplateResource> templates = new ArrayList<>();
		
		for (WebTemplate template : layout.getTemplates()) {
			templates.add(new WebTemplateResource(template, cayenneService, securityManager));
		}
		
		return templates;
	}
	
	public DirectoryResource getLayoutResourceByName(String name) {
		WebSiteLayout layout = getLayoutByName(name);
		
		return layout != null ? new LayoutDirectoryResource(layout.getLayoutKey(), layout, securityManager) : null;
	}
	
	public WebSiteLayout getLayoutByName(String name) {
		ObjectContext context = cayenneService.newContext();

		SelectQuery query = new SelectQuery(WebSiteLayout.class);

		query.andQualifier(ExpressionFactory.matchExp(WebSiteLayout.WEB_SITE_VERSION_PROPERTY,
				webSiteVersionService.getCurrentVersion(webSiteService.getCurrentWebSite())));
		query.andQualifier(ExpressionFactory.matchExp(WebSiteLayout.LAYOUT_KEY_PROPERTY, name));

		return  (WebSiteLayout) Cayenne.objectForQuery(context, query);
	}
	
	public WebTemplateResource getTemplateResourceByName(String name, WebSiteLayout layout) {
		WebTemplate template = getTemplateByName(name, layout);
		
		return template != null ? new WebTemplateResource(template, cayenneService, securityManager) : null;
	}
	
	public WebTemplate getTemplateByName(String name, WebSiteLayout layout) {
		ObjectContext context = cayenneService.newContext();

		SelectQuery query = new SelectQuery(WebTemplate.class);

		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.LAYOUT_PROPERTY, layout));
		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.NAME_PROPERTY, name));

		return (WebTemplate) Cayenne.objectForQuery(context, query);
	}
	
	public WebTemplateResource changeTemplate(WebTemplate templateToChange, String name, String content) {

		ObjectContext context = cayenneService.newContext();

		WebTemplate template = context.localObject(templateToChange);

		template.setName(name);
		template.setContent(content);

		context.commitChanges();

		return new WebTemplateResource(template, cayenneService, securityManager);
	}

	public WebTemplateResource createNewTemplate(WebSiteLayout layout, String name, String content) {
		ObjectContext ctx = cayenneService.newContext();

		WebSiteLayout localLayout = ctx.localObject(layout);
		
		WebTemplate template = ctx.newObject(WebTemplate.class);
		template.setName(name);
		template.setContent(content);
		template.setLayout(localLayout);

		ctx.commitChanges();

		return new WebTemplateResource(template, cayenneService, securityManager);
	}
	
	private class LayoutDirectoryResource extends DirectoryResource {
		
		private WebSiteLayout layout;

		public LayoutDirectoryResource(String name, WebSiteLayout layout, SecurityManager securityManager) {
			super(name, securityManager);
			
			this.layout = layout;
		}

		@Override
		public Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException, ConflictException, NotAuthorizedException, BadRequestException {

			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer);

			String content = writer.toString();

			// check if there is an existing block with similar name
			WebTemplate template = getTemplateByName(newName, layout);

			if (template != null) {
				return changeTemplate(template, newName, content);
			}

			return createNewTemplate(layout, newName, content);
		}

		@Override
		public Resource child(String childName) throws NotAuthorizedException, BadRequestException {
			return getTemplateResourceByName(childName, layout);
		}

		@Override
		public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
			return listTemplates(layout);
		}
	}
}
