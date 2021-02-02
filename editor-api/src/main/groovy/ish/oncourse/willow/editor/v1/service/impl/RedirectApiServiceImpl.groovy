package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.rest.UpdateRedirects
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.v1.model.RedirectItem
import ish.oncourse.willow.editor.v1.model.Redirects
import ish.oncourse.willow.editor.v1.service.RedirectApi
import ish.oncourse.willow.editor.website.WebUrlAliasFunctions
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

class RedirectApiServiceImpl implements RedirectApi {
    
    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    @Inject
    RedirectApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }
    @Override
    Redirects getRedirects() {
        return new Redirects()
                .rules( WebUrlAliasFunctions.getRedirects(requestService.request, cayenneService.newContext())
                .collect { alias  -> new RedirectItem().with { redirect ->
            redirect.from = alias.urlPath
            redirect.to = alias.redirectTo
            redirect
        }
        })
    }

    @Override
    Redirects updateRedirects(Redirects redirects) {
        ObjectContext context = cayenneService.newContext()
        UpdateRedirects updater = UpdateRedirects.valueOf(redirects, context, requestService.request).update()

        if (updater.errors.empty) {
            context.commitChanges()
            return redirects
        } else {
            context.rollbackChanges()

            throw new ClientErrorException(updater.errors.join('\n'), Response.status(Response.Status.BAD_REQUEST).entity(redirects).build())

        }
    }
    
}
