package ish.oncourse.willow.billing.service.impl

import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.billing.v1.model.SiteDTO
import ish.oncourse.willow.billing.v1.service.WebSiteApi
import ish.oncourse.willow.billing.website.WebSiteService

import javax.ws.rs.BadRequestException

class WebSiteApiImpl implements WebSiteApi {
    
    
    @Inject
    private WebSiteService webSiteService
    
    @Override
    void crateSite(SiteDTO site) {
        webSiteService.createWebSite(site)
    }

    @Override
    void deleteSite(Long id) {

    }
    
    @Override
    List<SiteDTO> getSites() {
        webSiteService.getCollegeSites()
    }

    @Override
    void updateSite(SiteDTO site) {
        
    }
}
