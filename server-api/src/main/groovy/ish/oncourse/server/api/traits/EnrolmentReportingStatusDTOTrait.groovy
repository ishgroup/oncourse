package ish.oncourse.server.api.traits

import ish.common.types.EnrolmentReportingStatus
import ish.oncourse.server.api.v1.model.EnrolmentReportingStatusDTO

trait EnrolmentReportingStatusDTOTrait {
    EnrolmentReportingStatus getDbType() {
        switch (this as EnrolmentReportingStatusDTO) {
            case EnrolmentReportingStatusDTO.ELIGIBLE:
                return EnrolmentReportingStatus.ELIGIBLE
            case EnrolmentReportingStatusDTO.NOT_ELIGIBLE:
                return EnrolmentReportingStatus.NOT_ELIGIBLE
            case EnrolmentReportingStatusDTO.ONGOING:
                return EnrolmentReportingStatus.ONGOING
            case EnrolmentReportingStatusDTO.FINALIZED:
                return EnrolmentReportingStatus.FINALIZED
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    EnrolmentReportingStatusDTO fromDbType(EnrolmentReportingStatus dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case EnrolmentReportingStatus.ELIGIBLE:
                return EnrolmentReportingStatusDTO.ELIGIBLE
            case EnrolmentReportingStatus.NOT_ELIGIBLE:
                return EnrolmentReportingStatusDTO.NOT_ELIGIBLE
            case EnrolmentReportingStatus.ONGOING:
                return EnrolmentReportingStatusDTO.ONGOING
            case EnrolmentReportingStatus.FINALIZED:
                return EnrolmentReportingStatusDTO.FINALIZED
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}