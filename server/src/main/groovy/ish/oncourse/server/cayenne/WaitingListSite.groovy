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

import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._WaitingListSite

import javax.annotation.Nonnull

/**
 * A persistent class mapped as "WaitingListSite" Cayenne entity.
 */
@QueueableEntity
class WaitingListSite extends _WaitingListSite implements Queueable {

	/**
	 * @return the site (if any) that the student nominated they wanted
	 */
	@Nonnull
	@Override
	Site getSite() {
		return super.getSite()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	WaitingList getWaitingList() {
		return super.getWaitingList()
	}
}
