package ish.oncourse.willow.editor.webdav

import io.milton.common.ContentTypeUtils
import io.milton.http.Auth
import io.milton.http.Range
import io.milton.http.exceptions.BadRequestException
import io.milton.http.exceptions.ConflictException
import io.milton.http.exceptions.NotAuthorizedException
import io.milton.http.exceptions.NotFoundException
import io.milton.resource.GetableResource
import io.milton.resource.PropFindableResource
import io.milton.resource.ReplaceableResource
import ish.oncourse.model.WebUrlAlias
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.rest.UpdateRedirects
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.v1.model.RedirectItem
import ish.oncourse.willow.editor.v1.model.Redirects
import ish.oncourse.willow.editor.website.WebUrlAliasFunctions
import org.apache.cayenne.ObjectContext

import javax.ws.rs.core.Response

class RedirectsResource extends AbstractResource implements GetableResource,PropFindableResource, ReplaceableResource {

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

    @Override
    void replaceContent(InputStream inputStream, Long length) throws BadRequestException, ConflictException, NotAuthorizedException {
        ObjectContext context = cayenneService.newContext()

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
        Redirects settings = new Redirects()

        String line
        while((line = br.readLine()) != null) {
            line = line.trim()
            if (line.length() > 0) {
                String[] pathes = line.trim().split(/\s+/)
                if (pathes.size() != 2) {
                    requestService.response.sendError(Response.Status.BAD_REQUEST.statusCode,"Wrong redirect line: $line. Please provide /from /to URLs splited by space or tab")
                    throw new BadRequestException(this)
                }
                settings.rules << new RedirectItem().from(pathes[0]).to(pathes[1])
            }
        }

        UpdateRedirects updater = UpdateRedirects.valueOf(settings, context, requestService.request).update()

        if (updater.errors.empty) {
            context.commitChanges()
        } else {
            context.rollbackChanges()
            requestService.response.sendError(Response.Status.BAD_REQUEST.statusCode,   updater.errors.join('\n'))
            throw new BadRequestException(this)
        }
        
    }
}

