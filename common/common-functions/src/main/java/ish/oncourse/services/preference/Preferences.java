/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.preference;

public interface Preferences {
    String STOP_WEB_ENROLMENTS_AGE = "stop.web.enrolments.age";
    String STOP_WEB_ENROLMENTS_AGE_TYPE = "stop.web.enrolments.age.type";

    String NTIS_LAST_UPDATE = "ntis.lastupdate";
    String POSTCODES_LAST_UPDATE = "postcodes.lastupdate";
    String HIDE_STUDENT_DETAILS_FROM_TUTOR = "student.details.hidden";
    String TUTOR_FEEDBACK_EMAIL = "tutor.feedbackemail";
    String OUTCOME_MARKING_VIA_PORTAL = "outcome.marking.via.portal";
    String ENABLE_SOCIAL_MEDIA_LINKS = "website.medialinks.enabled";
    String ENABLE_SOCIAL_MEDIA_LINKS_COURSE = "website.course.medialinks.enabled";
    String ENABLE_SOCIAL_MEDIA_LINKS_WEB_PAGE = "website.webpage.medialinks.enabled";
    String ADDTHIS_PROFILE_ID = "website.medialinks.addthis";
    String ENROLMENT_CREDIT_NOTE_ENABLED = "enrolment.credit.note.enabled";
    String SUBURB_AUTOCOMPLITE_STATE = "feature.suburb.autocomplete.states";

    String CHECKOUT_guardianRequiredAge = "checkout.guardianRequiredAge";

    String CHECKOUT_TERMS_URL = "checkout.terms.url";
    String CHECKOUT_TERMS_LABEL = "checkout.terms.label";
    String CHECKOUT_createContactAllowed = "checkout.createContactAllowed";
    
    int DEFAULT_contactAgeWhenNeedParent = 18;

    String HIDE_CLASS_ON_WEB_AGE = "hide.class.on.web.age";
    String HIDE_CLASS_ON_WEB_AGE_TYPE = "hide.class.on.web.age.type";
    
    enum ContactFieldSet {
        enrolment,
        waitinglist,
    }
    
}
