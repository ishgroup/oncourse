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
package ish.oncourse.cayenne

import ish.oncourse.API
import ish.oncourse.server.cayenne.Tag
/**
 * Entities which implement this interface may have TagRequirement, for example Student, Tutor but real TagRelation always created for Contact entity
 */
@API
interface Taggable {

	String TAGGING_RELATIONS_PROPERTY = "taggingRelations"
	String TAG_IDS = "tagIds"
	String TAG_COLORS = "tagColors"
	String CHECKLIST_COLORS = "checklistsColor"

	Long getId()
	List<? extends Tag> getTags()
	List<Long> getTagIds()
	List<String> getTagColors()
	List<? extends Tag> getChecklists()
	String getChecklistsColor()

}
