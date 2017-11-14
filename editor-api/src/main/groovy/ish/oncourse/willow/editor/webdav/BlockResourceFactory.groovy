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
import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebContentVisibility
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.textile.ConvertCoreTextile
import ish.oncourse.willow.editor.website.WebContentFunctions
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.ArrayUtils

import java.nio.charset.Charset

class BlockResourceFactory implements ResourceFactory {
    
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
            return new DirectoryResource(TopLevelDir.blocks.name(), securityManager) {
                @Override
                Resource child(String childName) throws NotAuthorizedException, BadRequestException {
                    return getBlockByName(childName)
                }

                @Override
                List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
                    return listBlocks()
                }

                @Override
                Resource createNew(String newName, InputStream inputStream, Long length, String contentType)
                        throws IOException, ConflictException, NotAuthorizedException, BadRequestException {

                    StringWriter writer = new StringWriter()
                    IOUtils.copy(inputStream, writer, Charset.defaultCharset())

                    String content = writer.toString()

                    // check if there is an existing block with similar name
                    WebContent block = WebContentFunctions.getWebContent(request, cayenneService.sharedContext(), WebContent.NAME, newName)

                    if (block) {
                        return changeBlock(block, newName, content)
                    }

                    return createNewBlock(newName, content)
                }

                @Override
                boolean authorise(Request request, Request.Method method, Auth auth) {
                    return super.authorise(request,method,auth) && method in TopLevelDir.blocks.allowedMethods
                }
            }
        } else if (path.length == 1) {
            String name = path.name
            return getBlockByName(name)
        }
        return null
    }

    List<WebContentResource> listBlocks() {
        List<WebContentResource> blocks = []
        
        WebContentFunctions.getBlocks(request, cayenneService.sharedContext()).each {
            blocks << new WebContentResource(it, cayenneService, securityManager)
        }

        return blocks
    }

    WebContentResource getBlockByName(String name) {
        WebContent block = WebContentFunctions.getWebContent(request, cayenneService.sharedContext(), WebContent.NAME, name)

        if (block) {
            return new WebContentResource(block, cayenneService, securityManager)
        }

        return null
    }

    WebContentResource changeBlock(WebContent blockToChange, String name, String content) {

        ObjectContext context = cayenneService.newContext();

        WebContent block = context.localObject(blockToChange);

        block.name = name
        block.contentTextile = content
        block.content = ConvertCoreTextile.valueOf(content).convert()

        context.commitChanges()

        return new WebContentResource(block, cayenneService, securityManager)
    }

    WebContentResource createNewBlock(String name, String content) {
        ObjectContext ctx = cayenneService.newContext()

        WebContent block = ctx.newObject(WebContent)
        block.name = name
        block.contentTextile = content
        block.content = ConvertCoreTextile.valueOf(content).convert()

        WebContentVisibility visibility = ctx.newObject(WebContentVisibility)
        visibility.regionKey = RegionKey.unassigned
        visibility.webContent = block

        block.webSiteVersion = ctx.localObject(ctx.localObject(WebSiteVersionFunctions.getCurrentVersion(request, cayenneService.sharedContext())))

        ctx.commitChanges()

        return new WebContentResource(block, cayenneService, securityManager)
    }
}