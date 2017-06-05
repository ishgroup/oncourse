package ish.oncourse.willow.functions

import groovy.transform.CompileStatic
import ish.oncourse.common.field.ContextType
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Field
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.willow.functions.field.FieldBuilder
import ish.oncourse.willow.functions.field.GetFieldConfigurations
import ish.oncourse.willow.model.field.ContactFields
import ish.oncourse.willow.model.field.FieldHeading
import ish.oncourse.willow.model.field.FieldSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class GetContactFields {
    
    final static Logger logger = LoggerFactory.getLogger(GetContactFields.class)
    
    static FieldProperty[] credentialProperty = [FieldProperty.FIRST_NAME, FieldProperty.LAST_NAME, FieldProperty.EMAIL_ADDRESS] as FieldProperty[]
    
    ContactFields result = new ContactFields()
    FieldHeading dummy = new FieldHeading()
    Map<String, FieldHeading> headingsMap = new HashMap<>()
    PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.model')

    Contact contact
    List<CourseClass> classes
    boolean mergeDefault
    FieldSet fieldSet
    boolean mandatoryOnly

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

    GetContactFields(Contact contact, List<CourseClass> classes, boolean mergeDefault, FieldSet fieldSet, boolean mandatoryOnly) {
        this.contact = contact
        this.classes = classes
        this.mergeDefault = mergeDefault
        this.fieldSet = fieldSet
        this.mandatoryOnly = mandatoryOnly
    }
    
    ContactFields getContactFields() {
        result.contactId = contact.id.toString()
        
        //get corresponded field configuration for all classes 
        Set<FieldConfiguration> configurations =  new GetFieldConfigurations(classes: classes, contact: contact, college: contact.college, mergeDefault: mergeDefault, fieldSet: fieldSet).get()
        Set<Field> fields = mergeFieldConfigurations(configurations)
        
        fillHeadingsByFields(fields)
        sortFields()
        
        return result
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
    
    private Set<Field> mergeFieldConfigurations(Set<FieldConfiguration> configurations) {
       (configurations*.fields.flatten()  as List<Field>)                                   // collect all fields in single list
                .groupBy { f -> f.property }                                      // group by unique key to map like [key1: [field1, field2,...], key2: [field3, field4,...],... ]
                .values()                                                               // get list of lists 
                .collect { List<Field> list ->  list.sort { !it.mandatory }[0] }         // get first mandatory field (if mandatory field there) from each list
                .toSet()
    }
    
    private void fillHeadingsByFields(Set<Field> fields ) {
        fields.each { f ->                                                            // sort out each field
            FieldProperty property = FieldProperty.getByKey(f.property)
            
            if (!credentialProperty.contains(property)) {
                PropertyGetSet getSet  = factory.get(f, getContext.call(property.contextType, contact))
                if (!mandatoryOnly || (f.mandatory && getSet.get() == null)) {
                    getHeadingBy(f.fieldHeading).fields << new FieldBuilder(field: f, aClass: getSet.type).build()               // create rest 'field' based on data type and persistent 'field'. Add to corresponded heading
                }
            }
        }
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
}