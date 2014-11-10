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
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.ContextUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tapestry5.ioc.Registry;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RootResourceFactory implements ResourceFactory {

	public static final String WEBDAV_PATH_PREFIX = "/cms/webdav";

    private IWebUrlAliasService webUrlAliasService;

	private BlockResourceFactory blockResourceFactory;
	private PageResourceFactory pageResourceFactory;
	private TemplateResourceFactory templateResourceFactory;
	private StaticResourceFactory staticResourceFactory;

	private SecurityManager securityManager;

	private String sRoot;

	public RootResourceFactory(Registry registry, io.milton.http.SecurityManager securityManager) {
		this.securityManager = securityManager;

        IWebSiteService webSiteService = registry.getService(IWebSiteService.class);
        IMailService mailService = registry.getService(IMailService.class);
        webUrlAliasService = registry.getService(IWebUrlAliasService.class);

		this.sRoot = ContextUtil.getSRoot();

		this.blockResourceFactory = registry.autobuild(BlockResourceFactory.class);
		this.pageResourceFactory = registry.autobuild(PageResourceFactory.class);
		this.templateResourceFactory = registry.autobuild(TemplateResourceFactory.class);
		this.staticResourceFactory = new StaticResourceFactory(sRoot, webSiteService,
				registry.getService(IAuthenticationService.class), securityManager,
                mailService);

		this.blockResourceFactory.setSecurityManager(securityManager);
		this.pageResourceFactory.setSecurityManager(securityManager);
		this.templateResourceFactory.setSecurityManager(securityManager);

		this.templateResourceFactory.initDefaultResources();
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
                        switch (childName)
                        {
                             case RedirectsResource.FILE_NAME:
                                 return new RedirectsResource(securityManager, webUrlAliasService);
                             default:
                                 return getDirectoryByName(TopLevelDir.valueOf(childName), host, path.getStripFirst().toPath());
                        }
					}

					@Override
					public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
						List<Resource> resources = new ArrayList<>();

						for (TopLevelDir dir : TopLevelDir.values()) {
							resources.add(getDirectoryByName(dir, host, path.getStripFirst().toPath()));
						}

						resources.add(new RedirectsResource(securityManager, webUrlAliasService));
						return resources;
					}

                    @Override
                    public boolean authorise(Request request, Request.Method method, Auth auth) {
                        return super.authorise(request,method,auth) && ArrayUtils.contains(AccessRights.DIR_READ_ONLY, method);

                    }
				};
			}
            if (TopLevelDir.has(path.getFirst()))
            {
                return getDirectoryByName(TopLevelDir.valueOf(path.getFirst()), host, path.getStripFirst().toPath());
            }
            else if (RedirectsResource.FILE_NAME.equals(path.getFirst()))
            {
                return new RedirectsResource(securityManager, webUrlAliasService);
            }
		}

		return null;
	}

	private Resource getDirectoryByName(TopLevelDir dir, String host, String url) throws NotAuthorizedException, BadRequestException {
		switch (dir) {
			case pages:
				return pageResourceFactory.getResource(host, url);
			case blocks:
				return blockResourceFactory.getResource(host, url);
			case templates:
				return templateResourceFactory.getResource(host, url);
            case s:
				return (sRoot == null ? null:staticResourceFactory.getResource(host,  url));
            default:
                throw new IllegalArgumentException(String.format("unknown static dir: %s",dir));
		}
	}

}
