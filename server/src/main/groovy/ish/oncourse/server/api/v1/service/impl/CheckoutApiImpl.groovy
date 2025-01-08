/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import groovy.transform.CompileStatic
import ish.common.types.EntityRelationCartAction
import ish.math.Money
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.CourseDao
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.api.dao.ProductDao
import ish.oncourse.server.api.service.ContactApiService
import ish.oncourse.server.api.service.CourseClassApiService
import ish.oncourse.server.api.service.DiscountApiService
import ish.oncourse.server.api.service.MembershipProductApiService
import ish.oncourse.server.api.v1.function.CartFunctions
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.api.v1.service.CheckoutApi
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.checkout.CheckoutApiService
import ish.util.DiscountUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils

@CompileStatic
class CheckoutApiImpl implements CheckoutApi {

    @Inject
    ContactDao contactDao

    @Inject
    CourseDao courseDao

    @Inject
    ProductDao productDao

    @Inject
    CourseClassApiService courseClassService

    @Inject
    CayenneService cayenneService

    @Inject
    ContactApiService contactApiService

    @Inject
    MembershipProductApiService membershipApiService

    @Inject
    CourseClassApiService courseClassApiService

    @Inject
    CheckoutApiService checkoutApiService

    @Inject
    DiscountApiService discountApiService

    @Override
    CartIdsDTO getCartDataIds(Long checkoutId) {
        def checkout = SelectById.query(Checkout,checkoutId).selectOne(cayenneService.newReadonlyContext)
        return CartFunctions.toRestCartIds(checkout)
    }

    @Override
    List<CourseClassDiscountDTO> getContactDiscounts(Long contactId, Long classId,
                                                     String courseIds, String productIds, String classIds,
                                                     String promoIds, String membershipIds, BigDecimal purchaseTotal, Long payerId) {
        ObjectContext context = cayenneService.newContext
        CourseClass courseClass = courseClassService.getEntityAndValidateExistence(context, classId)
        Contact contact = contactApiService.getEntityAndValidateExistence(context, contactId)
        Contact payerContact = payerId == null ? null : contactApiService.getEntityAndValidateExistence(context, payerId) as Contact
        if (contact.isCompany) {
            return []
        }
        List<Long> courses = courseIds == null || courseIds.empty ? [] : courseIds.split(',').collect { Long.valueOf(it) }
        List<Long> products = productIds == null || productIds.empty ? [] : productIds.split(',').collect { Long.valueOf(it) }
        List<Long> promos = promoIds == null || promoIds.empty ? [] : promoIds.split(',').collect { Long.valueOf(it)}
        List<CourseClass> enrolledClasses = classIds == null || classIds.empty ? [] : classIds.split(',')
                .collect { courseClassApiService.getEntityAndValidateExistence(context, Long.valueOf(it)) }
        List<MembershipProduct> memberships = membershipIds == null || membershipIds.empty ? [] :
                membershipIds.split(',').collect { membershipApiService.getEntityAndValidateExistence(context,Long.valueOf(it)) }
        Money total = new Money(purchaseTotal)

        List<DiscountCourseClass> discountCourseClasses = courseClass.getAvalibleDiscounts(contact, payerContact, courses, products, promos, enrolledClasses, memberships, total)

        if (discountCourseClasses.empty) {
            return []
        } else {
            DiscountCourseClass best = DiscountUtils.chooseDiscountForApply(discountCourseClasses, courseClass.feeExGst, courseClass.tax.rate) as DiscountCourseClass
            if (best) {
                discountCourseClasses.add(0, best)
            }
            return discountCourseClasses.unique().collect {
                CourseClassDiscountDTO dto = new CourseClassDiscountDTO()
                dto.forecast = it.predictedStudentsPercentage
                dto.discountOverride = it.discountDollar?.toBigDecimal()
                dto.setDiscount(discountApiService.toRestDiscountMinimized(it.discount))
                dto
            }
        }
    }

