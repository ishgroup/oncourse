package ish.oncourse.willow.editor.webdav

import io.milton.http.Auth
import io.milton.http.Range
import io.milton.http.Request
import io.milton.http.SecurityManager
import io.milton.http.exceptions.BadRequestException
import io.milton.http.exceptions.ConflictException
import io.milton.http.exceptions.NotAuthorizedException
import io.milton.http.exceptions.NotFoundException
import io.milton.resource.CollectionResource
import io.milton.resource.FolderResource
import io.milton.resource.Resource

class DirectoryResource extends AbstractResource implements FolderResource {

    private String name
    private Closure<Resource> createNew
    private Closure<Resource> child
    private Closure<ArrayList<? extends Resource>> getChildren
    private Closure<Boolean> authorise
    private Closure<CollectionResource> createCollection

    DirectoryResource(String name, SecurityManager securityManager,
                      Closure<Resource> createNew,
                      Closure<Resource> child,
                      Closure<ArrayList<? extends Resource>> getChildren,
                      Closure<Boolean> authorise,
                      Closure<CollectionResource> createCollection = null) {
        super(securityManager)
        this.name = name
        this.createNew = createNew
        this.child = child
        this.getChildren = getChildren
        this.authorise = authorise
        this.createCollection = createCollection
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
        return createCollection ? createCollection.call(newName) : null
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

    @Override
    Resource createNew(String newName, InputStream inputStream, Long length, String contentType) throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
        createNew.call(newName, inputStream, length, contentType)
    }

    @Override
    Resource child(String childName) throws NotAuthorizedException, BadRequestException {
        child.call(childName)
    }

    @Override
    List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
        getChildren.call()
    }

    @Override
    boolean authorise(Request request, Request.Method method, Auth auth) {
        return super.authorise(request,method,auth) && authorise.call(request, method, auth)
    }

}
