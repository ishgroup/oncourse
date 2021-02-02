package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.services.alias.GetSpecialPages
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.rest.UpdateRedirects
import ish.oncourse.willow.editor.rest.UpdateSpecialPages
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.v1.model.SpecialPage
import ish.oncourse.willow.editor.v1.model.SpecialPageItem
import ish.oncourse.willow.editor.v1.model.SpecialPages
import ish.oncourse.willow.editor.v1.service.SpecialPageApi
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

class SpecialPageApiServiceImpl implements SpecialPageApi {

    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    @Inject
    SpecialPageApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }

    @Override
    SpecialPages getSpecialPages() {
        Map<SpecialPage, SpecialPageItem> resultMap = new HashMap<>()
        SpecialPage.values().each {SpecialPage v ->
            resultMap.put(v, new SpecialPageItem(null, v, UpdateRedirects.matchTypeRuleMapping.getByValue(UpdateRedirects.specialPageMapping.get(v).matchType), null))}

        ObjectContext context = cayenneService.newContext()

        GetSpecialPages.valueOf(WebSiteVersionFunctions.getCurrentVersion(requestService.request, context), cayenneService.newContext(), true)
                .get()
                .each { redirect ->
            SpecialPageItem item = resultMap.get(UpdateRedirects.specialPageMapping.getByValue(redirect.specialPage))
            if (item) {
                item.from = redirect.urlPath
                item.matchType = UpdateRedirects.matchTypeRuleMapping.getByValue(redirect.matchType)
            }
        }

        new SpecialPages(rules: resultMap.collect {it -> it.value})
    }

    @Override
    SpecialPages updateSpecialPages(SpecialPages specialPages) {
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
