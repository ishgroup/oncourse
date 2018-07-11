package ish.oncourse.willow.functions.field

import groovy.transform.CompileStatic
import ish.common.types.FieldConfigurationType
import ish.oncourse.model.Contact
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Field
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.WebSite
import ish.oncourse.services.application.FindOfferedApplication
import ish.oncourse.willow.model.field.ContactFields
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class GetContactFields {
    
    final static Logger logger = LoggerFactory.getLogger(GetContactFields.class)
    
    private Contact contact
    private List<Course> coursesByClassIds
    private List<String> courseIds
    private boolean hasProducts
    private boolean mandatoryOnly

    GetContactFields(Contact contact, List<Course> coursesByClassIds, List<String> courseIds, List<String> productIds, boolean mandatoryOnly) {
        this.contact = contact
        this.hasProducts = !productIds.empty
        this.mandatoryOnly = mandatoryOnly
        this.coursesByClassIds = coursesByClassIds
        this.courseIds = courseIds
    }
    
    List<Field> getFields() {
       fieldConfigurations*.fields.flatten() as List<Field>
    }
    
    private Set<FieldConfiguration> getFieldConfigurations() {
        Set<FieldConfiguration> configurations = []

        coursesByClassIds.each { c ->
            if (new FindOfferedApplication(c, contact.student, contact.objectContext).applcation) {
                configurations << c.fieldConfigurationScheme.applicationFieldConfiguration
            } else {
                configurations << c.fieldConfigurationScheme.enrolFieldConfiguration
            }
        }

        if (!courseIds.empty) {
            (ObjectSelect.query(Course)
                    .where(ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, courseIds))
                    & Course.COLLEGE.eq(contact.college)
                    & Course.FIELD_CONFIGURATION_SCHEME.isNotNull())
                    .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                    .cacheGroup(Course.class.simpleName)
                    .select(contact.objectContext).each { c ->
                configurations << c.fieldConfigurationScheme.waitingListFieldConfiguration
            }
        }
        
        if (hasProducts || configurations.empty) {
            configurations << new GetDefaultFieldConfiguration(contact.college, contact.objectContext).get()
        }
        configurations
    }
}