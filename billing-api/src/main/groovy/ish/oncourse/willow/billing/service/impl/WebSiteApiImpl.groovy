package ish.oncourse.willow.billing.service.impl

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import ish.oncourse.willow.billing.v1.model.SiteDTO
import ish.oncourse.willow.billing.v1.service.WebSiteApi
import ish.oncourse.willow.billing.website.WebSiteService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.configuration.Configuration.AdminProperty.BILLING_UPDATE


class WebSiteApiImpl implements WebSiteApi {
    
    private static final String UPDATE_SCRIPT_PATH = Configuration.getValue(BILLING_UPDATE)
    private static final Logger logger = LogManager.logger


    @Inject
    private WebSiteService webSiteService
    
    @Override
    void createSite(SiteDTO site) {
        webSiteService.createWebSite(site)
        runUpdate(site.key)
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
        runUpdate(site.key)
    }
    
    private void runUpdate(String key) {
        try {
            Runtime.getRuntime().exec("$UPDATE_SCRIPT_PATH website $key")
        } catch (Exception e) {
            logger.catching(e)
        }
    }
}
