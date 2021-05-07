package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.preference.GetAutoCompleteState
import ish.oncourse.services.preference.GetContactAgeWhenNeedParent
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.IsCollectParentDetails
import ish.oncourse.willow.editor.v1.model.CheckoutSettings
import ish.oncourse.willow.editor.v1.model.ClassStateTransition
import ish.oncourse.willow.editor.v1.model.ClassAge
import ish.oncourse.willow.editor.v1.model.Condition
import ish.oncourse.willow.editor.v1.model.SkillsOnCourseSettings
import ish.oncourse.willow.editor.v1.model.State
import ish.oncourse.willow.editor.v1.model.WebsiteSettings

import groovy.transform.CompileStatic
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.v1.service.SettingsApi
import ish.oncourse.willow.editor.website.WebSiteFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.services.preference.Preferences.*
import static ish.oncourse.services.preference.Preferences.ConfigProperty.allowCreateContact

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
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(requestService.request, context)
        College college = webSite.college
        
        return new WebsiteSettings().with { settings ->
            settings.enableSocialMedia = new GetPreference(college,ENABLE_SOCIAL_MEDIA_LINKS,context).booleanValue
            settings.addThisId = new GetPreference(college, ADDTHIS_PROFILE_ID, context).stringValue?:StringUtils.EMPTY
            settings.enableForCourse = new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS_COURSE, context).booleanValue
            settings.enableForWebpage = new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE, context).booleanValue
            settings.googleTM =  webSite.googleTagmanagerAccount
            settings.rootTagFilter = webSite.coursesRootTagName
            String state = new GetAutoCompleteState(college, context, webSite).get()
            settings.suburbAutocompleteState = state ? State.fromValue(state) : null as State
            

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
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(requestService.request, context)
        College college = webSite.college

        new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS,context).booleanValue = settings.enableSocialMedia
        new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS_COURSE, context).booleanValue = settings.enableForCourse
        new GetPreference(college, ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE, context).booleanValue = settings.enableForWebpage
        new GetPreference(college, ADDTHIS_PROFILE_ID, context).stringValue = settings.addThisId
        new GetPreference(college, HIDE_CLASS_ON_WEB_AGE, context).integerValue = settings.classAge?.hideClass?.offset?.toInteger()
        new GetPreference(college, HIDE_CLASS_ON_WEB_AGE_TYPE, context).stringValue = settings.classAge?.hideClass?.condition?.toString()
        new GetPreference(college, STOP_WEB_ENROLMENTS_AGE, context).integerValue = settings.classAge?.stopWebEnrolment?.offset?.toInteger()
        new GetPreference(college, STOP_WEB_ENROLMENTS_AGE_TYPE, context).stringValue = settings.classAge?.stopWebEnrolment?.condition?.toString()
        new GetAutoCompleteState(college, context, webSite).stringValue = settings.suburbAutocompleteState?.toString()
        webSite.googleTagmanagerAccount = StringUtils.trimToNull(settings.googleTM)
        webSite.coursesRootTagName = StringUtils.trimToNull(settings.rootTagFilter)
        context.commitChanges()
        return settings
    }

    @Override
    CheckoutSettings getCheckoutSettings() {
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)
        
        return new CheckoutSettings().with { settings ->
            settings.allowCreateContactOnEnrol = new GetPreference(college, allowCreateContact.getPreferenceNameBy(ContactFieldSet.enrolment), context).booleanValue
            settings.allowCreateContactOnWaitingList = new GetPreference(college, allowCreateContact.getPreferenceNameBy(ContactFieldSet.waitinglist), context).booleanValue
            settings.allowCreateContactOnMailingList = new GetPreference(college, allowCreateContact.getPreferenceNameBy(ContactFieldSet.mailinglist), context).booleanValue

            settings.collectParentDetails = new IsCollectParentDetails(college, context).get() 
            settings.contactAgeWhenNeedParent = new GetContactAgeWhenNeedParent(college, context).get()
            settings.enrolmentMinAge = new GetPreference(college, ENROLMENT_MIN_AGE, context).integerValue
            settings
        }
        
    }

    @Override
    CheckoutSettings updateCheckoutSettings(CheckoutSettings settings) {
        ObjectContext context = cayenneService.newContext()
        College college = WebSiteFunctions.getCurrentCollege(requestService.request, context)

        new GetPreference(college, allowCreateContact.getPreferenceNameBy(ContactFieldSet.enrolment), context).booleanValue = settings.allowCreateContactOnEnrol
        new GetPreference(college, allowCreateContact.getPreferenceNameBy(ContactFieldSet.waitinglist), context).booleanValue = settings.allowCreateContactOnWaitingList
        new GetPreference(college, allowCreateContact.getPreferenceNameBy(ContactFieldSet.mailinglist), context).booleanValue = settings.allowCreateContactOnMailingList
        new IsCollectParentDetails(college, context).booleanValue = settings.collectParentDetails
        new GetContactAgeWhenNeedParent(college, context).integerValue = settings.contactAgeWhenNeedParent
        new GetPreference(college, ENROLMENT_MIN_AGE, context).integerValue = settings.enrolmentMinAge
        
        context.commitChanges()
        
        return settings

    }

}

