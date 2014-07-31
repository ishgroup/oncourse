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
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.util.ContextUtil;
import org.apache.tapestry5.ioc.Registry;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RootResourceFactory implements ResourceFactory {

	public static final String WEBDAV_PATH_PREFIX = "/cms/webdav";

	private IWebSiteService webSiteService;

	private Registry registry;

	private BlockResourceFactory blockResourceFactory;
	private PageResourceFactory pageResourceFactory;
	private TemplateResourceFactory templateResourceFactory;
	private StaticResourceFactory staticResourceFactory;

	private SecurityManager securityManager;

	private String sRoot;

	public RootResourceFactory(Registry registry, io.milton.http.SecurityManager securityManager) {
		this.registry = registry;
		this.securityManager = securityManager;

		this.webSiteService = registry.getService(IWebSiteService.class);

		this.sRoot = ContextUtil.getSRoot();

		this.blockResourceFactory = registry.autobuild(BlockResourceFactory.class);
		this.pageResourceFactory = registry.autobuild(PageResourceFactory.class);
		this.templateResourceFactory = registry.autobuild(TemplateResourceFactory.class);
		this.staticResourceFactory = new StaticResourceFactory(sRoot, webSiteService,
				registry.getService(IAuthenticationService.class), securityManager);

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
						return getDirectoryByName(TopLevelDir.valueOf(childName), host, path.getStripFirst().toPath());
					}

					@Override
					public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
						List<Resource> resources = new ArrayList<>();

						for (TopLevelDir dir : TopLevelDir.values()) {
							resources.add(getDirectoryByName(dir, host, path.getStripFirst().toPath()));
						}

						return resources;
					}

                    @Override
                    public boolean authorise(Request request, Request.Method method, Auth auth) {
                        switch(method) {
                            case GET:
                            case HEAD:
                            case OPTIONS:
                            case PROPFIND:
                                return auth != null;
                            default:
                                return false;
                        }
                    }
				};
			}
            if (TopLevelDir.has(path.getFirst()))
            {
                return getDirectoryByName(TopLevelDir.valueOf(path.getFirst()), host, path.getStripFirst().toPath());
            }
            else
            {
                return null;
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
