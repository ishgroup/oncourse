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

import ish.oncourse.server.cayenne.glue._CourseClassAttachmentRelation

import javax.annotation.Nonnull

/**
 * A persistent class mapped as "CourseClassAttachmentRelation" Cayenne entity.
 */
class CourseClassAttachmentRelation extends _CourseClassAttachmentRelation {



    /**
	 * @see AttachmentRelation#getAttachedRelation()
	 */
	@Nonnull
	@Override
	AttachableTrait getAttachedRelation() {
		return super.getAttachedCourseClass()
    }

	/**
	 * @see AttachmentRelation#setAttachedRelation(AttachableTrait)
	 */
	@Override
    void setAttachedRelation(AttachableTrait attachable) {
		super.setAttachedCourseClass((CourseClass) attachable)
    }

	/**
	 * @see AttachmentRelation#getEntityIdentifier()
	 */
	@Nonnull
	@Override
    String getEntityIdentifier() {
		return CourseClass.class.getSimpleName()
    }
}
