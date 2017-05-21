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
class ContactDetailsBuilder {
    
    final static Logger logger = LoggerFactory.getLogger(ContactDetailsBuilder.class)
    
    ContactFields result = new ContactFields()
    FieldHeading dummy = new FieldHeading()
    Map<String, FieldHeading> headingsMap = new HashMap<>()
    

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
    
    ContactFields getContactDetails(Contact contact, List<CourseClass> classes, boolean mergeDefault, FieldSet fieldSet) {

        result.contactId = contact.id.toString()

        //get corresponded field configuration for all classes 
        List<FieldConfiguration> configurations =  
                new GetFieldConfigurations(classes: classes, contact: contact, college: contact.college, mergeDefault: mergeDefault, fieldSet: fieldSet).get()

        PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.model')
        (configurations*.fields.flatten()  as List<Field>)                                   // collect all fields in single list
                .groupBy { f -> f.property }                                      // group by unique key to map like [key1: [field1, field2,...], key2: [field3, field4,...],... ]
                .values()                                                               // get list of lists 
                .collect { List<Field> list ->  list.sort { !it.mandatory }[0] }         // get first mandatory field (if mandatory field there) from each list
                .each { f ->                                                            // sort out each field
                    FieldProperty property = FieldProperty.getByKey(f.property)
                    Object source = getContext.call(property.contextType, contact)
                    PropertyGetSet getSet  = factory.get(f, source)
                    
                    if (getSet.get() == null) {                                         // add field in result list if contact has no value for corresponded property
                        FieldHeading heading = getHeadingBy(f.fieldHeading)             // get heading by name or create new on if not exist
                        heading.fields << new FieldBuilder(field: f, aClass: getSet.type).build()                              // create rest 'field' based on data type and persistent 'field'. Add to corresponded heading
                    }
                }
        
        result.headings << dummy                                                        // add dummy heading and real headings 
        result.headings += headingsMap.values()                                         // to resulted container
        
        result.headings =  result.headings.sort { a, b ->                               // sort all headings by ordering, dummy heading should be first
            a.name == null ? -1 : b.name == null ? 1 : a.ordering <=> b.ordering
        }
        
        result.headings.each { h ->                                                     // sort fields inside each heading by sortOrdering
            h.fields = h.fields.sort { it.ordering }
        }
        
        result
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

    
}