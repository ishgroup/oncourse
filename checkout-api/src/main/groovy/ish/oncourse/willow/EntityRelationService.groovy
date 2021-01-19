package ish.oncourse.willow

import com.google.inject.Inject
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.*
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import static ish.common.types.EntityRelationCartAction.* 

class EntityRelationService {

    private CayenneService cayenneService
    private CollegeService collegeService

    @Inject
    EntityRelationService(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
    }
    /**
     * 
     * 
     * @param course
     * @param contact
     * @return map key: course, value: allow remove from shopping  cart
     */
    Map<Course,Boolean> getCoursesToAdd(Course course, Contact contact) {
        
        ObjectContext context = cayenneService.newContext()
        Map<Course,Boolean> result = [:]
        
        List<EntityRelation> relations = ObjectSelect.query(EntityRelation)
                .where(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .and(EntityRelation.FROM_ENTITY_WILLOW_ID.eq(course.id))
                .and(EntityRelation.RELATION_TYPE.isNotNull())
                .and(EntityRelation.RELATION_TYPE.dot(EntityRelationType.SHOPPING_CART).in(ADD_ALLOW_REMOVAL, ADD_NO_REMOVAL))
                .select(context)
        
        relations.each { relation ->
            Course relatedCourse = SelectById.query(Course, relation.toEntityWillowId).selectOne(context)
            if (relatedCourse) {
                if (relation.relationType.considerHistory && contact.isEnrolled(course)) {
                    //ignore this course since student already has enrolment
                } else {
                    result[relatedCourse] = ADD_ALLOW_REMOVAL != relation.relationType.shoppingCart
                }
            }
        }
        
        return result
    }
}
