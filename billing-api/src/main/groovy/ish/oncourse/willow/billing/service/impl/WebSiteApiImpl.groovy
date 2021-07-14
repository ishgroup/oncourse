package ish.oncourse.willow.billing.service.impl

import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.model.College
import ish.oncourse.model.WebHostName
import ish.oncourse.model.WebSite
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.s3.IS3Service
import ish.oncourse.willow.billing.v1.model.SiteDTO
import ish.oncourse.willow.billing.v1.service.WebSiteApi
import org.apache.cayenne.query.ObjectSelect

class WebSiteApiImpl implements WebSiteApi {
    
    @Inject
    private ICayenneService cayenneService

    @Inject
    private RequestService requestService
    
    @Override
    void crateSite(SiteDTO site) {

    }

    @Override
    void deleteSite(Long id) {

    }
    
    @Override
    List<SiteDTO> getSites() {
        return requestService.college.webSites.collect {
            SiteDTO dto = new SiteDTO()
            dto.id = it.id
            dto.name = it.name
            dto.key = it.siteKey
            ObjectSelect.query(WebHostName)
                    .where(WebHostName.WEB_SITE.eq(it))
                    .select(cayenneService.newContext())
                    .each {host->
                dto.domains << host.name
            }
            dto
        }
    }

    @Override
    void updateSite(SiteDTO site) {

    }
}
