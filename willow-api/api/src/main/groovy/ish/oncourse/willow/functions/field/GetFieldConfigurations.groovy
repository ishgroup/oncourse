package ish.oncourse.willow.functions.field

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.FieldConfigurationScheme
import ish.oncourse.services.application.FindOfferedApplication
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.model.web.FieldSet
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GetFieldConfigurations {

    final static Logger logger = LoggerFactory.getLogger(GetFieldConfigurations.class)


    List<CourseClass> classes
    Contact contact

    FieldSet fieldSet
    College college
    boolean mergeDefault = false


    List<FieldConfiguration> get() {
        List<FieldConfiguration> configurations = []

        if (mergeDefault) {
            configurations << getDefaultFieldConfiguration(college)
        }
        
        switch (fieldSet) {
            case FieldSet.ENROLMENT:

                classes.each { c ->

                    FieldConfigurationScheme scheme
                    
                    if (c.course.fieldConfigurationScheme) {
                        scheme = c.course.fieldConfigurationScheme
                        if (new FindOfferedApplication(c.course, contact.student, contact.objectContext).applcation) {
                            configurations << scheme.applicationFieldConfiguration
                        } else {
                            configurations << scheme.enrolFieldConfiguration
                        }
                    } else if (!mergeDefault) {
                        configurations << getDefaultFieldConfiguration(college)
                        mergeDefault = true
                    }
                    
                }
                
                break
            case FieldSet.WAITINGLIST:
            case FieldSet.MAILINGLIST:
                logger.error("$fieldSet processing not supported yet")
                throw new UnsupportedOperationException("$fieldSet processing not supported yet")
            default:
                logger.error("Unknown fieldSet: $fieldSet")
                throw new IllegalArgumentException()
        }
        configurations
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
    
}
