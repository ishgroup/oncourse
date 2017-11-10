package ish.oncourse.willow.editor.webdav

import io.milton.http.Auth
import io.milton.http.Range
import io.milton.http.exceptions.BadRequestException
import io.milton.http.exceptions.ConflictException
import io.milton.http.exceptions.NotAuthorizedException
import io.milton.http.exceptions.NotFoundException
import io.milton.resource.CollectionResource
import io.milton.resource.FolderResource

abstract class DirectoryResource extends AbstractResource implements FolderResource {

    private String name

     DirectoryResource(String name, io.milton.http.SecurityManager securityManager) {
        super(securityManager)
        this.name = name
    }

    @Override
    void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
    }

    @Override
    void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
    }

    @Override
    void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
    }

    @Override
    Long getMaxAgeSeconds(Auth auth) {
        return null
    }

    @Override
    String getContentType(String accepts) {
        return null
    }

    @Override
    Long getContentLength() {
        return null
    }

    @Override
    CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
        return null
    }

    @Override
    void moveTo(CollectionResource rDest, String name) throws ConflictException, NotAuthorizedException, BadRequestException {
    }

    @Override
    Date getCreateDate() {
        return null
    }

    @Override
    String getUniqueId() {
        return name
    }

    @Override
    String getName() {
        return name
    }

    @Override
    Date getModifiedDate() {
        return null
    }

}
