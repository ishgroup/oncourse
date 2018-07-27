package ish.oncourse.solr.update.listener

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.commitlog.model.ObjectChange
import org.apache.cayenne.commitlog.model.ObjectChangeType
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.model.auto._CourseClass.ID_PK_COLUMN
import static ish.oncourse.model.auto._CourseClass.IS_WEB_VISIBLE
import static ish.oncourse.model.auto._Session.*

class GetCoursesFromSessions {
    Set<ObjectChange> objectChanges
    ObjectContext objectContext


    static GetCoursesFromSessions valueOf(Set<ObjectChange> objectChanges, ObjectContext objectContext){
        GetCoursesFromSessions condition = new GetCoursesFromSessions()
        condition.objectChanges = objectChanges
        condition.objectContext = objectContext
        condition
    }

    private static final List<String> SESSION_REINDEX_PROPS =
            [END_DATE_PROPERTY,
             START_DATE_PROPERTY,
             TIME_ZONE_PROPERTY,
             ROOM_PROPERTY,]

    private static final List<String> SESSION_REINDEX_TO_ONE_RELATION =
            [ROOM_PROPERTY]

    Set<Long> get(){
        List<Long> sessionIds = objectChanges
                .findAll { change -> change.type == ObjectChangeType.UPDATE  && (change.attributeChanges.any { attr -> attr.key in SESSION_REINDEX_PROPS } 
                                                                                || change.toOneRelationshipChanges.any { attr -> attr.key in SESSION_REINDEX_TO_ONE_RELATION })}
                .collect { it.postCommitId.idSnapshot[ID_PK_COLUMN]} as List<Long>
        
        if (sessionIds.size() > 0) {
            return ObjectSelect.query(Course)
                    .where(ExpressionFactory.inDbExp(Course.COURSE_CLASSES.dot(CourseClass.SESSIONS).dot(ID_PK_COLUMN).name, sessionIds))
                    .select(objectContext).collect { it.id }.toSet()
        }
        
        return []
    }
}
