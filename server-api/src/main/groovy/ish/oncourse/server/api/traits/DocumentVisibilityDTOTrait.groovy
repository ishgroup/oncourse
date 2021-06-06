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

import ish.common.types.AttachmentInfoVisibility
import ish.common.types.DataType
import ish.oncourse.server.api.v1.model.DataTypeDTO
import ish.oncourse.server.api.v1.model.DocumentVisibilityDTO

trait DocumentVisibilityDTOTrait {


    AttachmentInfoVisibility getDbType() {
        switch (this as DocumentVisibilityDTO) {
            case DocumentVisibilityDTO.PRIVATE:
                return AttachmentInfoVisibility.PRIVATE
            case DocumentVisibilityDTO.PUBLIC:
                return AttachmentInfoVisibility.PUBLIC
            case DocumentVisibilityDTO.TUTORS_AND_ENROLLED_STUDENTS:
                return AttachmentInfoVisibility.STUDENTS
            case DocumentVisibilityDTO.TUTORS_ONLY:
                return AttachmentInfoVisibility.TUTORS
            case DocumentVisibilityDTO.LINK:
                return AttachmentInfoVisibility.LINK
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    DocumentVisibilityDTO fromDbType(AttachmentInfoVisibility dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case AttachmentInfoVisibility.PRIVATE:
                return DocumentVisibilityDTO.PRIVATE
            case AttachmentInfoVisibility.TUTORS:
                return DocumentVisibilityDTO.TUTORS_ONLY
            case AttachmentInfoVisibility.STUDENTS:
                return DocumentVisibilityDTO.TUTORS_AND_ENROLLED_STUDENTS
            case AttachmentInfoVisibility.LINK:
                return DocumentVisibilityDTO.LINK
            case AttachmentInfoVisibility.PUBLIC:
                return DocumentVisibilityDTO.PUBLIC
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }


}
