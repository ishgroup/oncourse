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

package ish.oncourse.server.api.traits

import ish.oncourse.server.api.v1.model.SaleTypeDTO

trait SaleTypeDTOTrait {
    String getCayenneClassName() {
        switch (this as SaleTypeDTO) {
            case SaleTypeDTO.PRODUCT:
            case SaleTypeDTO.MEMBERSHIP:
            case SaleTypeDTO.VOUCHER:
                return "Product"
            case SaleTypeDTO.CLASS:
                return "CourseClass"
            default:
                return (this as SaleTypeDTO).toString()
        }
    }

    SaleTypeDTO getFromCayenneClassName(String entityName) {
        switch (entityName) {
            case "ArticleProduct":
                return SaleTypeDTO.PRODUCT
            case "MembershipProduct":
                return SaleTypeDTO.MEMBERSHIP
            case "VoucherProduct":
                return SaleTypeDTO.VOUCHER
            case "CourseClass":
                return SaleTypeDTO.CLASS
            default:
                return SaleTypeDTO.fromValue(entityName)
        }
    }
}
