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

import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._ReportOverlay
import ish.util.ImageHelper

import javax.annotation.Nonnull


/**
 * A report can be printed with an overlay to add logos, headers or other graphics. More correctly this 'overlay'
 * is an 'underlay' since it is added to the output PDF underneath the report itself.
 *
 * A user can print any report with any overlay, and onCourse will default to the last overlay used by each user.
 *
 * Common overlays are 'letterhead' for invoices and a certificate background with logos and signatures.
 */
@API
class ReportOverlay extends _ReportOverlay {

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return the name of the overlay which appears in the user interface
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * Overlays are stored in the database as a PDF.
	 *
	 * @return the binary PDF data
	 */
	@API
	@Override
	byte[] getOverlay() {
		return super.getOverlay()
	}

	@API
	Boolean getPortrait() {
		ImageHelper.isPortrait(this.overlay)
	}
}



