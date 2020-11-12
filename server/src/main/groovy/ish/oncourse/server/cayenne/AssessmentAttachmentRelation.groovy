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

import ish.oncourse.server.cayenne.glue._AssessmentAttachmentRelation

import javax.annotation.Nonnull

class AssessmentAttachmentRelation extends _AssessmentAttachmentRelation {



	@Override
	AttachableTrait getAttachedRelation() {
		return super.getAttachedAssessment()
	}

	@Nonnull
	@Override
	String getEntityIdentifier() {
		return Assessment.class.getSimpleName()
	}

	@Override
	void setAttachedRelation(AttachableTrait attachable) {
		super.setAttachedAssessment((Assessment) attachable)
	}

}



