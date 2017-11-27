package ish.oncourse.willow.editor.service.impl

import ish.oncourse.model.College
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.preference.GetEnrolSuccessUrl
import ish.oncourse.services.preference.GetRefundPolicyUrl
import ish.oncourse.willow.editor.model.common.CommonError
import ish.oncourse.willow.editor.model.settings.RedirectItem
import ish.oncourse.willow.editor.rest.UpdateRedirects
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.model.settings.CheckoutSettings
import ish.oncourse.willow.editor.model.settings.RedirectSettings
import ish.oncourse.willow.editor.model.settings.SkillsOnCourseSettings
import ish.oncourse.willow.editor.model.settings.WebsiteSettings

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebSiteFunctions
import ish.oncourse.willow.editor.website.WebUrlAliasFunctions
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@CompileStatic
class SettingsApiServiceImpl implements SettingsApi {

    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    SettingsApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }
    
    CheckoutSettings getCheckoutSettings() {
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)
        
        return new CheckoutSettings().with { settings ->
            settings.refundPolicy = new GetRefundPolicyUrl(college, context).get()
            settings.successUrl =  new GetEnrolSuccessUrl(college, context).get()
            settings
        }
    }

    CheckoutSettings setCheckoutSettings(CheckoutSettings saveCheckoutSettingsRequest) {
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)
        new GetRefundPolicyUrl(college, context).setValue(saveCheckoutSettingsRequest.refundPolicy)
        new GetEnrolSuccessUrl(college, context).setValue(saveCheckoutSettingsRequest.successUrl)
        context.commitChanges()
        return saveCheckoutSettingsRequest
    }
    
    
    RedirectSettings getRedirectSettings() {
        return new RedirectSettings()
                .rules( WebUrlAliasFunctions.getRedirects(requestService.request, cayenneService.newContext())
                    .collect { alias  -> new RedirectItem().with { redirect ->
                                redirect.from = alias.urlPath
                                redirect.to = alias.redirectTo
                                redirect
                             } 
                    })
    }

    RedirectSettings setRedirectSettings(RedirectSettings redirectSettingsRequest) {
        ObjectContext context = cayenneService.newContext()
        UpdateRedirects updater = UpdateRedirects.valueOf(redirectSettingsRequest, context, requestService.request).update()
        
        if (updater.errors.empty) {
            context.commitChanges()
            return redirectSettingsRequest
        } else {
            context.rollbackChanges()
            throw createClientException(updater.errors.join('\n'))
        }
    }
    
    SkillsOnCourseSettings getSkillsOnCourseSettings() {
        // TODO: Implement...
        
        return null
    }
    
    WebsiteSettings getWebsiteSettings() {
        // TODO: Implement...
        
        return null
    }
    
    
    SkillsOnCourseSettings setSkillsOnCourseSettings(SkillsOnCourseSettings skillsOnCourseSettingsRequest) {
        // TODO: Implement...
        
        return null
    }
    
    WebsiteSettings setWebsiteSettings(WebsiteSettings websiteSettingsRequest) {
        // TODO: Implement...
        
        return null
    }

    private ClientErrorException createClientException(String message) {
        logger.error("$message, server name: $requestService.request.serverName")
        new ClientErrorException(Response.status(400).entity(new CommonError(message: message)).build())
    }
}

