package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.rest.UpdateRedirects
import ish.oncourse.willow.editor.rest.UpdateSpecialPages
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.v1.model.SpecialPageItem
import ish.oncourse.willow.editor.v1.service.SpecialPageApi
import ish.oncourse.willow.editor.website.WebUrlAliasFunctions
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

class SpecialPageApiImpl implements SpecialPageApi {

    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    @Inject
    SpecialPageApiImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }

    @Override
    List<SpecialPageItem> getSpecialPages() {
        WebUrlAliasFunctions.getRedirects(requestService.request, cayenneService.newContext())
                .findAll { !it.redirectTo && it.specialPage && it.matchType}
                .collect {redirect -> new SpecialPageItem().with { item ->
            item.from = redirect.urlPath
            item.specialPage = UpdateRedirects.specialPageMapping.getByValue(redirect.specialPage)
            item.matchType = UpdateRedirects.matchTypeRuleMapping.getByValue(redirect.matchType)
            item
        }}
    }

    @Override
    List<SpecialPageItem> updateSpecialPages(List<SpecialPageItem> specialPages) {
        ObjectContext context = cayenneService.newContext()
        UpdateSpecialPages updater = UpdateSpecialPages.valueOf(specialPages, context, requestService.request).update()

        if (updater.errors.empty) {
            context.commitChanges()
            return specialPages
        } else {
            context.rollbackChanges()

            throw new ClientErrorException(updater.errors.join('\n'), Response.status(Response.Status.BAD_REQUEST).entity(specialPages).build())

        }
    }
}
