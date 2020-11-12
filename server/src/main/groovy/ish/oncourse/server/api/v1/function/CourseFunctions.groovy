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

import ish.common.types.CourseEnrolmentType
import static ish.common.types.CourseEnrolmentType.ENROLMENT_BY_APPLICATION
import static ish.common.types.CourseEnrolmentType.OPEN_FOR_ENROLMENT
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.CourseEnrolmentTypeDTO
import ish.oncourse.server.api.v1.model.SaleDTO
import ish.oncourse.server.api.v1.model.SaleTypeDTO
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.VoucherProduct

class CourseFunctions {

    public static final BidiMap<CourseEnrolmentType, CourseEnrolmentTypeDTO> ENROLMENT_TYPE_MAP = new BidiMap<CourseEnrolmentType, CourseEnrolmentTypeDTO>() {{
        put(OPEN_FOR_ENROLMENT, CourseEnrolmentTypeDTO.OPEN_ENROLMENT)
        put(ENROLMENT_BY_APPLICATION, CourseEnrolmentTypeDTO.ENROLMENT_BY_APPLICATION)
    }}

    static SaleDTO toRestSalable(Course course) {
        new SaleDTO().with { s ->
            s.id = course.id
            s.active =  course.currentlyOffered || course.isShownOnWeb
            s.code = course.code
            s.name = course.with { "$it.name $it.code" }
            s.type = SaleTypeDTO.COURSE
            s
        }
    }

    static SaleDTO toRestSalable(Product product) {
        new SaleDTO().with { s ->
            s.id = product.id
            s.name = product.name
            s.code = product.sku
            switch (product) {
                case VoucherProduct:
                    s.type = SaleTypeDTO.VOUCHER
                    break
                case MembershipProduct:
                    s.type = SaleTypeDTO.MEMBERSHIP
                    break
                case ArticleProduct:
                    s.type = SaleTypeDTO.PRODUCT
                    break
                default:
                    throw new IllegalArgumentException("${product.class}")
                }
            s
        }
    }
}
