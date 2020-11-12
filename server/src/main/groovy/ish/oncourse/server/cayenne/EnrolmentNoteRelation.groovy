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

package ish.oncourse.server.cayenne

import ish.oncourse.server.cayenne.glue._EnrolmentNoteRelation

import javax.annotation.Nonnull

class EnrolmentNoteRelation extends _EnrolmentNoteRelation {



    @Nonnull
	@Override
    String getEntityIdentifier() {
		return Enrolment.class.getSimpleName()
    }

	@Override
    void setNotableEntity(NotableTrait entity) {
        super.setNotedEnrolment((Enrolment) entity)
    }

	@Nonnull
	@Override
    NotableTrait getNotableEntity() {
		return super.getNotedEnrolment()
    }
}



