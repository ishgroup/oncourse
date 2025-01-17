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


import ish.oncourse.server.api.v1.model.DiscountDTO
import ish.oncourse.server.api.v1.model.SaleDTO
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById

class CorporatePassFunctions {

    static void updateCorporatePassDiscounts(ObjectContext context, CorporatePass dbCorporatePass, List<DiscountDTO> discounts) {
        List<Long> relationsToSave = discounts*.id ?: [] as List<Long>
        context.deleteObjects(dbCorporatePass.corporatePassDiscounts.findAll { !relationsToSave.contains(it.discount.id) })
        discounts.findAll { !dbCorporatePass.corporatePassDiscounts*.discount*.id.contains(it.id) }.each { DiscountDTO d ->
            context.newObject(CorporatePassDiscount).with { cpd ->
                cpd.corporatePass = dbCorporatePass
                cpd.discount = getRecordById(context, Discount, d.id)
                cpd
            }
        }
    }

    static void updateCorporatePassCourseClasses(ObjectContext context, CorporatePass dbCorporatePass, List<SaleDTO> courseClasses) {
        List<Long> relationsToSave = courseClasses*.id ?: [] as List<Long>
        context.deleteObjects(dbCorporatePass.corporatePassCourseClasses.findAll { !relationsToSave.contains(it.courseClass.id) })
        courseClasses.findAll { !dbCorporatePass.corporatePassCourseClasses*.courseClass*.id.contains(it.id) }.each { SaleDTO  cc ->
            context.newObject(CorporatePassCourseClass).with { dcc ->
                dcc.corporatePass = dbCorporatePass
                dcc.courseClass = getRecordById(context, CourseClass, cc.id)
            }
        }
    }

    static void updateCorporateProducts(ObjectContext context, CorporatePass dbCorporatePass, List<SaleDTO> products) {
        List<Long> relationsToSave = products*.id ?: [] as List<Long>
        context.deleteObjects(dbCorporatePass.corporatePassProduct.findAll { !relationsToSave.contains(it.product.id) })
        products.findAll { product -> !dbCorporatePass.corporatePassProduct*.product*.id.contains(product.id) }.each { product ->
            context.newObject(CorporatePassProduct).with { cpd ->
                cpd.corporatePass = dbCorporatePass
                cpd.product = getRecordById(context, Product, product.id)
                cpd
            }
        }
    }
}
