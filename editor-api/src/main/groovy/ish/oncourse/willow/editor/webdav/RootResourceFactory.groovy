package ish.oncourse.willow.editor.webdav

import io.milton.common.Path
import io.milton.http.Auth
import io.milton.http.Request
import io.milton.http.ResourceFactory
import io.milton.http.SecurityManager
import io.milton.http.exceptions.BadRequestException
import io.milton.http.exceptions.ConflictException
import io.milton.http.exceptions.NotAuthorizedException
import io.milton.resource.Resource
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.services.access.AuthenticationService
import org.apache.commons.lang3.ArrayUtils

import static ish.oncourse.willow.editor.EditorProperty.S_ROOT
import static ish.oncourse.willow.editor.webdav.TopLevelDir.*

class RootResourceFactory implements ResourceFactory {

    public static final String WEBDAV_PATH_PREFIX = '/editor/webdav'
    
    private BlockResourceFactory blockResourceFactory
    private PageResourceFactory pageResourceFactory
    private TemplateResourceFactory templateResourceFactory
    private StaticResourceFactory staticResourceFactory

    private SecurityManager securityManager
    private ICayenneService cayenneService
    private RequestService requestService

    private String sRoot

    RootResourceFactory(SecurityManager securityManager, AuthenticationService authenticationService, ICayenneService cayenneService, RequestService requestService) {
        this.securityManager = securityManager
        this.cayenneService = cayenneService
        this.requestService = requestService
        
        this.sRoot = S_ROOT.value
                
        this.blockResourceFactory = new BlockResourceFactory(cayenneService, requestService, securityManager)
        this.pageResourceFactory = new PageResourceFactory(cayenneService, requestService, securityManager)
        this.templateResourceFactory = new TemplateResourceFactory(cayenneService, requestService, securityManager)
        this.staticResourceFactory = new StaticResourceFactory(sRoot, authenticationService, securityManager, cayenneService, requestService)

        this.templateResourceFactory.initDefaultResources()
    }

    @Override
    Resource getResource(final String host, String url) throws NotAuthorizedException, BadRequestException {

        Path rawPath = Path.path(url)

        if (rawPath.toPath().startsWith(WEBDAV_PATH_PREFIX)) {
            final Path path = rawPath.stripFirst.stripFirst

            if (path.root) {
                return new DirectoryResource('webdav', securityManager) {
                    @Override
                    Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
                        return null
                    }

                    @Override
                    Resource child(String childName) throws NotAuthorizedException, BadRequestException {
                        switch (childName)
                        {
                            case RedirectsResource.FILE_NAME:
                                return new RedirectsResource(securityManager, cayenneService, requestService)
                            default:
                                return getDirectoryByName(valueOf(childName), host, path.stripFirst.toPath())
                        }
                    }

                    @Override
                    List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
                        List<Resource> resources = new ArrayList<>()

                        for (TopLevelDir dir : values()) {
                            resources.add(getDirectoryByName(dir, host, path.stripFirst.toPath()))
                        }

                        resources.add(new RedirectsResource(securityManager, cayenneService, requestService))
                        return resources
                    }

                    @Override
                    boolean authorise(Request request, Request.Method method, Auth auth) {
                        return super.authorise(request,method,auth) && ArrayUtils.contains(AccessRights.DIR_READ_ONLY, method)
                    }
                }
            }
            if (has(path.first))
            {
                return getDirectoryByName(valueOf(path.first), host, path.stripFirst.toPath())
            }
            else if (RedirectsResource.FILE_NAME == path.first)
            {
                return new RedirectsResource(securityManager, cayenneService, requestService)
            }
        }

        return null
    }

    private Resource getDirectoryByName(TopLevelDir dir, String host, String url) throws NotAuthorizedException, BadRequestException {
        switch (dir) {
            case pages:
                return pageResourceFactory.getResource(host, url)
            case blocks:
                return blockResourceFactory.getResource(host, url)
            case templates:
                return templateResourceFactory.getResource(host, url)
            case s:
                return (sRoot == null ? null:staticResourceFactory.getResource(host,  url))
            default:
                throw new IllegalArgumentException("unknown static dir: $dir")
        }
    }

}