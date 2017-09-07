package ish.oncourse.willow.functions.field

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.model.field.Field

class GetEnrolmentFields {
    
    private CourseClass courseClass

    GetEnrolmentFields(CourseClass courseClass) {
        this.courseClass = courseClass
    }
    
    List<Field> get() {
        List<ish.oncourse.model.Field> allFields = courseClass.course.fieldConfigurationScheme?.enrolFieldConfiguration?.fields?: new GetDefaultFieldConfiguration(courseClass.college, courseClass.objectContext).get().fields
        List<Field> result = []
        allFields.findAll { f -> FieldProperty.getByKey(f.property) == FieldProperty.CUSTOM_FIELD_ENROLMENT }
                .each { f -> result << new FieldBuilder(field: f).build() }
        result
    }
}
