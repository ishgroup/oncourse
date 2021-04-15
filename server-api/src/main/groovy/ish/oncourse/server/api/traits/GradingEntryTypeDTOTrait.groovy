package ish.oncourse.server.api.traits

import ish.common.types.GradingEntryType
import ish.oncourse.server.api.v1.model.GradingEntryTypeDTO

trait GradingEntryTypeDTOTrait {

    GradingEntryType getDbType() {
        switch (this as GradingEntryTypeDTO) {
            case GradingEntryTypeDTO.NUMBER:
                return GradingEntryType.NUMBER
            case GradingEntryTypeDTO.NAME:
                return GradingEntryType.NAME
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    GradingEntryTypeDTO fromDbType(GradingEntryType gradingEntryType) {
        switch (gradingEntryType) {
            case GradingEntryType.NUMBER:
                return GradingEntryTypeDTO.NUMBER
            case GradingEntryType.NAME:
                return GradingEntryTypeDTO.NAME
            default:
                throw new IllegalArgumentException("$gradingEntryType.displayName")
        }
    }
}