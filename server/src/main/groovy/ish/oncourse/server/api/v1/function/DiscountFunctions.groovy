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

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.common.types.ClassCostFlowType
import ish.common.types.ClassCostRepetitionType
import ish.common.types.DiscountType
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.cayenne.Discount as DbDiscount
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.DiscountMembershipFunctions.updateContactRelationTypes
import static ish.oncourse.server.api.v1.model.DiscountTypeDTO.*

@CompileStatic
class DiscountFunctions {

    private static final BidiMap<DiscountType, DiscountTypeDTO> discountTypeMap = new BidiMap<DiscountType, DiscountTypeDTO>() {{
        put(DiscountType.PERCENT, PERCENT)
        put(DiscountType.DOLLAR, DOLLAR)
        put(DiscountType.FEE_OVERRIDE, FEE_OVERRIDE)
    }}

    static DiscountCorporatePassDTO toRestDiscountCorporatePass(CorporatePass corporatePass) {
        new DiscountCorporatePassDTO().with { dcp ->
            dcp.id = corporatePass.id
            dcp.contactFullName = corporatePass.contact.fullName
            dcp
        }
    }

    static DiscountDTO toRestDiscountMinimized(DbDiscount dbDiscount) {
        new DiscountDTO().with { d ->
            d.id = dbDiscount.id
            d.name = dbDiscount.name
            d.discountType = discountTypeMap.get(dbDiscount.discountType)
            d.discountValue = dbDiscount.discountDollar?.toBigDecimal()
            d.discountPercent = dbDiscount.discountPercent
            d
        }
    }

    static void updateDiscountConcessionTypes(ObjectContext context, DbDiscount dbDiscount, List<ConcessionTypeDTO> concessionTypes) {
        List<Long> relationsToSave = concessionTypes*.id.collect { Long.valueOf(it as String) } as List<Long>
        dbDiscount.discountConcessionTypes.findAll {
            !relationsToSave.contains(it.concessionType.id)
        }.forEach{ it ->
            dbDiscount.removeFromDiscountConcessionTypes(it as DiscountConcessionType)
            context.deleteObject(it)
        }
        concessionTypes.findAll { !dbDiscount.discountConcessionTypes*.concessionType*.id.contains(Long.valueOf(it.id)) }.each {
            context.newObject(DiscountConcessionType).with { dct ->
                dct.discount = dbDiscount
                dct.concessionType = getRecordById(context, ConcessionType, Long.valueOf((it as ConcessionTypeDTO).id))
                dct
            }
        }
    }

    static void updateDiscountMemberships(ObjectContext context, DbDiscount dbDiscount, List<DiscountMembershipDTO> restMemberships) {
        List<Long> relationsToSave = restMemberships*.productId.findAll() as List<Long>
        dbDiscount.discountMemberships.findAll {
            !relationsToSave.contains(it.membershipProduct.id)
        }.forEach{ it ->
            dbDiscount.removeFromDiscountMemberships(it as DiscountMembership)
            context.deleteObject(it)
        }
        List<Long> alreadyExist = dbDiscount.discountMemberships.findAll { relationsToSave.contains(it.membershipProduct.id) }*.membershipProduct*.id.findAll() as List<Long>
        restMemberships.each { rdm ->
            DiscountMembership dbDiscountMembership
            if (alreadyExist.contains(rdm.productId)) {
                dbDiscountMembership = dbDiscount.discountMemberships.find { it.membershipProduct.id == rdm.productId }
            } else {
                dbDiscountMembership = context.newObject(DiscountMembership).with { dbDM ->
                    dbDM.discount = dbDiscount
                    dbDM.membershipProduct = getRecordById(context, MembershipProduct, rdm.productId)
                    dbDM
                }
            }
            dbDiscountMembership.applyToMemberOnly = rdm.contactRelations.isEmpty()
            updateContactRelationTypes(context, dbDiscountMembership, rdm.contactRelations)
        }
    }

    static void updateDiscountCourseClasses(ObjectContext context, DbDiscount dbDiscount, List<SaleDTO> courseClasses) {
        List<Long> relationsToSave = courseClasses*.id as List<Long>
        context.deleteObjects(dbDiscount.discountCourseClasses.findAll { !relationsToSave.contains(it.courseClass.id) })
        courseClasses.findAll { !dbDiscount.discountCourseClasses*.courseClass*.id.contains(it.id) }.each {
            context.newObject(DiscountCourseClass).with { dcc ->
                dcc.discount = dbDiscount
                dcc.courseClass = getRecordById(context, CourseClass, (it as SaleDTO).id)

                context.newObject(ClassCost).with { cc ->
                    cc.courseClass = dcc.courseClass
                    cc.discountCourseClass = dcc
                    cc.flowType = ClassCostFlowType.DISCOUNT
                    cc.repetitionType = ClassCostRepetitionType.DISCOUNT
                    cc.description = dbDiscount.name
                }
            }
        }
    }

    static void updateCorporatePassDiscount(ObjectContext context, DbDiscount dbDiscount, List<DiscountCorporatePassDTO> corporatePasses) {
        List<Long> relationsToSave = corporatePasses*.id as List<Long>
        context.deleteObjects(dbDiscount.corporatePassDiscount.findAll { !relationsToSave.contains(it.corporatePass.id) })
        corporatePasses.findAll { !dbDiscount.corporatePassDiscount*.corporatePass*.id.contains(it.id) }.each {
            context.newObject(CorporatePassDiscount).with { cpd ->
                cpd.discount = dbDiscount
                cpd.corporatePass = getRecordById(context, CorporatePass, (it as DiscountCorporatePassDTO).id)
                cpd
            }
        }
    }
}
