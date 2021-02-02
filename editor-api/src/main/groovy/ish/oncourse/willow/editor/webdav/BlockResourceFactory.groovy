package ish.oncourse.willow.editor.webdav

import io.milton.common.Path
import io.milton.http.Auth
import io.milton.http.Request
import io.milton.http.ResourceFactory
import io.milton.http.SecurityManager
import io.milton.http.exceptions.BadRequestException
import io.milton.http.exceptions.NotAuthorizedException
import io.milton.resource.Resource
import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebContentVisibility
import ish.oncourse.services.converter.CoreConverter
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.website.WebContentFunctions
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.io.IOUtils

import java.nio.charset.Charset

class BlockResourceFactory implements ResourceFactory {
    
    private ICayenneService cayenneService
    private RequestService requestService
    private SecurityManager securityManager

    BlockResourceFactory(ICayenneService cayenneService, RequestService requestService, SecurityManager securityManager) {
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.securityManager = securityManager
    }

    @Override
    Resource getResource(String host, String url) throws NotAuthorizedException, BadRequestException {

        Path path = Path.path(url)

        if (path.root) {
            return new DirectoryResource(TopLevelDir.blocks.name(), securityManager,
                    { String newName, InputStream inputStream, Long length, String contentType ->
                        StringWriter writer = new StringWriter()
                        IOUtils.copy(inputStream, writer, Charset.defaultCharset())

                        String content = writer.toString()

                        // check if there is an existing block with similar name
                        WebContent block = WebContentFunctions.getWebContent(requestService.request, cayenneService.newContext(), WebContent.NAME, newName)

                        if (block) {
                            return changeBlock(block, newName, content)
                        }

                        return createNewBlock(newName, content)
                    } as Closure<Resource>,
                    { String childName ->
                        return getBlockByName(childName)
                    } as Closure<Resource>,
                    {
                        return listBlocks() as ArrayList
                    } as Closure<ArrayList<Resource>>,
                    {  Request request, Request.Method method, Auth auth  ->
                        return method in TopLevelDir.blocks.allowedMethods
                    } as Closure<Boolean>) 
            
        } else if (path.length == 1) {
            String name = path.name
            return getBlockByName(name)
        }
        return null
    }

    List<WebContentResource> listBlocks() {
        List<WebContentResource> blocks = []
        
        WebContentFunctions.getBlocks(requestService.request, cayenneService.newContext()).each {
            blocks << new WebContentResource(it, cayenneService, requestService, securityManager)
        }

        return blocks
    }

    WebContentResource getBlockByName(String name) {
        WebContent block = WebContentFunctions.getWebContent(requestService.request, cayenneService.newContext(), WebContent.NAME, name)

        if (block) {
            return new WebContentResource(block, cayenneService, requestService, securityManager)
        }

        return null
    }

    WebContentResource changeBlock(WebContent blockToChange, String name, String content) {

        ObjectContext context = cayenneService.newContext()

        WebContent block = context.localObject(blockToChange)

        block.name = name
        block.contentTextile = content
        block.content = CoreConverter.convert(content)

        context.commitChanges()

        return new WebContentResource(block, cayenneService, requestService, securityManager)
    }

    WebContentResource createNewBlock(String name, String content) {
        ObjectContext ctx = cayenneService.newContext()

        WebContent block = ctx.newObject(WebContent)
        block.name = name
        block.contentTextile = content
        block.content = CoreConverter.convert(content)

        WebContentVisibility visibility = ctx.newObject(WebContentVisibility)
        visibility.regionKey = RegionKey.unassigned
        visibility.webContent = block

        block.webSiteVersion = WebSiteVersionFunctions.getCurrentVersion(requestService.request, ctx)

        ctx.commitChanges()

        return new WebContentResource(block, cayenneService, requestService, securityManager)
    }
}