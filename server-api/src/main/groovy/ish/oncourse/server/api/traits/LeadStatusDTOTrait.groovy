package ish.oncourse.server.api.traits

import ish.common.types.LeadStatus
import ish.oncourse.server.api.v1.model.LeadStatusDTO

trait LeadStatusDTOTrait {

    LeadStatus getDbType() {
        switch (this as LeadStatusDTO) {
            case LeadStatusDTO.OPEN:
                return LeadStatus.OPEN
            case LeadStatusDTO.CLOSED:
                return LeadStatus.CLOSED
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    LeadStatusDTO fromDbType(LeadStatus leadStatus) {
        if(!leadStatus) {
            return null
        }
        switch (leadStatus) {
            case LeadStatus.OPEN:
                return LeadStatusDTO.OPEN
            case LeadStatus.CLOSED:
                return LeadStatusDTO.CLOSED
            default:
                throw new IllegalArgumentException("$leadStatus.displayName")
        }
    }

}