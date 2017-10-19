package ish.oncourse.willow.functions.field

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Field
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.willow.model.field.FieldHeading

class GetApplicationFields {

    private CourseClass courseClass

    GetApplicationFields(CourseClass courseClass) {
        this.courseClass = courseClass
    }

    List<FieldHeading> get() {
        FieldConfiguration configuration = courseClass.course.fieldConfigurationScheme?.applicationFieldConfiguration?: new GetDefaultFieldConfiguration(courseClass.college, courseClass.objectContext).get()
        Set<Field> applicationCustomFields = configuration.fields.findAll { f -> FieldProperty.getByKey(f.property) == FieldProperty.CUSTOM_FIELD_APPLICATION }.toSet()
        return FieldHelper.valueOf(applicationCustomFields).buildFieldHeadings()
    }
}
