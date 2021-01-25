package ish.oncourse.willow.functions.field

import ish.oncourse.cayenne.FieldInterface
import ish.oncourse.common.field.ContextType
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.model.Field
import ish.oncourse.model.WebSite
import ish.oncourse.willow.model.field.FieldHeading
import org.apache.cayenne.PersistentObject 

class FieldHelper {

    static FieldProperty[] credentialProperty = [FieldProperty.FIRST_NAME, FieldProperty.LAST_NAME, FieldProperty.EMAIL_ADDRESS] as FieldProperty[]
    static FieldProperty[] extendedCustomFields = [FieldProperty.CUSTOM_FIELD_STUDENT, FieldProperty.CUSTOM_FIELD_COURSE, FieldProperty.CUSTOM_FIELD_ENROLMENT,
                                                   FieldProperty.CUSTOM_FIELD_APPLICATION, FieldProperty.CUSTOM_FIELD_WAITING_LIST] as FieldProperty[]

   
    private boolean mandatoryOnly
    private Contact contact
    private WebSite webSite
    private Set<Field> fields

    private PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.model')
    private FieldHeading dummy = new FieldHeading()
    private Map<String, FieldHeading> headingsMap = new HashMap<>()
    private List<FieldHeading> result = []


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

    private ContactFieldHelper(){}

    static FieldHelper valueOf(boolean mandatoryOnly, Contact contact, WebSite webSite, Set<Field> fields) {
        FieldHelper helper = new FieldHelper()
        helper.mandatoryOnly = mandatoryOnly
        helper.contact = contact
        helper.webSite = webSite
        helper.fields = fields
        return helper
    }
    
    static FieldHelper valueOf(Set<Field> fields) {
        FieldHelper helper = new FieldHelper()
        helper.fields = fields
        return helper
    }
    
    
    List<FieldHeading> buildFieldHeadings() {
        fillHeadings()
        sortFields()
        return result
    }
    
    private void fillHeadings() {
        fields.each { f ->                                                            // sort out each field
            if (contact) {
                FieldProperty property = FieldProperty.getByKey(f.property)
                if (property.contextType in [ContextType.CONTACT, ContextType.STUDENT] && !credentialProperty.contains(property) && !extendedCustomFields.contains(property)) {
                    if (!property.key.startsWith(PropertyGetSetFactory.TAG_PATTERN) &&
                            !property.key.startsWith(PropertyGetSetFactory.MAILING_LIST_FIELD_PATTERN)) {
                        PropertyGetSet getSet = factory.get(f, getContext.call(property.contextType, contact))
                        if (!mandatoryOnly || (f.mandatory && getSet.get() == null)) {
                            getHeadingBy(f.fieldHeading).fields << new FieldBuilder(field: f, aClass: getSet.type).build()
                            // create rest 'field' based on data type and persistent 'field'. Add to corresponded heading
                        }
                    } else {
                        if (!mandatoryOnly) {
                            getHeadingBy(f.fieldHeading).fields.addAll(new FieldTagBuilder(field: f, contact: contact, webSite: webSite).build())
                        }
                    }
                }
            } else {
                getHeadingBy(f.fieldHeading).fields << new FieldBuilder(field: f, aClass: String.class).build()
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
            result << dummy                                                    // add dummy heading and real headings 
        }
        result += headingsMap.values()                                         // to resulted container
        result =  result.sort { a, b ->                               // sort all headings by ordering, dummy heading should be first
            a.name == null ? -1 : b.name == null ? 1 : a.ordering <=> b.ordering
        }
        result.each { h ->                                                     // sort fields inside each heading by sortOrdering
            h.fields = h.fields.sort { it.ordering }
        }
    }
    
    static void populateFields(List<FieldHeading> fieldHeadings, PersistentObject record) {
        (fieldHeadings.fields.flatten() as List<ish.oncourse.willow.model.field.Field>).each { f  ->
            Object value =  new FieldValueParser(f, record.objectContext).parse().value
            if  (value) {
                PropertyGetSet getSet = factory.get([getProperty: {f.key}] as FieldInterface, record)
                getSet.set(value)
            }
        }
    }
}
