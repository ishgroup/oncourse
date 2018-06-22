package ish.oncourse.willow.functions.field

import groovy.transform.CompileStatic
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
    private WebSite webSite
    private List<String> classIds
    private List<String> courseIds
    private boolean hasProducts
    private boolean mandatoryOnly

    GetContactFields(Contact contact, WebSite webSite, List<String> classIds, List<String> courseIds, List<String> productIds, boolean mandatoryOnly) {
        this.contact = contact
        this.webSite = webSite
        this.hasProducts = !productIds.empty
        this.mandatoryOnly = mandatoryOnly
        this.classIds = classIds
        this.courseIds = courseIds

    }
    
    ContactFields getContactFields() {
        //get corresponded field configuration for all classes 
        Set<FieldConfiguration> configurations = getFieldConfigurations()
        
        Set<Field> fields = mergeFieldConfigurations(configurations)
        
        ContactFields result = new ContactFields()
        result.contactId = contact.id.toString()
        result.headings = FieldHelper.valueOf(mandatoryOnly, contact, webSite, fields).buildFieldHeadings()
        return result
    }
    
    private Set<Field> mergeFieldConfigurations(Set<FieldConfiguration> configurations) {
       (configurations*.fields.flatten()  as List<Field>)                                   // collect all fields in single list
                .groupBy { f -> f.property }                                      // group by unique key to map like [key1: [field1, field2,...], key2: [field3, field4,...],... ]
                .values()                                                               // get list of lists 
                .collect { List<Field> list ->  list.sort { !it.mandatory }[0] }         // get first mandatory field (if mandatory field there) from each list
                .toSet()
    }
    
    private Set<FieldConfiguration> getFieldConfigurations() {
        Set<FieldConfiguration> configurations = []
        if (!classIds.empty) {
            List<CourseClass> classes = (ObjectSelect.query(CourseClass)
                    .where(ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, classIds))
                    & CourseClass.COLLEGE.eq(contact.college))
                    .prefetch(CourseClass.COURSE.joint())
                    .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                    .cacheGroup(CourseClass.class.simpleName)
                    .select(contact.objectContext)

            classes*.course.findAll { c -> c.fieldConfigurationScheme }.unique().each { c ->
                if (new FindOfferedApplication(c, contact.student, contact.objectContext).applcation) {
                    configurations << c.fieldConfigurationScheme.applicationFieldConfiguration
                } else {
                    configurations << c.fieldConfigurationScheme.enrolFieldConfiguration
                }
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