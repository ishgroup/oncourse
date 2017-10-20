package ish.oncourse.willow.functions.field

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Field
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.willow.model.field.FieldHeading

class GetWaitingListFields {

    private CourseClass courseClass

    GetWaitingListFields(CourseClass courseClass) {
        this.courseClass = courseClass
    }

    List<FieldHeading> get() {
        FieldConfiguration configuration = courseClass.course.fieldConfigurationScheme?.waitingListFieldConfiguration?: new GetDefaultFieldConfiguration(courseClass.college, courseClass.objectContext).get()
        Set<Field> waitingListFields = configuration.fields.findAll { f -> FieldProperty.getByKey(f.property) == FieldProperty.CUSTOM_FIELD_WAITING_LIST }.toSet()
        return FieldHelper.valueOf(waitingListFields).buildFieldHeadings()
    }
}
