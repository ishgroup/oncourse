package ish.oncourse.willow

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.common.types.EntityRelationCartAction
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.*
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import static ish.common.types.EntityRelationCartAction.* 

@CompileStatic
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
        getCourses(Course, course.id, contact)
    }
    
    Map<Course,Boolean> getCoursesToAdd(Product product, Contact contact) {
        getCourses(Product, product.id, contact)
    }


    Map<Product,Boolean> getProductsToAdd(Course course) {
        getProducts(Course, course.id)
    }
    
    Map<Product,Boolean> getProductsToAdd(Product course) {
        getProducts(Product, course.id)
    }
    
    List<Product> getSuggestedProducts(Product product)  {
        (getToRelations(Product, product.id, Product, [SUGGESTION]).values().toList() + 
                getFromRelations(Product, product.id, Product, [SUGGESTION]).values().toList()).unique()
    }

    List<Course> getSuggestedCourses(Product product)  {
        (getToRelations(Product, product.id, Course, [SUGGESTION]).values().toList() +
                getFromRelations(Product, product.id, Course, [SUGGESTION]).values().toList()).unique()
    }
    
    List<Product> getSuggestedProducts(Course course)  {
        (getToRelations(Course, course.id, Product, [SUGGESTION]).values().toList() +
                getFromRelations(Course, course.id, Product, [SUGGESTION]).values().toList()).unique()
    }

    List<Course> getSuggestedCourses(Course course)  {
        (getToRelations(Course, course.id, Course, [SUGGESTION]).values().toList() +
                getFromRelations(Course, course.id, Course, [SUGGESTION]).values().toList()).unique()
    }


    private Map<Course,Boolean> getCourses (Class from, Long id, Contact contact) {

        Map<Course,Boolean> result = [:]
        getToRelations(from, id, Course, [ADD_ALLOW_REMOVAL, ADD_NO_REMOVAL]).each { relation, relatedCourse ->

            if (relatedCourse) {
                if (relation.relationType.considerHistory && contact.isEnrolled(relatedCourse)) {
                    //ignore this course since student already has enrolment
                } else {
                    result[relatedCourse] = ADD_ALLOW_REMOVAL == relation.relationType.shoppingCart
                }
            }
        }
        return result
    }

    private Map<Product,Boolean> getProducts(Class from, Long id) {
        Map<Product,Boolean> result = [:]

        getToRelations(from, id, Product, [ADD_ALLOW_REMOVAL, ADD_NO_REMOVAL]).each { relation, relatedProduct ->
            if (relatedProduct) {
                result[relatedProduct] = ADD_ALLOW_REMOVAL == relation.relationType.shoppingCart
            }
        }
        return result
    }

    private <T>  Map<EntityRelation, T>  getToRelations(Class from, Long fromId, Class<T> to , List<EntityRelationCartAction> actions) {
        ObjectContext context = cayenneService.newContext()
        return ObjectSelect.query(EntityRelation)
                .where(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(from.simpleName))
                .and(EntityRelation.FROM_ENTITY_WILLOW_ID.eq(fromId))
                .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(to.simpleName))
                .and(EntityRelation.RELATION_TYPE.dot(EntityRelationType.SHOPPING_CART).in(actions))
                .select(cayenneService.newContext())
                .collectEntries {relation -> [(relation): SelectById.query(to, relation.toEntityWillowId).selectOne(context) ]}
                .findAll {k,v -> v != null}
    }

    private <T>  Map<EntityRelation, T>  getFromRelations(Class to, Long toId, Class<T> from , List<EntityRelationCartAction> actions) {
        ObjectContext context = cayenneService.newContext()
        return ObjectSelect.query(EntityRelation)
                .where(EntityRelation.TO_ENTITY_IDENTIFIER.eq(to.simpleName))
                .and(EntityRelation.TO_ENTITY_WILLOW_ID.eq(toId))
                .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(from.simpleName))
                .and(EntityRelation.RELATION_TYPE.dot(EntityRelationType.SHOPPING_CART).in(actions))
                .select(cayenneService.newContext())
                .collectEntries {relation -> [(relation): SelectById.query(from, relation.fromEntityWillowId).selectOne(context) ]}
                .findAll {k,v -> v != null}
    }
    
}
