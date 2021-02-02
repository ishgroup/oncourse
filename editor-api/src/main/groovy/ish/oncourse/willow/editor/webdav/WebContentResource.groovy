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
import ish.oncourse.services.converter.CoreConverter
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.website.WebContentFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.io.IOUtils

import java.nio.charset.Charset

class WebContentResource  extends AbstractResource implements CopyableResource, DeletableResource, GetableResource, MoveableResource, PropFindableResource, ReplaceableResource {

    private WebContent webContent

    private ICayenneService cayenneService
    private RequestService requestService
    
    WebContentResource(WebContent webContent, ICayenneService cayenneService, RequestService requestService, SecurityManager securityManager) {
        super(securityManager)
        this.webContent = webContent
        this.cayenneService = cayenneService
        this.requestService = requestService
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
        if (webContent.contentTextile) {
            out << webContent.contentTextile.bytes
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
        if (webContent.contentTextile == null) {
            return 0l
        }
        //we should retrun amount of bytes (not chars)
        return (long) webContent.contentTextile.bytes.length
    }

    @Override
    void moveTo(CollectionResource rDest, String newName) throws ConflictException, NotAuthorizedException, BadRequestException {
        ObjectContext context = cayenneService.newContext()
        WebContent existingBlock = WebContentFunctions.getBlockByName(requestService.request, context, newName)
        if (existingBlock == null || existingBlock.objectId == webContent.objectId) {
            WebContent localBlock = context.localObject(webContent)
            localBlock.name = newName
            context.commitChanges()
        }
    }

    @Override
    Date getCreateDate() {
        return webContent.created
    }

    @Override
    String getName() {
        return webContent.name
    }

    @Override
    Date getModifiedDate() {
        return webContent.modified
    }

    @Override
    void replaceContent(InputStream into, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
        try {
            ObjectContext context = cayenneService.newContext()

            WebContent block = context.localObject(webContent)

            StringWriter writer = new StringWriter()
            IOUtils.copy(into, writer, Charset.defaultCharset())

            block.contentTextile = writer.toString()
            block.content = CoreConverter.convert(block.contentTextile)

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
