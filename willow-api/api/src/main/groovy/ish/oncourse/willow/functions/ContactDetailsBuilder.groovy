package ish.oncourse.willow.functions

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.common.field.ContextType
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.common.field.PropertyGetSet
import ish.oncourse.common.field.PropertyGetSetFactory
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Field
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.FieldConfigurationScheme
import ish.oncourse.model.Product
import ish.oncourse.willow.functions.field.GetFieldConfigurations
import ish.oncourse.willow.model.field.ContactFields
import ish.oncourse.willow.model.field.DataType
import ish.oncourse.willow.model.field.FieldHeading
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.model.web.FieldSet
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ContactDetailsBuilder {
    
    final static Logger logger = LoggerFactory.getLogger(ContactDetailsBuilder.class)

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

        ContactFields result = new ContactFields(contactId: contact.id.toString())

        List<FieldConfiguration> configurations =  
                new GetFieldConfigurations(classes: classes, contact: contact, college: contact.college, mergeDefault: mergeDefault, fieldSet: fieldSet).get()


        
        
        
        PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.model')
        Map<Field, Class> fields = [:]
        
        configuration.fields.each { f ->
            
            FieldProperty property = FieldProperty.getByKey(f.property)
            Object source = getContext.call(property.contextType, contact)
            
            
            PropertyGetSet getSet  = factory.get(f, source)
            
            if (getSet.get() != null) {
                fields[f] = getSet.type
            }
        }

        FieldHeading dummy = new FieldHeading(name: 'Dummy', description: 'Dummy')

        dummy.fields += fields.entrySet().findAll { !it.key.fieldHeading }.sort { it.key.order }.collect { toField it.key,it.value  }
        
        fields.entrySet().findAll { it.key.fieldHeading }.groupBy { it.key.fieldHeading }.each { heading, headingFields ->
            classHeadings.headings << new FieldHeading().with { h ->
                h.name = heading.name
                h.description = heading.description
                h.fields += headingFields.sort { it.key.order }.collect { toField it.key, it.value }
                h
            }
        }

        classHeadings.headings = classHeadings.headings.sort {it -> it.name}
        classHeadings
        
    }

    private ish.oncourse.willow.model.field.Field toField(Field field, Class aClass) {
        new ish.oncourse.willow.model.field.Field().with { f ->
            f.id = field.id.toString()
            f.name = field.name
            f.description = field.description
            f.mandatory = field.mandatory
            f.key = field.property
            f.defaultValue = field.defaultValue

            if (FieldProperty.EMAIL_ADDRESS.key == field.property) {
                f.dataType = DataType.EMAIL
            } else if (aClass.enum) {
                f.dataType = DataType.ENUM
                f.enumType = aClass.simpleName
                
                aClass.enumConstants.each { DisplayableExtendedEnumeration item ->
                    f.enumItems  << new Item(value: item.displayName, key: item.databaseValue as Integer)
                }
            } else {
                f.dataType = DataType.fromValue(aClass.simpleName.toLowerCase())
            }
            
            f
        }
    }
}