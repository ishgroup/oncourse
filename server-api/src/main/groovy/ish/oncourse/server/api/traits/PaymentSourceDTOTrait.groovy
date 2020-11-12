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

import ish.common.types.PaymentSource
import ish.oncourse.server.api.v1.model.PaymentSourceDTO

trait PaymentSourceDTOTrait {

    PaymentSource getDbType() {
        switch (this as PaymentSourceDTO) {
            case PaymentSourceDTO.OFFICE:
                return PaymentSource.SOURCE_ONCOURSE
            case PaymentSourceDTO.WEB:
                return PaymentSource.SOURCE_WEB
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    PaymentSourceDTO fromDbType(PaymentSource dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case PaymentSource.SOURCE_ONCOURSE:
                return PaymentSourceDTO.OFFICE
            case PaymentSource.SOURCE_WEB:
                return PaymentSourceDTO.WEB
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
