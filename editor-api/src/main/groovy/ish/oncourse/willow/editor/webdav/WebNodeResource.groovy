package ish.oncourse.willow.editor.webdav

import io.milton.common.ContentTypeUtils
import io.milton.http.Auth
import io.milton.http.Range
import io.milton.http.SecurityManager
import io.milton.http.exceptions.BadRequestException
import io.milton.http.exceptions.ConflictException
import io.milton.http.exceptions.NotAuthorizedException
import io.milton.http.exceptions.NotFoundException
import io.milton.resource.CollectionResource
import io.milton.resource.CopyableResource
import io.milton.resource.DeletableResource
import io.milton.resource.GetableResource
import io.milton.resource.MoveableResource
import io.milton.resource.PropFindableResource
import io.milton.resource.ReplaceableResource
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebNode
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.textile.ConvertCoreTextile
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebNodeFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.io.IOUtils

import java.nio.charset.Charset

class WebNodeResource  extends AbstractResource implements CopyableResource, DeletableResource, GetableResource, MoveableResource, PropFindableResource, ReplaceableResource {

    private WebNode webNode
    
    private ICayenneService cayenneService
    private RequestService requestService

    WebNodeResource(WebNode webNode, ICayenneService cayenneService, SecurityManager securityManager, RequestService requestService) {
        super(securityManager)
        this.webNode = webNode
        this.cayenneService = cayenneService
        this.requestService = requestService
    }

    private WebContent getWebContent() {
        return webNode.webContentVisibility[0].webContent
    }

    @Override
    void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
    }

    @Override
    void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
        // delete does nothing due to the Cyberduck way of inline editing, see moveTo method for details
    }

    @Override
    void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
        String content = getWebContent().contentTextile

        if (content) {
            out << content.bytes
        }
    }

    @Override
    Long getMaxAgeSeconds(Auth auth) {
        return null
    }

    @Override
    String getContentType(String accepts) {
        return ContentTypeUtils.findAcceptableContentType('text/html', accepts)
    }

    @Override
    Long getContentLength() {
        String content = getWebContent().contentTextile

        if (content == null) {
            return 0l
        }

        //we should retrun amount of bytes (not chars)
        return (long) content.bytes.length
    }

    @Override
    void moveTo(CollectionResource rDest, String newName) throws ConflictException, NotAuthorizedException, BadRequestException {

        // this logic is a bit tricky and is there for the purpose of working around odd Cyberduck behavior when
        // editing records. When editing record Cyberduck's actions are:
        //
        //		1. create new block/page with changed content and temporary name
        //		2. delete existing block/page
        //		3. rename new block/page to its real name
        //
        // To avoid losing block/page relationships and other non WebDAV editable fields during the step 2
        // we do the following:
        //
        // 		- if the rename does not overwrite anything, then just perform the rename of the name 
        // 		  of the record in the db leaving all relations intact
        // 		- if the rename overwrites something, then just copy the content and delete the old record permanently

        ObjectContext context = cayenneService.newContext()

        WebNode existingNode = WebNodeFunctions.getNodeForName(newName, requestService.request, context)

        // if there is no existing record with such name and we are not renaming current record to the same name
        // then just change name of the page
        // otherwise - replace content of existing record with the new one and delete the new record

        if (existingNode == null || existingNode.objectId == webNode.objectId) {
            WebNode localNode = context.localObject(webNode)
            localNode.name = newName
        } else {
            WebContent webContent = existingNode.webContentVisibility[0].webContent

            webContent.contentTextile = getWebContent().contentTextile
            webContent.content = getWebContent().content

            context.deleteObjects(context.localObject(webNode))
            //we should delete not only temporary webNode but also and whole structure of the content
            context.deleteObjects(context.localObject(webNode.webContentVisibility[0]))
            context.deleteObjects(context.localObject(webNode.webContentVisibility[0].webContent))
        }

        context.commitChanges()
    }

    @Override
    Date getCreateDate() {
        return webNode.created
    }

    @Override
    String getName() {
        return webNode.name
    }

    @Override
    Date getModifiedDate() {
        return webNode.modified
    }

    @Override
    void replaceContent(InputStream into, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
        try {
            ObjectContext context = cayenneService.newContext()
            WebContent block = context.localObject(getWebContent())

            StringWriter writer = new StringWriter()
            IOUtils.copy(into, writer, Charset.defaultCharset())

            String contentTextile = writer.toString()
            block.contentTextile = contentTextile
            block.content = ConvertCoreTextile.valueOf(contentTextile).convert()

            context.commitChanges()
        } catch (Exception e) {
            throw new BadRequestException('Can\'t replace block content.', e)
        }
    }

    @Override
    String getUniqueId() {
        return null
    }
}
