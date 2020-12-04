package ish.oncourse.server.api.traits

import ish.common.types.PayslipPayType
import ish.oncourse.server.api.v1.model.PayslipPayTypeDTO

trait PayslipPayTypeDTOTrait {

    PayslipPayType getDbType() {
        switch (this as PayslipPayTypeDTO) {
            case PayslipPayTypeDTO.EMPLOYEE:
                return PayslipPayType.EMPLOYEE
            case PayslipPayTypeDTO.CONTRACTOR:
                return PayslipPayType.CONTRACTOR
            default:
                throw new IllegalArgumentException("Wrong payslip payType: ${toString()}")
        }
    }

    PayslipPayTypeDTO fromDbType(PayslipPayType payslipPayType) {
        if (!payslipPayType) {
            return null
        }
        switch (payslipPayType) {
            case PayslipPayType.EMPLOYEE:
                return PayslipPayTypeDTO.EMPLOYEE
            case PayslipPayType.CONTRACTOR:
                return PayslipPayTypeDTO.CONTRACTOR
            default:
                throw new IllegalArgumentException("Wrong payslip payType: $payslipPayType.displayName")
        }
    }
}