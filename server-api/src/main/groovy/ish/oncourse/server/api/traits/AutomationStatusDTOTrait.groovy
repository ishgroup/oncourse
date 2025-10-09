package ish.oncourse.server.api.traits

import ish.common.types.AutomationStatus
import ish.oncourse.server.api.v1.model.AutomationStatusDTO

trait AutomationStatusDTOTrait {
    AutomationStatus getDbType() {
        switch (this as AutomationStatusDTO) {
            case AutomationStatusDTO.NOT_INSTALLED:
                return AutomationStatus.NOT_INSTALLED
            case AutomationStatusDTO.INSTALLED_BUT_DISABLED:
                return AutomationStatus.INSTALLED_DISABLED
            case AutomationStatusDTO.ENABLED:
                return AutomationStatus.ENABLED
            case AutomationStatusDTO.HIDDEN:
                return AutomationStatus.HIDDEN
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    AutomationStatusDTO fromDbType(AutomationStatus dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case AutomationStatus.NOT_INSTALLED:
                return AutomationStatusDTO.NOT_INSTALLED
            case AutomationStatus.INSTALLED_DISABLED:
                return AutomationStatusDTO.INSTALLED_BUT_DISABLED
            case AutomationStatus.ENABLED:
                return AutomationStatusDTO.ENABLED
            case AutomationStatus.HIDDEN:
                return AutomationStatusDTO.HIDDEN
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}