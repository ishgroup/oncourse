package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.willow.editor.v1.model.ClassStateTransition
import ish.oncourse.willow.editor.v1.model.ClassAge
import ish.oncourse.willow.editor.v1.model.Condition
import ish.oncourse.willow.editor.v1.model.SkillsOnCourseSettings
import ish.oncourse.willow.editor.v1.model.WebsiteSettings

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.v1.service.SettingsApi
import ish.oncourse.willow.editor.website.WebSiteFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

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

    SkillsOnCourseSettings updateSkillsOnCourseSettings(SkillsOnCourseSettings skillsOnCourseSettingsRequest) {
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
                age.hideClass = new ClassStateTransition().with { hideClass ->
                    hideClass.offset = new GetPreference(college, HIDE_CLASS_ON_WEB_AGE, context).integerValue
                    String classCondition = new GetPreference(college, HIDE_CLASS_ON_WEB_AGE_TYPE, context).stringValue
                    hideClass.condition = classCondition? Condition.fromValue(classCondition) : Condition.BEFORECLASSENDS
                    hideClass
                }
                age.stopWebEnrolment = new ClassStateTransition().with { stopWebEnrolment ->

                    stopWebEnrolment.offset = new GetPreference(college, STOP_WEB_ENROLMENTS_AGE, context).integerValue
                    String enrolmentCondition = new GetPreference(college, STOP_WEB_ENROLMENTS_AGE_TYPE, context).stringValue
                    stopWebEnrolment.condition = enrolmentCondition? Condition.fromValue(enrolmentCondition) : Condition.BEFORECLASSENDS
                    stopWebEnrolment
                }
                age
            }
            settings
        }
    }
    
    
    WebsiteSettings updateWebsiteSettings(WebsiteSettings settings) {
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)
        
        new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS,context).booleanValue = settings.enableSocialMedia
        new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS_COURSE, context).booleanValue = settings.enableForCourse
        new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE, context).booleanValue = settings.enableForWebpage
        new GetPreference(college, ADDTHIS_PROFILE_ID, context).stringValue = settings.addThisId
        new GetPreference(college, HIDE_CLASS_ON_WEB_AGE, context).integerValue = settings.classAge?.hideClass?.offset?.toInteger()
        new GetPreference(college, HIDE_CLASS_ON_WEB_AGE_TYPE, context).stringValue = settings.classAge?.hideClass?.condition?.toString()
        new GetPreference(college, STOP_WEB_ENROLMENTS_AGE, context).integerValue = settings.classAge?.stopWebEnrolment?.offset?.toInteger()
        new GetPreference(college, STOP_WEB_ENROLMENTS_AGE_TYPE, context).stringValue = settings.classAge?.stopWebEnrolment?.condition?.toString()
        
        context.commitChanges()
        return settings
    }

}

