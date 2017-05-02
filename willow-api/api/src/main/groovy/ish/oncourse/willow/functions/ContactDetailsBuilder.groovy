package ish.oncourse.willow.functions

import ish.oncourse.common.field.ContextType
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.model.Course
import ish.oncourse.model.Field
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.willow.model.FieldSet

class ContactDetailsBuilder {

    List<Field> getContactDetails(Contact contact, Course courses, FieldSet fieldSet) {

        PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.model')
        List<Field> fields = new ArrayList<>()

        courses.each { c ->
            
            FieldConfiguration scheme
            switch (fieldSet) {
                case FieldSet.ENROLMENT:
                    scheme = c.fieldConfigurationScheme.enrolFieldConfiguration
                    break
                case FieldSet.WAITINGLIST:
                    scheme = c.fieldConfigurationScheme.waitingListFieldConfiguration
                    break
                case FieldSet.APPLICATION:
                    scheme = c.fieldConfigurationScheme.applicationFieldConfiguration
                    break
                default: throw new IllegalArgumentException()
            }

            scheme.fields.each { f ->
                
                FieldProperty property = FieldProperty.getByKey(f.property)
                Object source
                switch (property.contextType) {
                    case ContextType.CONTACT:
                        source = contact
                        break
                    case ContextType.STUDENT:
                        source = contact.student
                        break
                    default:
                        throw new IllegalArgumentException()
                }
                
                PropertyGetSet getSet  = factory.get(f, source)
                if (!getSet.get()) {
                    fields << f
                }
            }
        }
        fields
    }
}