    List<CheckoutSaleRelationDTO> getSaleRelations(Long id, String entityName, Contact contact) {
        ObjectContext context = cayenneService.newContext
        List<EntityRelation> relations = EntityRelationDao.getRelatedToOrEqual(context, entityName, id)
                .findAll { EntityRelationCartAction.NO_ACTION != it.relationType.shoppingCart }
        List<CheckoutSaleRelationDTO> result = []

        relations.findAll { Course.simpleName == it.toEntityIdentifier && id != it.toEntityAngelId }.each { relation ->
            EntityRelationType relationType = relation.relationType
            Course course = courseDao.getById(context, relation.toEntityAngelId)

            if (contact && relationType.considerHistory  && contact.student.isEnrolled(course)) {
                //ignore that course since student already enrolled in
            } else {
                result << createCourseCheckoutSaleRelation(id, entityName, course, relationType)
            }
        }

        relations.findAll { Course.simpleName == it.fromEntityIdentifier && id != it.fromEntityAngelId }.each { relation ->
            EntityRelationType relationType = relation.relationType
            Course course = courseDao.getById(context, relation.fromEntityAngelId)

            if (contact && relationType.considerHistory  && contact.student.isEnrolled(course)) {
                //ignore that course since student already enrolled in
            } else {
                result << createCourseCheckoutSaleRelation(id, entityName, course, relationType)
            }
        }

        relations.findAll { Product.simpleName == it.toEntityIdentifier && id != it.toEntityAngelId }.each { relation ->
            EntityRelationType relationType = relation.relationType
            Product product = productDao.getById(context, relation.toEntityAngelId)
            result << createProductCheckoutSaleRelation(id, entityName, product, relationType)
        }

        relations.findAll { Product.simpleName == it.fromEntityIdentifier && id != it.fromEntityAngelId }.each { relation ->
            EntityRelationType relationType = relation.relationType
            Product product = productDao.getById(context, relation.fromEntityAngelId)
            result << createProductCheckoutSaleRelation(id, entityName, product, relationType)
        }

        result
    }

    private static CheckoutSaleRelationDTO createCourseCheckoutSaleRelation(Long id, String entityName, Course course, EntityRelationType relationType) {
        new CheckoutSaleRelationDTO().with {saleRelation ->
            saleRelation.fromItem = new SaleDTO(id: id, type: SaleTypeDTO.values()[0].getFromCayenneClassName(entityName))
            saleRelation.toItem = new SaleDTO(id: course.id, type: SaleTypeDTO.COURSE)
            saleRelation.cartAction = EntityRelationCartActionDTO.values()[0].fromDbType(relationType.shoppingCart)
            if (relationType.discount) {
                saleRelation.discount = discountApiService.toNotFullRestModel(relationType.discount)
            }
            saleRelation
        }
    }

    private static CheckoutSaleRelationDTO createProductCheckoutSaleRelation(Long id, String entityName, Product product, EntityRelationType relationType) {
        new CheckoutSaleRelationDTO().with { saleRelation ->
            saleRelation.fromItem = new SaleDTO(id: id, type: SaleTypeDTO.values()[0].getFromCayenneClassName(entityName))
            saleRelation.toItem = new SaleDTO(id: product.id, type: SaleTypeDTO.values()[0].getFromCayenneClassName(product.getClass().getSimpleName()))
            saleRelation.cartAction = EntityRelationCartActionDTO.values()[0].fromDbType(relationType.shoppingCart)
            saleRelation
        }
    }

    @Override
    List<CheckoutSaleRelationDTO> getSaleRelations(String courseIds, String productIds, Long contactId) {

        ObjectContext context = cayenneService.newContext
        List<CheckoutSaleRelationDTO> result = []
        Contact contact = contactId ? contactDao.getById(context, contactId) : null

        if (StringUtils.trimToNull(courseIds)) {
            (courseIds.split(',').collect {Long.valueOf(it)} as List<Long>).each { courseId ->
                result.addAll(getSaleRelations(courseId, Course.simpleName, contact))
            }
        } else if (StringUtils.trimToNull(productIds)) {
            (productIds.split(',').collect {Long.valueOf(it)} as List<Long>).each { productId ->
                result.addAll(getSaleRelations(productId, Product.simpleName, contact))
            }
        }
        return result
    }

    @Override
    SessionStatusDTO status(String sessionId) {
       return checkoutApiService.status(sessionId)
    }

    @Override
    CheckoutResponseDTO submit(CheckoutModelDTO checkoutModel, Boolean xValidateOnly, String xPaymentSessionId, String xOrigin ) {
        return checkoutApiService.submit(checkoutModel, xValidateOnly, xPaymentSessionId, xOrigin)
    }

}
