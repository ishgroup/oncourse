package ish.oncourse.willow.billing.website

import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.configuration.Configuration
import ish.oncourse.model.College
import ish.oncourse.model.WebHostName
import ish.oncourse.model.WebSite
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.billing.v1.model.SiteDTO
import ish.oncourse.willow.billing.v1.model.SiteTemplate
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.BadRequestException

import static ish.oncourse.configuration.Configuration.AdminProperty.S_ROOT

class WebSiteService {
    
    private static final Logger logger = LogManager.logger

    @Inject
    private ICayenneService cayenneService
    @Inject
    private RequestService requestService
    
    void createWebSite(SiteDTO dto) {
        validateSiteBeforeCreate(dto)
        WebSite newSite = createWebSite(requestService.college, dto.webSiteTemplate, dto.name, dto.key)
        updateDomains(newSite, dto.domains)
        newSite.objectContext.commitChanges()
    }
    
    WebSite createWebSite(College college, SiteTemplate siteTemplate, String name, String key) {
        ObjectContext context = cayenneService.newNonReplicatingContext()
        Map<String, String>  errors = [:]

        WebSite template = ObjectSelect.query(WebSite)
                .where(WebSite.SITE_KEY.eq("template-$siteTemplate".toString()))
                .selectOne(context)

        CreateNewWebSite createNewWebSite = CreateNewWebSite.valueOf(name,
                key,
                template,
                Configuration.getValue(S_ROOT),
                context.localObject(college), context)
        createNewWebSite.create()
        if (errors) {
            errors.each { k, v ->
                logger.error("$k: $v")
            }
        }
        return createNewWebSite.webSite

    }
    void updateDomains(WebSite site, List<String> domains) {
        ObjectContext context = site.objectContext
        List<WebHostName> domainsToDelete = site.collegeDomains.findAll{!(it.name in domains)}

        domains.findAll {!(it in site.collegeDomains*.name) }.each { domainName ->
            WebHostName domain = context.newObject(WebHostName)
            domain.webSite = site
            domain.college = site.college
            domain.name = domainName
            domain.created = new Date()
            domain.modified = new Date()

        }
        context.deleteObjects(domainsToDelete)
    
    
    
    }

    void validateSiteBeforeCreate(SiteDTO dto) {
        if (!dto.name) {
            throw new BadRequestException("Web site name is required")
        }
        if (!dto.key) {
            throw new BadRequestException("Web site key is required")
        }

        String collegeKey = requestService.college.collegeKey

        if (!dto.key.startsWith(requestService.college.collegeKey)) {
            throw new BadRequestException("Web site key should starts with $collegeKey prefix")
        }

        if (!dto.webSiteTemplate) {
            throw new BadRequestException("Web site template is required")
        }
    }
    
    List<SiteDTO> getCollegeSites(College college) {
        return requestService.college.webSites.collect {
            SiteDTO dto = new SiteDTO()
            dto.id = it.id
            dto.name = it.name
            dto.key = it.siteKey
            dto.domains = it.collegeDomains.collect{host -> host.name }
            dto
        }
    }
    
}
