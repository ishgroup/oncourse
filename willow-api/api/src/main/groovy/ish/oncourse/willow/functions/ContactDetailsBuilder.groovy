package ish.oncourse.willow.functions

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
import ish.oncourse.willow.model.field.ClassHeadings
import ish.oncourse.willow.model.field.FieldHeading
import ish.oncourse.willow.model.web.FieldSet
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ContactDetailsBuilder {
    
    final static Logger logger = LoggerFactory.getLogger(ContactDetailsBuilder.class)

    ClassHeadings getContactDetails(Contact contact, CourseClass courseClass, FieldSet fieldSet) {

        PropertyGetSetFactory factory = new PropertyGetSetFactory('ish.oncourse.model')
        List<Field> fields = new ArrayList<>()
        
        
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
        ClassHeadings classHeadings = new ClassHeadings(classId: courseClass.id)
        FieldHeading dummy = new FieldHeading(name: 'Dummy')
        classHeadings.headings << dummy
        dummy.fields += fields.findAll { !it.fieldHeading }.collect { toField it}
        
        classHeadings
    }
    
    private FieldConfiguration getDefaultFieldConfiguration(College college) {
       FieldConfiguration configuration =  (((ObjectSelect.query(FieldConfiguration)
                .where(FieldConfiguration.COLLEGE.eq(college))
                & FieldConfiguration.ENROL_FIELD_CONFIGURATION_SCHEMES.outer().isNull())
                & FieldConfiguration.WAITING_LIST_FIELD_CONFIGURATION_SCHEMES.outer().isNull())
                & FieldConfiguration.APPLICATION_FIELD_CONFIGURATION_SCHEMES.outer().isNull())
                .prefetch(FieldConfiguration.FIELD_HEADINGS.joint())
                .prefetch(FieldConfiguration.FIELDS.joint())
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .selectFirst(college.objectContext)
        
        if (!configuration) {
            logger.error("College (id: $college.id) has no default field configuration")
            throw new IllegalStateException()
        }
    }

    private ish.oncourse.willow.model.field.Field toField(Field field) {
        new ish.oncourse.willow.model.field.Field().with {
            it.id = field.id.toString()
            
            it
        }
    }
}