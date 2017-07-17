package ish.oncourse.willow.functions.field

import ish.oncourse.model.College
import ish.oncourse.model.FieldConfiguration
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GetDefaultFieldConfiguration {
    
    final static Logger logger = LoggerFactory.getLogger(GetDefaultFieldConfiguration)

    College college
    ObjectContext context

    GetDefaultFieldConfiguration(College college, ObjectContext context) {
        this.college = college
        this.context = context
    }

    FieldConfiguration get() {
        FieldConfiguration configuration =  (ObjectSelect.query(FieldConfiguration)
                .where(FieldConfiguration.COLLEGE.eq(college))
                & FieldConfiguration.ANGEL_ID.eq(-1L))
                .prefetch(FieldConfiguration.FIELD_HEADINGS.disjoint())
                .prefetch(FieldConfiguration.FIELDS.disjoint())
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroup(FieldConfiguration.class.simpleName)
                .selectFirst(college.objectContext)

        if (!configuration) {
            logger.error("College (id: $college.id) has no default field configuration")
            throw new IllegalStateException("College (id: $college.id) has no default field configuration")
        } else {
            configuration
        }
    }
}
