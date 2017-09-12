package ish.oncourse.willow.functions.field

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.CourseClass
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.willow.model.field.Field
import ish.oncourse.willow.model.field.FieldHeading

class GetEnrolmentFields {
    
    private CourseClass courseClass

    GetEnrolmentFields(CourseClass courseClass) {
        this.courseClass = courseClass
    }
    
    List<FieldHeading> get() {
        FieldConfiguration configuration = courseClass.course.fieldConfigurationScheme?.enrolFieldConfiguration?: new GetDefaultFieldConfiguration(courseClass.college, courseClass.objectContext).get()
        Set<ish.oncourse.model.Field> enrolmentCustomFields = configuration.fields.findAll { f -> FieldProperty.getByKey(f.property) == FieldProperty.CUSTOM_FIELD_ENROLMENT }.toSet()
        return FieldHelper.valueOf(enrolmentCustomFields).buildFieldHeadings()
    }
}
