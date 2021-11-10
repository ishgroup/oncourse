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

import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.SaleDTO
import ish.oncourse.server.api.v1.model.SaleTypeDTO
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Module
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.Qualification
import ish.oncourse.server.entity.mixins.CourseClassMixin

class SaleFunctions {

    public static final BidiMap<Integer, SaleTypeDTO> saleTypeMap = new BidiMap<Integer, SaleTypeDTO>() {{
        put(1, SaleTypeDTO.PRODUCT)
        put(2, SaleTypeDTO.MEMBERSHIP)
        put(3, SaleTypeDTO.VOUCHER)
    }}

    static SaleDTO toRestSale(CourseClass courseClass) {
        new SaleDTO().with { sale ->
            sale.id = courseClass.id
            sale.name = courseClass.course.name
            sale.code = courseClass.uniqueCode
            sale.type = SaleTypeDTO.CLASS
            sale.active = CourseClassMixin.isActual(courseClass)
            sale
        }
    }

    static SaleDTO toRestSale(Product product) {
        new SaleDTO().with { sale ->
            sale.id = product.id
            sale.name = product.name
            sale.code = product.sku
            sale.type = saleTypeMap[product.type]
            sale.active = true
            sale
        }
    }

    static SaleDTO toRestSale(Course course) {
        new SaleDTO().with { s ->
            s.id = course.id
            s.active =  course.currentlyOffered || course.isShownOnWeb
            s.code = course.code
            s.name = course.with { "$it.name $it.code" }
            s.type = SaleTypeDTO.COURSE
            s
        }
    }

    static SaleDTO toRestSale(Module module) {
        new SaleDTO().with { s ->
            s.id = module.id
            s.name = module.title
            s.code = module.nationalCode
            s.active = module.isOffered
            s.type = SaleTypeDTO.MODULE
            s
        }
    }

    static SaleDTO toRestSale(Qualification qualification) {
        new SaleDTO().with { s ->
            s.id = qualification.id
            s.name = qualification.title
            s.code = qualification.nationalCode
            s.active = qualification.isOffered
            s.type = SaleTypeDTO.QUALIFICATION
            s
        }
    }
}
