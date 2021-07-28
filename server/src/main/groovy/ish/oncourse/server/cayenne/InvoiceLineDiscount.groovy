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
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._InvoiceLineDiscount

import javax.annotation.Nonnull
import java.util.Date

/**
 * Object representing relation between InvoiceLine and Discount.
 */
@API
@QueueableEntity
class InvoiceLineDiscount extends _InvoiceLineDiscount implements Queueable {




    /**
     * Check if async replication is allowed on this object.
     *
     * @return isAsyncReplicationAllowed
     */
    @Override
    boolean isAsyncReplicationAllowed() {
        return getInvoiceLine() != null && getInvoiceLine().isAsyncReplicationAllowed()
    }

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
	 * @return linked discount
	 */
	@Nonnull
	@API
	@Override
    Discount getDiscount() {
		return super.getDiscount()
    }

	/**
	 * @return linked invoice line
	 */
	@Nonnull
	@API
	@Override
    InvoiceLine getInvoiceLine() {
		return super.getInvoiceLine()
    }

	void setInvoiceLine(AbstractInvoiceLine abstractInvoiceLine) {
		if (abstractInvoiceLine instanceof InvoiceLine) {
			super.setInvoiceLine(abstractInvoiceLine)
		} else if (abstractInvoiceLine instanceof QuoteLine) {
			super.setQuoteLine(abstractInvoiceLine)
		}
	}
}
