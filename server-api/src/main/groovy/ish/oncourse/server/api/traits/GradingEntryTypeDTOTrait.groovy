package ish.oncourse.server.api.traits

import ish.common.types.GradingEntryType
import ish.oncourse.server.api.v1.model.GradingEntryTypeDTO

trait GradingEntryTypeDTOTrait {

    GradingEntryType getDbType() {
        switch (this as GradingEntryTypeDTO) {
            case GradingEntryTypeDTO.NUMBER:
                return GradingEntryType.NUMBER
            case GradingEntryTypeDTO.CHOICE_LIST:
                return GradingEntryType.CHOICE_LIST
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    GradingEntryTypeDTO fromDbType(GradingEntryType gradingEntryType) {
        switch (gradingEntryType) {
            case GradingEntryType.NUMBER:
                return GradingEntryTypeDTO.NUMBER
            case GradingEntryType.CHOICE_LIST:
                return GradingEntryTypeDTO.CHOICE_LIST
            default:
                throw new IllegalArgumentException("$gradingEntryType.displayName")
        }
    }
}