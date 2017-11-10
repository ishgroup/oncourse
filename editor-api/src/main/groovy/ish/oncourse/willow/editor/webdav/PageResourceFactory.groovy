package ish.oncourse.willow.editor.webdav

import com.google.inject.Inject
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
import ish.oncourse.willow.editor.website.WebNodeFunctions
import ish.oncourse.willow.editor.website.WebSiteFunctions
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.ArrayUtils

import java.nio.charset.Charset

class PageResourceFactory implements ResourceFactory {

    @Inject
    private ICayenneService cayenneService


    @Inject
    private org.eclipse.jetty.server.Request request

    private SecurityManager securityManager

    void setSecurityManager(SecurityManager securityManager) {
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
                    WebNode page = WebNodeFunctions.getNodeForName(newName, request, cayenneService.sharedContext())

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
        WebNodeFunctions.getNodes(request, cayenneService.sharedContext()).each { page ->
            pages.add(new WebNodeResource(page, cayenneService, securityManager))
        }
        return pages
    }

    WebNodeResource getWebNodeResource(String name) {
        WebNode page = WebNodeFunctions.getNodeForName(name,request,cayenneService.sharedContext())
        if (page == null) {
            return null
        }
        return new WebNodeResource(page, cayenneService, securityManager)
    }
        
    WebNodeResource changePage(WebNode pageToChange, String name, String content) {

        ObjectContext context = cayenneService.newContext()

        WebNode page = context.localObject(pageToChange)
        page.name = name

        WebContent block = page.webContentVisibility[0].webContent
        block.contentTextile = content
        block.content = ConvertCoreTextile.valueOf(content).convert()
        context.commitChanges()
        return new WebNodeResource(page, cayenneService, securityManager)
    }

    WebNodeResource createNewPage(String name, String content) {
        ObjectContext sharedContext = cayenneService.sharedContext()
        ObjectContext ctx = cayenneService.newContext()
        WebSiteVersion webSiteVersion = ctx.localObject(WebSiteVersionFunctions.getCurrentVersion(request, sharedContext))
        WebNodeType webNodeType = ctx.localObject(WebNodeFunctions.getDefaultWebNodeType(request, sharedContext))
        WebNode webNode = WebNodeFunctions.createNewNodeBy(webSiteVersion, webNodeType, name, content, WebNodeFunctions.getNextNodeNumber(request, sharedContext))
        ctx.commitChanges()
        return new WebNodeResource(webNode, cayenneService, securityManager)
    }

}
