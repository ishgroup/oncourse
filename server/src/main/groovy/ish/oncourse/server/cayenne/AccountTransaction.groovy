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

import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.server.cayenne.glue._AccountTransaction
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate
/**
 * AccountTransactions are entries against the general ledger in onCourse. They are immutable (cannot be edited) and
 * can only be created in pairs.
 *
 */
@API
class AccountTransaction extends _AccountTransaction {



	public static final String TRANSACTION_DESCRIPTION_PROPERTY = "transactionDescription"
	public static final String SOURCE_STRING_PROPERTY = "source"
	public static final String INVOICE_NUMBER_PROPERTY = "invoiceNumber"
	public static final String CONTACT_NAME_PROPERTY = "contactName"
	public static final String PAYMENT_TYPE_PROPERTY = "paymentType"
	public static final String INVOICE_DESCRIPTION_PROPERTY = "invoiceDescription"

	/**
	 * @return The primary key for this record. This value will never change once the record is created.
	 */
	@Nonnull
	@API
	@Override
	Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null
	}

	/**
	 * Some transactions are linked to invoice lines.
	 *
	 * @param context return the resulting objects in this context
	 * @param t account transaction related to this invoice line
	 * @return the invoice line linked to this transaction or null if there is no such link
	 */
	static InvoiceLine getInvoiceLineForTransaction(@Nonnull ObjectContext context, @Nonnull AccountTransaction t) {
		if (t.getTableName() == AccountTransactionType.INVOICE_LINE) {
			return SelectById.query(InvoiceLine.class, t.getForeignRecordId()).selectOne(context)
		}
		return null
	}

	/**
	 * Some transactions are linked to paymentIn lines
	 *
	 * @param context return the resulting objects in this context
	 * @param t account transaction related to this paymentIn line
	 * @return the paymentIn line linked to this transaction or null if there is no such link
	 */
	static PaymentInLine getPaymentInLineForTransaction(@Nonnull ObjectContext context, @Nonnull AccountTransaction t) {
		if (t.getTableName() == AccountTransactionType.PAYMENT_IN_LINE) {
			return SelectById.query(PaymentInLine.class, t.getForeignRecordId()).selectOne(context)
		}
		return null
	}

	/**
	 * Some transactions are linked to paymentOut lines
	 *
	 * @param context return the resulting objects in this context
	 * @param t account transaction related to this paymentOut line
	 * @return the paymentOut line linked to this transaction or null if there is no such link
	 */
	static PaymentOutLine getPaymentOutLineForTransaction(@Nonnull ObjectContext context, @Nonnull AccountTransaction t) {
		if (t.getTableName() == AccountTransactionType.PAYMENT_OUT_LINE) {
			return SelectById.query(PaymentOutLine.class, t.getForeignRecordId()).selectOne(context)
		}
		return null
	}

	/**
	 * @return
	 */
	@Nullable
	@Override
	Object getValueForKey(@Nonnull String key) {
		switch (key) {
			case TRANSACTION_DESCRIPTION_PROPERTY:
				return getTransactionDescription()
			case SOURCE_STRING_PROPERTY:
				return getSource()
			case INVOICE_NUMBER_PROPERTY:
				return getInvoiceNumber()
			case PAYMENT_TYPE_PROPERTY:
				return getPaymentType()
			case CONTACT_NAME_PROPERTY:
				return getContactName()
		}

		return super.getValueForKey(key)
	}

	/**
	 * Generate description for transaction:
	 *
	 * * Invoice transaction -> invoice number + contact name
	 * * Payment in/out -> payment type (cc/cheque/cash/etc) + contact name
	 *
	 * @return string describing transaction
	 */
	@Nullable
	@API
	String getTransactionDescription() {
		if (AccountTransactionType.INVOICE_LINE == getTableName()) {
			InvoiceLine invoiceLine = getInvoiceLineForTransaction(getObjectContext(), this)
			if (invoiceLine != null) {
				return "invoice #" + invoiceLine.getInvoice().getInvoiceNumber() + ", " + invoiceLine.getInvoice().getContact().getName()
			}
		} else if (AccountTransactionType.PAYMENT_IN_LINE == getTableName() || AccountTransactionType.PAYMENT_OUT_LINE == getTableName()) {
			PaymentLineInterface pl = getPaymentInLineForTransaction(getObjectContext(), this)
			if (pl != null) {
				return pl.getPayment().getTypeOfPayment() + ", " + pl.getPayment().getPaymentMethod().getName() + ", " + pl.getPayment().getContact().getName()
			}
		}

		return ""
	}

	String getCourseName()
    {
        InvoiceLine invoiceLine = getInvoiceLineForTransaction(getObjectContext(), this)
		if (invoiceLine != null) {
            Enrolment enrolment = invoiceLine.getEnrolment()
			if (enrolment != null) {
                return enrolment.getCourseClass().getCourse().getName()
			}
        }
        return StringUtils.EMPTY
	}

	String getInvoiceDescription()
    {
        InvoiceLine invoiceLine = getInvoiceLineForTransaction(getObjectContext(), this)
		if (invoiceLine != null) {
            return invoiceLine.getDescription()
		}
        return StringUtils.EMPTY
	}


    /**
	 * Get number of related invoice if transaction type is {@link AccountTransactionType#INVOICE_LINE} or null if it is not.
	 *
	 * @return invoice number
	 */
	@Nullable
	@API
	Long getInvoiceNumber() {
		if (AccountTransactionType.INVOICE_LINE == getTableName()) {
			InvoiceLine invoiceLine = getInvoiceLineForTransaction(getObjectContext(), this)
			if (invoiceLine != null) {
				return invoiceLine.getInvoice().getInvoiceNumber()
			}
		}
		return null
	}

	String getPaymentType() {
		if (AccountTransactionType.PAYMENT_IN_LINE == getTableName() || AccountTransactionType.PAYMENT_OUT_LINE == getTableName()) {
			PaymentLineInterface pl = getPaymentInLineForTransaction(getObjectContext(), this)
			if (pl != null) {
				return pl.getPayment().getPaymentMethod().getName()
			}
		}
		return null
	}

	/**
	 * Get contact related to the transaction through invoice or payment.
	 *
	 * @return contact name
	 */
	@Nullable
	@API
	String getContactName() {
		if (AccountTransactionType.INVOICE_LINE == getTableName()) {
			InvoiceLine invoiceLine = getInvoiceLineForTransaction(getObjectContext(), this)
			if (invoiceLine != null) {
				return invoiceLine.getInvoice().getContact().getName()
			}
		} else if (AccountTransactionType.PAYMENT_IN_LINE == getTableName() || AccountTransactionType.PAYMENT_OUT_LINE == getTableName()) {
			PaymentLineInterface pl
			if (AccountTransactionType.PAYMENT_IN_LINE == getTableName()) {
				pl = getPaymentInLineForTransaction(getObjectContext(), this) as PaymentLineInterface
			} else {
				pl = getPaymentOutLineForTransaction(getObjectContext(), this) as PaymentLineInterface
			}

			if (pl != null) {
				return pl.getPayment().getContact().getName()
			}
		}
		return ""
	}

	String getSource() {
		return getTableName().getDisplayName().replace(" line", "")
	}

	/**
	 *
	 * @return the Persistent class to which this transaction is linked
	 */
	@Nullable
	@API
	Class<? extends PersistentObject> getSourceClass() {
		switch (getTableName()) {
			case AccountTransactionType.DEPRECIATION:
				return null

			case AccountTransactionType.INVOICE_LINE:
				return InvoiceLine.class

			case AccountTransactionType.JOURNAL:
				return null

			case AccountTransactionType.PAYMENT_IN_LINE:
				return PaymentInLine.class

			case AccountTransactionType.PAYMENT_OUT_LINE:
				return PaymentOutLine.class

			case AccountTransactionType.PURCHASE_LINE:
				return null
		}

		return null
	}


	PersistentObject getSourceRecord() {
		if (getSourceClass() == null) {
			return null
		}
		return SelectById.query(getSourceClass(), getForeignRecordId()).selectOne(getContext())
	}

	/**
	 * @return the value of this transaction
	 */
	@Nonnull
	@API
	@Override
	Money getAmount() {
		return super.getAmount()
	}

	/**
	 * @return the date and time this record was created
	 */
	@Nonnull
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	@Nonnull
	@Override
	Long getForeignRecordId() {
		return super.getForeignRecordId()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	@Override
	AccountTransactionType getTableName() {
		return super.getTableName()
	}

	/**
	 * @return the transaction date. This may be different to the creation or modification date
	 */
	@Nonnull
	@API
	@Override
	LocalDate getTransactionDate() {
		return super.getTransactionDate()
	}

	/**
	 * @return the account for this transaction
	 */
	@Nonnull
	@API
	@Override
	Account getAccount() {
		return super.getAccount()
	}
}
