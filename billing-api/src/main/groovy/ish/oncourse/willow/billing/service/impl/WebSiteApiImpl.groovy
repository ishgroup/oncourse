package ish.oncourse.willow.billing.service.impl

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import ish.oncourse.willow.billing.env.EnvironmentService
import ish.oncourse.willow.billing.v1.model.SiteDTO
import ish.oncourse.willow.billing.v1.service.WebSiteApi
import ish.oncourse.willow.billing.website.WebSiteService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.configuration.Configuration.AdminProperty.BILLING_UPDATE


class WebSiteApiImpl implements WebSiteApi {

    
    @Inject
    private WebSiteService webSiteService
    
    @Inject
    private EnvironmentService environmentService
    
    @Override
    void createSite(SiteDTO site) {
        webSiteService.createWebSite(site)
        environmentService.webSiteUpdated(site.key)
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
        environmentService.webSiteUpdated(site.key)
    }

}
