package ish.oncourse.willow.functions.field

import ish.oncourse.common.field.ContextType
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.model.Field
import ish.oncourse.willow.model.field.ContactFields
import ish.oncourse.willow.model.field.FieldHeading

class ContactFieldHelper {

    static FieldProperty[] credentialProperty = [FieldProperty.FIRST_NAME, FieldProperty.LAST_NAME, FieldProperty.EMAIL_ADDRESS] as FieldProperty[]
    static FieldProperty[] extendedCustomFields = [FieldProperty.CUSTOM_FIELD_STUDENT, FieldProperty.CUSTOM_FIELD_COURSE, FieldProperty.CUSTOM_FIELD_ENROLMENT,
                                                   FieldProperty.CUSTOM_FIELD_APPLICATION, FieldProperty.CUSTOM_FIELD_WAITING_LIST] as FieldProperty[]

   
    private boolean mandatoryOnly
    private Contact contact
    private Set<Field> fields

    private PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.model')
    private FieldHeading dummy = new FieldHeading()
    private Map<String, FieldHeading> headingsMap = new HashMap<>()
    private ContactFields result = new ContactFields()


    static Closure getContext = { ContextType contextType, Contact contact ->
        switch (contextType) {
            case ContextType.CONTACT:
                contact
                break
            case ContextType.STUDENT:
                contact.student
                break
            default:
                throw new IllegalArgumentException()
        }
    }

    ContactFieldHelper(boolean mandatoryOnly, Contact contact, Set<Field> fields) {
        this.mandatoryOnly = mandatoryOnly
        this.contact = contact
        this.fields = fields
    }

    ContactFields buildContactFieldsResult() {
        result.contactId = contact.id.toString()
        fillHeadingsByFields()
        sortFields()
        return result
    }
    
    private void fillHeadingsByFields() {
        fields.each { f ->                                                            // sort out each field
            FieldProperty property = FieldProperty.getByKey(f.property)

            if (!credentialProperty.contains(property) && !extendedCustomFields.contains(property)) {
                PropertyGetSet getSet  = factory.get(f, getContext.call(property.contextType, contact))
                if (!mandatoryOnly || (f.mandatory && getSet.get() == null)) {
                    getHeadingBy(f.fieldHeading).fields << new FieldBuilder(field: f, aClass: getSet.type).build()               // create rest 'field' based on data type and persistent 'field'. Add to corresponded heading
                }
            }
        }
    }


    private FieldHeading getHeadingBy(ish.oncourse.model.FieldHeading heading) {
        if (!heading) {
            return dummy
        }

        FieldHeading result = headingsMap.get(heading.name)
        if (!result) {
            result = new FieldHeading(name: heading.name, description: heading.description, ordering: heading.order)
            headingsMap[heading.name] = result
        }
        result
    }

    private void sortFields() {
        if (!dummy.fields.empty) {
            result.headings << dummy                                                    // add dummy heading and real headings 
        }
        result.headings += headingsMap.values()                                         // to resulted container
        result.headings =  result.headings.sort { a, b ->                               // sort all headings by ordering, dummy heading should be first
            a.name == null ? -1 : b.name == null ? 1 : a.ordering <=> b.ordering
        }
        result.headings.each { h ->                                                     // sort fields inside each heading by sortOrdering
            h.fields = h.fields.sort { it.ordering }
        }
    }

    ContactFields getResult() {
        return result
    }
}
