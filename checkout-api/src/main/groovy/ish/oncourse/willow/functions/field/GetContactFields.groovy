package ish.oncourse.willow.functions.field

import groovy.transform.CompileStatic
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Field
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.willow.model.field.ContactFields
import ish.oncourse.willow.model.field.FieldSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class GetContactFields {
    
    final static Logger logger = LoggerFactory.getLogger(GetContactFields.class)

    
    private Contact contact
    private List<CourseClass> classes
    private boolean mergeDefault
    private FieldSet fieldSet
    private boolean mandatoryOnly


    GetContactFields(Contact contact, List<CourseClass> classes, boolean mergeDefault, FieldSet fieldSet, boolean mandatoryOnly) {
        this.contact = contact
        this.classes = classes
        this.mergeDefault = mergeDefault
        this.fieldSet = fieldSet
        this.mandatoryOnly = mandatoryOnly
    }
    
    ContactFields getContactFields() {
        //get corresponded field configuration for all classes 
        Set<FieldConfiguration> configurations =  new GetFieldConfigurations(classes: classes, contact: contact, college: contact.college, mergeDefault: mergeDefault, fieldSet: fieldSet, context: contact.objectContext).get()
        Set<Field> fields = mergeFieldConfigurations(configurations)
        
        ContactFields result = new ContactFields()
        result.contactId = contact.id.toString()
        result.headings = FieldHelper.valueOf(mandatoryOnly, contact, fields).buildFieldHeadings()
        return result
    }
    
    private Set<Field> mergeFieldConfigurations(Set<FieldConfiguration> configurations) {
       (configurations*.fields.flatten()  as List<Field>)                                   // collect all fields in single list
                .groupBy { f -> f.property }                                      // group by unique key to map like [key1: [field1, field2,...], key2: [field3, field4,...],... ]
                .values()                                                               // get list of lists 
                .collect { List<Field> list ->  list.sort { !it.mandatory }[0] }         // get first mandatory field (if mandatory field there) from each list
                .toSet()
    }
}