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
import io.milton.resource.CollectionResource;
import io.milton.resource.Resource;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.model.WebTemplate;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.templates.IWebTemplateService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Pattern;

public class TemplateResourceFactory implements ResourceFactory {
	
	private static final Logger logger = LogManager.getLogger();
	
	private static final String TEMPLATE_DIR_NAME = "templates";
	private static final String DEFAULT_TEMPLATES_PACKAGE = "ish.oncourse.ui";

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IWebSiteVersionService webSiteVersionService;
	
	@Inject
	private IWebTemplateService webTemplateService;

	private io.milton.http.SecurityManager securityManager;
	
	private Map<String, String> defaultTemplatesMap;

	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}
	
	public void initDefaultResources() {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage(DEFAULT_TEMPLATES_PACKAGE))
				// exclude resources with *.internal.* in package name
				.filterInputsBy(new FilterBuilder().include(String.format("^%s.*", DEFAULT_TEMPLATES_PACKAGE)).exclude(".*(.internal.).*"))
				.setScanners(new ResourcesScanner()));

		Set<String> templates = reflections.getResources(Pattern.compile(".*\\.tml"));

		this.defaultTemplatesMap = new HashMap<>();

		for (String templatePath : templates) {
			defaultTemplatesMap.put(Path.path(templatePath).getName(), templatePath);
		}
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

				@Override
				public CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
					WebSiteLayout layout = createLayout(newName);
					
					return new LayoutDirectoryResource(layout.getLayoutKey(), layout, securityManager);
				}

                @Override
                public boolean authorise(Request request, Request.Method method, Auth auth) {
                    return super.authorise(request,method,auth) && ArrayUtils.contains(TopLevelDir.templates.getAllowedMethods(), method);
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
	
	private List<DirectoryResource> listLayouts() {
		ObjectContext context = cayenneService.newContext();
		
		List<WebSiteLayout> layouts = ObjectSelect.query(WebSiteLayout.class).
				where(WebSiteLayout.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion())).
				select(context);
		
		List<DirectoryResource> directoryResources = new ArrayList<>();
		for (WebSiteLayout layout : layouts) {
			directoryResources.add(new LayoutDirectoryResource(layout.getLayoutKey(), layout, securityManager));
		}
		
		return directoryResources;
	}
	
	private List<WebTemplateResource> listTemplates(WebSiteLayout layout) {
		List<WebTemplateResource> templates = new ArrayList<>();
		
		for (String templateFileName : defaultTemplatesMap.values()) {
			templates.add(new WebTemplateResource(templateFileName, layout, cayenneService, webTemplateService, securityManager, defaultTemplatesMap));
		}
		
		// add db templates which don't have corresponding classpath entries
		for (WebTemplate template : webTemplateService.getTemplatesForLayout(layout)) {
			if (!defaultTemplatesMap.containsKey(template.getName())) {
				templates.add(new WebTemplateResource(template.getName(), layout, cayenneService, webTemplateService, securityManager, defaultTemplatesMap));
			}
		}
		
		return templates;
	}

	private DirectoryResource getLayoutResourceByName(String name) {
		WebSiteLayout layout = getLayoutByName(name);
		
		return layout != null ? new LayoutDirectoryResource(layout.getLayoutKey(), layout, securityManager) : null;
	}
	
	private WebSiteLayout getLayoutByName(String name) {
		ObjectContext context = cayenneService.newContext();

		return ObjectSelect.query(WebSiteLayout.class).
				where(WebSiteLayout.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion())).
				and(WebSiteLayout.LAYOUT_KEY.eq(name)).
				selectOne(context);
	}
	
	private WebSiteLayout createLayout(String name) {
		ObjectContext context = cayenneService.newContext();
		
		WebSiteVersion siteVersion = context.localObject(
				webSiteVersionService.getCurrentVersion());
		
		WebSiteLayout layout = context.newObject(WebSiteLayout.class);
		
		layout.setLayoutKey(name);
		layout.setWebSiteVersion(siteVersion);
		
		context.commitChanges();
		
		return layout;
	}
	
	private WebTemplateResource getTemplateResourceByName(String name, WebSiteLayout layout) {
		String templateName = defaultTemplatesMap.get(name);
		
		if (templateName == null) {
			templateName = name;
		}
		
		return new WebTemplateResource(templateName, layout, cayenneService, webTemplateService, securityManager,defaultTemplatesMap);
	}
	
	private class LayoutDirectoryResource extends DirectoryResource {
		
		private WebSiteLayout layout;

		public LayoutDirectoryResource(String name, WebSiteLayout layout, SecurityManager securityManager) {
			super(name, securityManager);
			
			this.layout = layout;
		}

		@Override
		public Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
			ObjectContext context = cayenneService.newContext();
			WebSiteLayout localLayout = context.localObject(layout);

			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer);
			
			WebTemplate template = webTemplateService.createWebTemplate(newName, writer.toString(), localLayout);
			
			context.commitChanges();
			
			return new WebTemplateResource(template.getName(), localLayout, cayenneService, webTemplateService, securityManager, defaultTemplatesMap);
		}

		@Override
		public Resource child(String childName) throws NotAuthorizedException, BadRequestException {
			return getTemplateResourceByName(childName, layout);
		}

		@Override
		public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
			return listTemplates(layout);
		}

		@Override
		public void moveTo(CollectionResource rDest, String name) throws ConflictException, NotAuthorizedException, BadRequestException {
			ObjectContext context = cayenneService.newContext();
			
			WebSiteLayout layoutToChange = context.localObject(layout);
			layoutToChange.setLayoutKey(name);
			
			context.commitChanges();
		}

		@Override
		public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
			ObjectContext context = cayenneService.newContext();

			WebSiteLayout layoutToChange = context.localObject(layout);
			context.deleteObjects(layoutToChange);

			context.commitChanges();
		}

        @Override
        public boolean authorise(Request request, Request.Method method, Auth auth) {
            if (layout.getLayoutKey().equals(WebNodeType.DEFAULT_LAYOUT_KEY))
            {
                return super.authorise(request,method,auth) && ArrayUtils.contains(AccessRights.DIR_READ_ONLY_AND_ADD_CHILD, method);
            }
            else
            {
                return super.authorise(request,method,auth);
            }
        }
	}
}
