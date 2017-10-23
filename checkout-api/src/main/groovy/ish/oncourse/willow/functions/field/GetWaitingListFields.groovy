package ish.oncourse.willow.functions.field

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.Course
import ish.oncourse.model.Field
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.willow.model.field.FieldHeading

class GetWaitingListFields {

    private Course course

    GetWaitingListFields(Course course) {
        this.course = course
    }

    List<FieldHeading> get() {
        FieldConfiguration configuration = course.fieldConfigurationScheme?.waitingListFieldConfiguration?: new GetDefaultFieldConfiguration(course.college, course.objectContext).get()
        Set<Field> waitingListFields = configuration.fields.findAll { f -> FieldProperty.getByKey(f.property) == FieldProperty.CUSTOM_FIELD_WAITING_LIST }.toSet()
        return FieldHelper.valueOf(waitingListFields).buildFieldHeadings()
    }
}
