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
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebNode
import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.textile.ConvertCoreTextile
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebNodeFunctions
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.ArrayUtils

import java.nio.charset.Charset

class PageResourceFactory implements ResourceFactory {

    private ICayenneService cayenneService
    private RequestService requestService
    private SecurityManager securityManager

    PageResourceFactory(ICayenneService cayenneService, RequestService requestService, SecurityManager securityManager) {
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.securityManager = securityManager
    }

    @Override
    Resource getResource(String host, String url) throws NotAuthorizedException, BadRequestException {

        Path path = Path.path(url)

        if (path.root) {
            return new DirectoryResource(TopLevelDir.pages.name(), securityManager) {
                @Override
                Resource child(String childName) throws NotAuthorizedException, BadRequestException {
                    return getWebNodeResource(childName)
                }

                @Override
                List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
                    return listPages()
                }

                @Override
                Resource createNew(String newName, InputStream inputStream, Long length, String contentType)
                        throws IOException, ConflictException, NotAuthorizedException, BadRequestException {

                    StringWriter writer = new StringWriter()
                    IOUtils.copy(inputStream, writer, Charset.defaultCharset())

                    String content = writer.toString()

                    // check if there is an existing page with similar name
                    WebNode page = WebNodeFunctions.getNodeForName(newName, requestService.request, cayenneService.sharedContext())

                    if (page) {
                        return changePage(page, newName, content)
                    }

                    return createNewPage(newName, content)
                }

                @Override
                boolean authorise(Request request, Request.Method method, Auth auth) {
                    return super.authorise(request,method,auth) && ArrayUtils.contains(TopLevelDir.pages.allowedMethods, method)
                }
            }
        } else if (path.length == 1) {
            String name = path.name
            return getWebNodeResource(name)
        }

        return null
    }

    List<WebNodeResource> listPages() {
        List<WebNodeResource> pages = new ArrayList<>()
        WebNodeFunctions.getNodes(requestService.request, cayenneService.sharedContext()).each { page ->
            pages.add(new WebNodeResource(page, cayenneService, securityManager, requestService))
        }
        return pages
    }

    WebNodeResource getWebNodeResource(String name) {
        WebNode page = WebNodeFunctions.getNodeForName(name, requestService.request, cayenneService.sharedContext())
        if (page == null) {
            return null
        }
        return new WebNodeResource(page, cayenneService, securityManager, requestService)
    }
        
    WebNodeResource changePage(WebNode pageToChange, String name, String content) {

        ObjectContext context = cayenneService.newContext()

        WebNode page = context.localObject(pageToChange)
        page.name = name

        WebContent block = page.webContentVisibility[0].webContent
        block.contentTextile = content
        block.content = ConvertCoreTextile.valueOf(content).convert()
        context.commitChanges()
        return new WebNodeResource(page, cayenneService, securityManager, requestService)
    }

    WebNodeResource createNewPage(String name, String content) {
        ObjectContext sharedContext = cayenneService.sharedContext()
        ObjectContext ctx = cayenneService.newContext()
        WebSiteVersion webSiteVersion = ctx.localObject(WebSiteVersionFunctions.getCurrentVersion(requestService.request, sharedContext))
        WebNodeType webNodeType = ctx.localObject(WebNodeFunctions.getDefaultWebNodeType(requestService.request, sharedContext))
        WebNode webNode = WebNodeFunctions.createNewNodeBy(webSiteVersion, webNodeType, name, content, WebNodeFunctions.getNextNodeNumber(requestService.request, sharedContext))
        ctx.commitChanges()
        return new WebNodeResource(webNode, cayenneService, securityManager, requestService)
    }

}
