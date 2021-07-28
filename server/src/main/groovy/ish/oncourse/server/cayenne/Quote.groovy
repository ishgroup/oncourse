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

import ish.common.types.InvoiceType
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Quote

/**
 * Pre-invoice state
 */
@API
//@QueueableEntity
class Quote extends _Quote {

	@Override
	InvoiceType getType() {
		return InvoiceType.QUOTE
	}

	Class<QuoteLine> getLinePersistentClass() {
		return QuoteLine.class
	}

	List<QuoteLine> getLines() {
		return this.getQuoteLines()
	}
}



