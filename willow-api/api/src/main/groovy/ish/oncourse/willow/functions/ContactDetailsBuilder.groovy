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
import ish.oncourse.willow.model.field.Choice
import ish.oncourse.willow.model.field.ClassHeadings
import ish.oncourse.willow.model.field.DataType
import ish.oncourse.willow.model.field.FieldHeading
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

    ClassHeadings getContactDetails(Contact contact, CourseClass courseClass, FieldSet fieldSet) {

        PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.model')
        Map<Field, Class> fields = [:]
        
        
        FieldConfiguration configuration
        FieldConfigurationScheme scheme = courseClass.course.fieldConfigurationScheme
        if(!scheme) {
            configuration =  getDefaultFieldConfiguration(courseClass.course.college)
        } else {
            switch (fieldSet) {
                case FieldSet.ENROLMENT:
                    configuration = scheme.enrolFieldConfiguration
                    break
                case FieldSet.WAITINGLIST:
                    configuration = scheme.waitingListFieldConfiguration
                    break
                case FieldSet.APPLICATION:
                    configuration = scheme.applicationFieldConfiguration
                    break
                case FieldSet.MAILINGLIST:
                    configuration = getDefaultFieldConfiguration(courseClass.course.college)
                    break
                default: throw new IllegalArgumentException()
            }
        }
        
        configuration.fields.each { f ->
            
            FieldProperty property = FieldProperty.getByKey(f.property)
            Object source = getContext.call(property.contextType, contact)
            
            
            PropertyGetSet getSet  = factory.get(f, source)
            
            if (!getSet.get()) {
                fields[f] = getSet.type
            }
        }
        ClassHeadings classHeadings = new ClassHeadings(classId: courseClass.id.toString())
        FieldHeading dummy = new FieldHeading(name: 'Dummy', description: 'Dummy')

        classHeadings.dummyHeading = dummy
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
    
    private FieldConfiguration getDefaultFieldConfiguration(College college) {
       FieldConfiguration configuration =  (ObjectSelect.query(FieldConfiguration)
               .where(FieldConfiguration.COLLEGE.eq(college)) 
               & ExpressionFactory.matchDbExp(FieldConfiguration.ID_PK_COLUMN, -1))
                .prefetch(FieldConfiguration.FIELD_HEADINGS.joint())
                .prefetch(FieldConfiguration.FIELDS.joint())
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroups(FieldConfiguration.class.simpleName)
                .selectFirst(college.objectContext)
        
        if (!configuration) {
            logger.error("College (id: $college.id) has no default field configuration")
            throw new IllegalStateException()
        }
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
                    f.choices << new Choice(displayName: item.displayName, databaseValue: item.databaseValue as Integer)
                }
            } else {
                f.dataType = DataType.fromValue(aClass.simpleName.toLowerCase())
            }
            
            f
        }
    }
}