package ish.oncourse.willow.billing.website

import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.configuration.Configuration
import ish.oncourse.model.College
import ish.oncourse.model.WebHostName
import ish.oncourse.model.WebSite
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.site.WebSiteDelete
import ish.oncourse.willow.billing.v1.model.SiteDTO
import ish.oncourse.willow.billing.v1.model.SiteTemplate
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.BadRequestException
import javax.ws.rs.InternalServerErrorException

import static ish.oncourse.configuration.Configuration.AdminProperty.S_ROOT

class WebSiteService {
    
    private static final Logger logger = LogManager.logger

    @Inject
    private ICayenneService cayenneService
    @Inject
    private RequestService requestService
    
    void createWebSite(SiteDTO dto) {
        validateWebSiteBeforeCreate(dto)
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
    
    private void updateDomains(WebSite site, List<String> domains) {
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

    private void validateWebSiteBeforeCreate(SiteDTO dto) {
        if (!dto.name) {
            throw new BadRequestException("Web site name is required")
        }
        if (!dto.key) {
            throw new BadRequestException("Web site url location is required")
        }
        
        if (getWebSite(dto.key)) {
            throw new BadRequestException("Web site url location must be unique")
        } 

        String collegeKey = requestService.college.collegeKey

        if (!dto.key.startsWith(collegeKey)) {
            throw new BadRequestException("Web site key should starts with $collegeKey prefix")
        }

        if (!dto.webSiteTemplate) {
            throw new BadRequestException("Web site template is required")
        }
    }
    
    List<SiteDTO> getCollegeWebSites() {
        return requestService.college.webSites.collect {
            SiteDTO dto = new SiteDTO()
            dto.id = it.id
            dto.name = it.name
            dto.key = it.siteKey
            dto.domains = it.collegeDomains.collect{host -> host.name }
            dto
        }
    }
    
    WebSite getWebSite(Long id) {
        WebSite webSite = SelectById.query(WebSite, id).selectOne(cayenneService.newContext())
        if (webSite) {
            return webSite
        } else {
            throw new BadRequestException("Web site not found")
        }
    }
    WebSite getWebSite(String key) {
        ObjectSelect.query(WebSite).where(WebSite.SITE_KEY.eq(key)).selectFirst(cayenneService.newContext())
    }
    
    void updateWebSite(SiteDTO dto) {
        if (!dto.id) {
            throw new BadRequestException("Web site identifier in not present in update request")
        }
        WebSite webSite = getWebSite(dto.id)

        if (!dto.name) {
            throw new BadRequestException("Web site name is required")
        } else {
            webSite.name = dto.name
        }

        if (!dto.key) {
            throw new BadRequestException("Web site url location is required")
        } else if (webSite.siteKey != dto.key) {
            if (getWebSite(dto.key)) {
                throw new BadRequestException("Web site url location must be unique")
            } else if (!dto.key.startsWith(requestService.college.collegeKey)) {
                throw new BadRequestException("Web site key should starts with $requestService.college.collegeKey prefix")
            } else {
                webSite.siteKey = dto.key
            }
        }
        updateDomains(webSite, dto.domains)
        webSite.objectContext.commitChanges()
    } 
    
    void deleteWebSite(Long id) {
        WebSite webSite = getWebSite(id)
        try {
            WebSiteDelete.valueOf(webSite, webSite.objectContext).delete()
        } catch (Exception e) {
            logger.error("Web site could not be deleted", e)
            throw new InternalServerErrorException("Something unexpected has happened while deleting web site.\nContact ish support, please.")

        }

    }
}
