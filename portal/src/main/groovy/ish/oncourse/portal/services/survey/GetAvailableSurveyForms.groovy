package ish.oncourse.portal.services.survey

import ish.common.types.DeliverySchedule
import ish.oncourse.model.CourseClass
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.SurveyFieldConfiguration
import org.apache.cayenne.query.ObjectSelect

import static ish.common.types.DeliverySchedule.*

class GetAvailableSurveyForms {
    private static final Long DEFAULT_FORM_ANGEL_ID = -2
    private CourseClass courseClass
    
    static GetAvailableSurveyForms valueOf(CourseClass courseClass) {
        GetAvailableSurveyForms forms = new GetAvailableSurveyForms()
        forms.courseClass = courseClass
        forms
    }

    
    List<FieldConfiguration> get() {
        List<DeliverySchedule> cases
        Date now = new Date()
        List<FieldConfiguration> forms = courseClass.course.fieldConfigurationScheme.surveyFieldConfigurations

        if (forms.empty) {
            FieldConfiguration defaultForm = getDefaultForm()

            if (defaultForm != null) {
                forms << defaultForm
            } else {
                return null
            }
        }

        Date start = courseClass.startDate
        Date end = courseClass.endDate

        if (courseClass.isDistantLearningCourse
                || start == null
                || end == null) {
            cases = [ON_ENROL, ON_START, MIDWAY, AT_COMPLETION]
        } else {

            Date midway = getClassMidway()
            if (start.after(now)) {
                cases = [ON_ENROL]
            } else if (midway.after(now) ) {
                cases = [ON_ENROL, ON_START]
            } else  if (end.after(now) ){
                cases = [ON_ENROL, ON_START, MIDWAY]
            } else {
                cases = [ON_ENROL, ON_START, MIDWAY, AT_COMPLETION]
            }
        }
        
        return forms.findAll { f -> (f as SurveyFieldConfiguration).deliverySchedule in cases }

    }


    private Date getClassMidway() {
        if (courseClass.sessions.empty) {
            int sessionsCount =  courseClass.sessions.size()
            return courseClass.sessions[(sessionsCount/2).intValue() - 1].endDatetime
        } else {
            return new Date(((courseClass.endDate.time + courseClass.startDate.time)/2).longValue())
        }

    }

    private FieldConfiguration getDefaultForm() {
        return ObjectSelect.query(FieldConfiguration)
                .where(FieldConfiguration.ANGEL_ID.eq(DEFAULT_FORM_ANGEL_ID))
                .and(FieldConfiguration.COLLEGE.eq(courseClass.college))
                .selectOne(courseClass.objectContext)
    }

}
