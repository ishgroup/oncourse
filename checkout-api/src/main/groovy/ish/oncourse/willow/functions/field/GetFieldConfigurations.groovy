package ish.oncourse.willow.functions.field

import ish.oncourse.model.*
import ish.oncourse.services.application.FindOfferedApplication
import ish.oncourse.willow.model.field.FieldSet
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GetFieldConfigurations {

    final static Logger logger = LoggerFactory.getLogger(GetFieldConfigurations.class)


    List<CourseClass> classes
    Contact contact
    ObjectContext context

    FieldSet fieldSet
    College college
    boolean mergeDefault = false


    Set<FieldConfiguration> get() {
        Set<FieldConfiguration> configurations = []

        if (mergeDefault) {
            configurations << new GetDefaultFieldConfiguration(college, context).get()
        }
        
        switch (fieldSet) {
            case FieldSet.ENROLMENT:

                classes.each { c ->

                    FieldConfigurationScheme scheme
                    
                    if (c.course.fieldConfigurationScheme) {
                        scheme = c.course.fieldConfigurationScheme
                        if (new FindOfferedApplication(c.course, contact.student, context).applcation) {
                            configurations << scheme.applicationFieldConfiguration
                        } else {
                            configurations << scheme.enrolFieldConfiguration
                        }
                    } else if (!mergeDefault) {
                        configurations <<  new GetDefaultFieldConfiguration(college, context).get()
                        mergeDefault = true
                    }
                    
                }
                
                break
            case FieldSet.WAITINGLIST:
                if (classes.size() != 1) {
                    logger.error("More than one class in waiting list, classes: ${classes*.id}, contactId: ${contact.id}")
                    throw new IllegalArgumentException('More than one class in waiting list')  
                }
                if (classes[0].course.fieldConfigurationScheme) {
                    configurations << classes[0].course.fieldConfigurationScheme.waitingListFieldConfiguration
                } else {
                    configurations <<  new GetDefaultFieldConfiguration(college, context).get()
                }
                break
            case FieldSet.MAILINGLIST:
                logger.error("$fieldSet processing not supported yet")
                throw new UnsupportedOperationException("$fieldSet processing not supported yet")
            default:
                logger.error("Unknown fieldSet: $fieldSet")
                throw new IllegalArgumentException()
        }
        configurations
    }
}
