package ish.oncourse.solr.update.listener

import ish.oncourse.model.Course
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.commitlog.model.ObjectChange
import org.apache.cayenne.commitlog.model.ObjectChangeType
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.model.auto._CourseClass.*

class GetCoursesFromClasses {
    Set<ObjectChange> objectChanges
    ObjectContext objectContext


    static GetCoursesFromClasses valueOf(Set<ObjectChange> objectChanges, ObjectContext objectContext){
        GetCoursesFromClasses condition = new GetCoursesFromClasses()
        condition.objectChanges = objectChanges
        condition.objectContext = objectContext
        condition
    }
    
    private static final List<String> CLASS_REINDEX_PROPS = 
            [IS_ACTIVE_PROPERTY,
                CANCELLED_PROPERTY,
                IS_WEB_VISIBLE_PROPERTY,
                CODE_PROPERTY,
                END_DATE_PROPERTY,
                FEE_EX_GST_PROPERTY,
                FEE_GST_PROPERTY,
                IS_DISTANT_LEARNING_COURSE_PROPERTY,
                START_DATE_PROPERTY,
                TIME_ZONE_PROPERTY ]
    
    private static final List<String> CLASS_REINDEX_TO_MANY_RELATIONS =
            [ TUTOR_ROLES_PROPERTY,
              SESSIONS_PROPERTY ]
    private static final List<String> CLASS_REINDEX_TO_ONE_RELATIONS =
            [ ROOM_PROPERTY ]
    
    

    Set<Long> get(){
        List<Long> classIds = objectChanges
                .findAll { change -> change.type == ObjectChangeType.UPDATE && (change.attributeChanges.any { attr -> attr.key in CLASS_REINDEX_PROPS }
                                    || change.toManyRelationshipChanges.any {attr -> attr.key in CLASS_REINDEX_TO_MANY_RELATIONS }
                                    || change.toOneRelationshipChanges.any {attr -> attr.key in CLASS_REINDEX_TO_ONE_RELATIONS }    )
                        }
                .collect { it.postCommitId.idSnapshot[ID_PK_COLUMN]} as List<Long>
                

        if (classIds.size() > 0) {

            return ObjectSelect.query(Course)
                    .where(ExpressionFactory.inDbExp(Course.COURSE_CLASSES.dot(ID_PK_COLUMN).name, classIds))
                    .select(objectContext).collect { it.id }.toSet()

        }
        return []
    }
}
