package ish.oncourse.willow.billing.service.impl

import com.google.inject.Inject
import ish.oncourse.willow.billing.v1.model.SiteDTO
import ish.oncourse.willow.billing.v1.service.WebSiteApi
import ish.oncourse.willow.billing.website.WebSiteService


class WebSiteApiImpl implements WebSiteApi {
    
    
    @Inject
    private WebSiteService webSiteService
    
    @Override
    void crateSite(SiteDTO site) {
        webSiteService.createWebSite(site)
    }

    @Override
    void deleteSite(Long id) {
        webSiteService.deleteWebSite(id)
    }
    
    @Override
    List<SiteDTO> getSites() {
        webSiteService.getCollegeWebSites()
    }

    @Override
    void updateSite(SiteDTO site) {
        webSiteService.updateWebSite(site)
    }
}
