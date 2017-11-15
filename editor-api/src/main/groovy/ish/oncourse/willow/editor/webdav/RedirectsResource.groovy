package ish.oncourse.willow.editor.webdav

import com.google.inject.Inject
import io.milton.common.ContentTypeUtils
import io.milton.http.Auth
import io.milton.http.Range
import io.milton.http.exceptions.BadRequestException
import io.milton.http.exceptions.NotAuthorizedException
import io.milton.http.exceptions.NotFoundException
import io.milton.resource.GetableResource
import io.milton.resource.PropFindableResource
import ish.oncourse.model.WebUrlAlias
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebUrlAliasFunctions
import org.eclipse.jetty.server.Request

class RedirectsResource extends AbstractResource implements GetableResource,PropFindableResource {

    public static final String FILE_NAME = 'redirects.txt'


    private ICayenneService cayenneService
    private RequestService requestService
    
    RedirectsResource(io.milton.http.SecurityManager securityManager, ICayenneService cayenneService, RequestService requestService) {
        super(securityManager)
        this.cayenneService = cayenneService
        this.requestService = requestService
    }

    @Override
    void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
        List<WebUrlAlias> urlAliases = WebUrlAliasFunctions.getRedirects(requestService.request, cayenneService.newContext())
        urlAliases.each { urlAlias -> out.write("${urlAlias.urlPath}\t${urlAlias.redirectTo}\n".bytes) }
       
    }

    @Override
    Long getMaxAgeSeconds(Auth auth) {
        return null
    }

    @Override
    String getContentType(String accepts) {
        return ContentTypeUtils.findAcceptableContentType('text', accepts)
    }

    @Override
    Long getContentLength() {
        List<WebUrlAlias> urlAliases = WebUrlAliasFunctions.getRedirects(requestService.request, cayenneService.newContext())

        StringBuilder value = new StringBuilder()
        urlAliases.each { urlAlias -> value.append("${urlAlias.urlPath}\t${ urlAlias.redirectTo}\n") }

        return (long)value.toString().bytes.length
    }

    @Override
    String getUniqueId() {
        return null
    }

    @Override
    String getName() {
        return FILE_NAME
    }

    @Override
    Date getModifiedDate() {
        return null
    }

    @Override
    Date getCreateDate() {
        return new Date()
    }
}

