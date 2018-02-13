package ish.oncourse.willow.editor.service.impl

import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.preference.GetEnrolSuccessUrl
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.GetRefundPolicyUrl
import ish.oncourse.willow.editor.model.common.CommonError
import ish.oncourse.willow.editor.model.settings.ClassAge
import ish.oncourse.willow.editor.model.settings.ClassCondition
import ish.oncourse.willow.editor.model.settings.ClassEnrolmentCondition
import ish.oncourse.willow.editor.model.settings.RedirectItem
import ish.oncourse.willow.editor.rest.UpdateRedirects
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.model.settings.RedirectSettings
import ish.oncourse.willow.editor.model.settings.SkillsOnCourseSettings
import ish.oncourse.willow.editor.model.settings.WebsiteSettings

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebSiteFunctions
import ish.oncourse.willow.editor.website.WebUrlAliasFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.oncourse.services.preference.Preferences.*

@CompileStatic
class SettingsApiServiceImpl implements SettingsApi {

    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    @Inject
    SettingsApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
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
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)
        return new SkillsOnCourseSettings().with { settings ->
            settings.enableOutcomeMarking = new GetPreference(college, OUTCOME_MARKING_VIA_PORTAL, context).booleanValue
            settings.hideStudentDetails =  new GetPreference(college, HIDE_STUDENT_DETAILS_FROM_TUTOR, context).booleanValue
            settings.tutorFeedbackEmail =  new GetPreference(college, TUTOR_FEEDBACK_EMAIL, context).stringValue
            settings
        }
        
    }

    SkillsOnCourseSettings setSkillsOnCourseSettings(SkillsOnCourseSettings skillsOnCourseSettingsRequest) {
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)
        
        new GetPreference(college, OUTCOME_MARKING_VIA_PORTAL, context).booleanValue = skillsOnCourseSettingsRequest.enableOutcomeMarking
        new GetPreference(college, HIDE_STUDENT_DETAILS_FROM_TUTOR, context).booleanValue = skillsOnCourseSettingsRequest.hideStudentDetails
        new GetPreference(college, TUTOR_FEEDBACK_EMAIL, context).stringValue = skillsOnCourseSettingsRequest.tutorFeedbackEmail
        context.commitChanges()
        
        return skillsOnCourseSettingsRequest
    }
    
    WebsiteSettings getWebsiteSettings() {
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)
        
        return new WebsiteSettings().with { settings ->
            settings.enableSocialMedia = new GetPreference(college,ENABLE_SOCIAL_MEDIA_LINKS,context).booleanValue
            settings.addThisId = new GetPreference(college, ADDTHIS_PROFILE_ID, context).stringValue?:StringUtils.EMPTY
            settings.enableForCourse = new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS_COURSE, context).booleanValue
            settings.enableForWebpage = new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE, context).booleanValue
            settings.classAge = new ClassAge().with { age ->
                
                age.hideClassDays = new GetPreference(college, HIDE_CLASS_ON_WEB_AGE, context).integerValue
                
                String classCondition = new GetPreference(college, HIDE_CLASS_ON_WEB_AGE_TYPE, context).stringValue
                age.hideClassCondition = classCondition? ClassCondition.fromValue(classCondition) : ClassCondition.BEFORECLASSENDS
                
                age.stopWebEnrolmentDays = new GetPreference(college, STOP_WEB_ENROLMENTS_AGE, context).integerValue
                
                String enrolmentCondition = new GetPreference(college, STOP_WEB_ENROLMENTS_AGE_TYPE, context).stringValue
                age.stopWebEnrolmentCondition = enrolmentCondition? ClassEnrolmentCondition.fromValue(classCondition) : ClassEnrolmentCondition.BEFORECLASSENDS
                age
            }
            settings
        }
    }
    
    
    WebsiteSettings setWebsiteSettings(WebsiteSettings settings) {
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)
        
        new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS,context).booleanValue = settings.enableSocialMedia
        new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS_COURSE, context).booleanValue = settings.enableForCourse
        new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE, context).booleanValue = settings.enableForWebpage
        new GetPreference(college, ADDTHIS_PROFILE_ID, context).stringValue = settings.addThisId
        new GetPreference(college, HIDE_CLASS_ON_WEB_AGE, context).integerValue = settings.classAge.hideClassDays?.toInteger()
        new GetPreference(college, HIDE_CLASS_ON_WEB_AGE_TYPE, context).stringValue = settings.classAge.hideClassCondition?.toString()
        new GetPreference(college, STOP_WEB_ENROLMENTS_AGE, context).integerValue = settings.classAge.stopWebEnrolmentDays?.toInteger()
        new GetPreference(college, STOP_WEB_ENROLMENTS_AGE_TYPE, context).stringValue = settings.classAge.stopWebEnrolmentCondition?.toString()
        
        context.commitChanges()
        return settings
    }

    private ClientErrorException createClientException(String message) {
        logger.error("$message, server name: $requestService.request.serverName")
        new ClientErrorException(Response.status(400).entity(new CommonError(message: message)).build())
    }
}

