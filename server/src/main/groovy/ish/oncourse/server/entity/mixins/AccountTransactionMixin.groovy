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

package ish.oncourse.server.entity.mixins

import groovy.transform.CompileStatic
import ish.common.types.AccountTransactionType
import ish.oncourse.API
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentOutLine
import static ish.oncourse.server.entity.mixins.MixinHelper.getService
import ish.oncourse.server.services.TransactionLockedService
import org.apache.cayenne.query.SelectById

import javax.annotation.Nullable
import javax.annotation.Nonnull

@CompileStatic
class AccountTransactionMixin {

	/**
	 * @return the related invoiceLine to this AccountTransaction, if it exists
	 */
	@API @Nullable
	static InvoiceLine getRelatedInvoiceLine(AccountTransaction self) {
		return AccountTransactionType.INVOICE_LINE.equals(self.getTableName()) ?
				SelectById.query(InvoiceLine, self.foreignRecordId).selectOne(self.objectContext) : null
	}

	/**
	 * Returns the name of the contact related to this transaction. If transaction is:
	 * # invoice line: return invoice.contact.name
	 * # payment in: return paymentIn.contact.name
	 * # payment out: return paymentOut.payee.name
	 *
	 * @return name of contact related to this AccountTransaction
	 */
	@API @Nullable
	static String getPrintTitle(AccountTransaction self) {
		if (AccountTransactionType.INVOICE_LINE.equals(self.tableName)) {
			def il = SelectById.query(InvoiceLine, self.foreignRecordId).selectOne(self.objectContext)
			if (il?.invoice?.contact != null) {
				return il.invoice.contact.name
			}
		} else if (AccountTransactionType.PAYMENT_IN_LINE.equals(self.tableName)) {
			def pil = SelectById.query(PaymentInLine, self.foreignRecordId).selectOne(self.objectContext)
			if (pil?.paymentIn?.contact != null) {
				return pil.paymentIn.contact.name
			}
		} else if (AccountTransactionType.PAYMENT_OUT_LINE.equals(self.tableName)) {
			def pol = SelectById.query(PaymentOutLine, self.foreignRecordId).selectOne(self.objectContext)
			if (pol?.paymentOut?.payee != null) {
				return pol.paymentOut.payee.name
			}
		}
		return ""
	}

	/**
	 * Return true if transaction day cannot be changed.
	 *
	 * @param self
	 * @return true if transaction day is locked
	 */
	@API @Nonnull
	static boolean getIsLocked(AccountTransaction self) {
		def transactionLocked = getService(TransactionLockedService).getTransactionLocked()
		return !transactionLocked.isBefore(self.transactionDate)
	}
}
