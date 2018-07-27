package ish.oncourse.solr.update.listener

import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Room
import ish.oncourse.model.Session
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.commitlog.model.ObjectChange
import org.apache.cayenne.commitlog.model.ObjectChangeType
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.model.auto._Site.*

class GetCoursesFromSite {

    Set<ObjectChange> objectChanges
    ObjectContext objectContext


    static GetCoursesFromSite valueOf(Set<ObjectChange> objectChanges, ObjectContext objectContext){
        GetCoursesFromSite condition = new GetCoursesFromSite()
        condition.objectChanges = objectChanges
        condition.objectContext = objectContext
        condition
    }

    private static final List<String> SITE_REINDEX_PROPS =
            [LONGITUDE_PROPERTY,
             LATITUDE_PROPERTY,
             SUBURB_PROPERTY,
             POSTCODE_PROPERTY,
            IS_WEB_VISIBLE_PROPERTY]



    Set<Long> get(){
        List<Long> siteIds = objectChanges.findAll { change -> change.type == ObjectChangeType.UPDATE  && change.attributeChanges.any { attr -> attr.key in SITE_REINDEX_PROPS }}
        .collect { it.postCommitId.idSnapshot[ID_PK_COLUMN]} as List<Long>

        if (siteIds.size() > 0) {
            return ObjectSelect.query(Course).where(Course.IS_WEB_VISIBLE.isTrue())
                    .and(ExpressionFactory.inDbExp(Course.COURSE_CLASSES.dot(CourseClass.ROOM).dot(Room.SITE).dot(ID_PK_COLUMN).name, siteIds)
                        .orExp(ExpressionFactory.inDbExp(Course.COURSE_CLASSES.dot(CourseClass.SESSIONS).dot(Session.ROOM).dot(Room.SITE).dot(ID_PK_COLUMN).name, siteIds)))
                    .select(objectContext).collect { it.id }.toSet()
        }

        return []
    }
}
