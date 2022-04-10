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

import ish.common.types.ConfirmationStatus
import ish.common.types.CreditCardType
import ish.common.types.EnrolmentStatus
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.AccountInterface
import ish.oncourse.cayenne.BankingInterface
import ish.oncourse.cayenne.ContactInterface
import ish.oncourse.cayenne.PaymentInInterface
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.cayenne.PaymentMethodInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.api.dao.PaymentOutDao
import ish.oncourse.server.cayenne.glue._PaymentIn
import ish.util.CreditCardUtil
import ish.util.InvoiceUtil
import ish.validation.ValidationFailure
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.validation.ValidationResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate
/**
 * A payment received by the college. Each payment can be linked to one or more invoices (or credit notes), and may
 * partially pay an invoice or fully discharge the amount owing.
 */
@API
@QueueableEntity
class PaymentIn extends _PaymentIn implements PaymentInInterface, Queueable, ContactActivityTrait {



	private static final Logger logger = LogManager.getLogger()

	/**
	 * @see ish.oncourse.server.cayenne.glue.CayenneDataObject#onEntityCreation()
	 */
	@Override
	void onEntityCreation() {
		super.onEntityCreation()
		if (getAmount() == null) {
			setAmount(Money.ZERO)
		}
		if (getPrivateNotes() == null) {
			setPrivateNotes("")
		}
		if (getReconciled() == null) {
			setReconciled(false)
		}
		if (getSource() == null) {
			setSource(PaymentSource.SOURCE_ONCOURSE)
		}
	}

	@Override
	void postAdd() {
		super.postAdd()

		if (getAmount() == null) {
			setAmount(Money.ZERO)
		}

		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND)
		}
	}

	@Override
	protected void preUpdate() {
		super.preUpdate()
	}

	/**
	 *
	 * @return the onCourse payment gateway reference. This value will start with "W" for payments taken online.
	 */
	@Nullable
	@API
	String getCreditCardClientReference() {
		if (sessionId != null ) {
			try {
				UUID.fromString(sessionId)
				return sessionId
			} catch (IllegalArgumentException exception){
				//handle the case where string is not valid UUID
			}
		}
		if (getWillowId() == null) {
			return ""
		} else if (PaymentSource.SOURCE_WEB == getSource()) {
			return PaymentSource.SOURCE_WEB.getDatabaseValue() + getWillowId()
		}
		return String.valueOf(getWillowId())
	}

	/**
	 * See PaymentStatus for a list of statuses which are final.
	 *
 	 * @return true if the payment is in a final state which can no longer be changed
	 */
	@API
	boolean isFinalised() {
		return PaymentStatus.STATUSES_FINAL.contains(getStatus())
	}

	boolean isNonZeroAmount() {
		return getAmount() != null && !getAmount().isZero()
	}

	@Override
	void validateForSave(@Nonnull final ValidationResult result) {
		super.validateForSave(result)

		if (Boolean.TRUE == getReconciled() && getBanking() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, RECONCILED.getName(), "The payment without Banking cannot be reconciled."))
		}

		// Amount - mandatory field
		if (getAmount() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, AMOUNT_PROPERTY, "The payment amount cannot be empty."))
		} else if ((PaymentType.CONTRA == getPaymentMethod().getType() || PaymentType.REVERSE == getPaymentMethod().getType())
				&& !getAmount().isZero()) {
			result.addFailure(ValidationFailure.validationFailure(this, PaymentInLine.AMOUNT_PROPERTY, "The CONTRA and REVERSE payment-in amount must be \$0"))
		} else {

			Money sum = InvoiceUtil.sumPaymentLines(getPaymentInLines(), TYPE_IN, false)

			if (getAmount() != sum) {
				result.addFailure(ValidationFailure.validationFailure(this, AMOUNT_PROPERTY, "The payment (" + getId() + ") amount (=" +
						getAmount() + ") does not match the sum (" + sum + ") of amounts allocated for invoices/credit notes."))
			}
		}
	}

	@Nullable
	@Override
	String toString() {
		String result = ""
		result = result + "status : " + getStatus()
		result = result + " method : " + getPaymentMethod().getName()
		result = result + " amount : " + getAmount()
		result = result + " PersistenceState : " + PersistenceState.persistenceStateName(getPersistenceState())

		return result
	}

	/**
	 * @return list of invoices linked to this payment record
	 */
	@Nonnull
	@API
	List<Invoice> getInvoices() {
		final List<Invoice> invoices = new ArrayList<>()
		final List<PaymentInLine> payInLines = getPaymentInLines()
		if (payInLines != null) {
			for (final PaymentInLine payInLine : payInLines) {
				if (!invoices.contains(payInLine.getInvoice())) {
					invoices.add(payInLine.getInvoice())
				}
			}
		}
		return invoices
	}

	void obfuscateCreditCardNumber() {
		setCreditCardNumber(CreditCardUtil.obfuscateCCNumber(getCreditCardNumber()))
	}

	void updateAmountsOwing(@Nonnull ObjectContext context) {
		for (Invoice invoice : getInvoices()) {
			Invoice localInvoice = invoice
			if (localInvoice.getObjectContext() != context) {
				localInvoice = context.localObject(invoice)
			}
			localInvoice.updateAmountOwing()
		}
	}

	boolean isSuccess() {
		return PaymentStatus.SUCCESS == getStatus()
	}

	/**
	 * @see PaymentInterface#getTypeOfPayment()
	 * @return
	 */
	String getTypeOfPayment() {
		return TYPE_IN
	}

	@Override
	void setPaymentMethod(PaymentMethodInterface method) {
		super.setPaymentMethod((PaymentMethod) method)
	}

	@Override
	void setAccount(AccountInterface account) {
		setAccountIn((Account) account)
	}

	/**
	 * The accountIn can be only of ASSET or LIABILITY type, as the payment is representation of a real world payment and real world money
     * or voucher
	 *
	 */
	@Override
	void setAccountIn(@Nullable Account accountIn) {
		if (accountIn == null) {
			return
		}
		if (accountIn.isAsset() ||  accountIn.isLiability()) {
            super.setAccountIn(accountIn)
		} else {
            throw new IllegalStateException("Only Asset or Liability account can be linked to payment in")
		}
	}

	@Nullable
	String getShortRecordDescription() {
		return null
	}


	@Override
	Object getValueForKey(String key) {

		if (PAYMENT_TYPE_PROPERTY == key) {
			return TYPE_IN
		}

		return readProperty(key)
	}

	/**
	 * Overriding the entity setter to make sure illegal changes aren't allowed. <br>
	 * <br>
	 * List of allowed status changes: <br>
	 * <ul>
	 * <li>null -> anything</li>
	 * <li>NEW -> anything but null</li>
	 * <li>QUEUED -> anything but null/NEW</li>
	 * <li>IN_TRANSACTION -> anything but null/NEW/QUEUED</li>
	 * <li>CARD_DETAILS_REQUIRED -> anything but null/NEW/QUEUED</li>
	 * <li>SUCCESS -> only STATUS_CANCELLED/STATUS_REFUNDED allowed</li>
	 * <li>FAILED/FAILED_CARD_DECLINED/FAILED_NO_PLACES -> no further status change allowed</li>
	 * <li>STATUS_CANCELLED/STATUS_REFUNDED -> no further status change allowed</li>
	 * </ul>
	 *
	 * @param status new payment status to set
	 */
	@Override
	void setStatus(@Nullable PaymentStatus status) {

		if (getStatus() != null && getStatus() != status) {
			if (status == null) {
				throw new IllegalArgumentException("Can't change status to null.")
			} else if (PaymentStatus.QUEUED == getStatus()) {
				if (PaymentStatus.NEW == status) {
					throw new IllegalArgumentException("Can't change status from QUEUED to NEW.")
				}
			} else if (PaymentStatus.IN_TRANSACTION == getStatus() || PaymentStatus.CARD_DETAILS_REQUIRED == getStatus()) {
				if (PaymentStatus.NEW == status || PaymentStatus.QUEUED == status) {
					throw new IllegalArgumentException("Can't change status from IN_TRANSACTION to NEW/QUEUED.")
				}
			} else if (PaymentStatus.SUCCESS == getStatus()) {
				if (PaymentStatus.SUCCESS != status) {
					throw new IllegalArgumentException("Can't change status from SUCCESS to other statuses.")
				}
			} else if (PaymentStatus.STATUSES_FINAL.contains(getStatus()) && status != getStatus()) {

				// once payment in final status all status changes are forbidden
				throw new IllegalArgumentException("Can't change payment status from " + getStatus() + " to " + status)
			}
		}

		super.setStatus(status)
		if (PaymentStatus.SUCCESS == status) {

			Date date = new Date()
			for (PaymentInLine pil : getPaymentInLines()) {
				pil.setModifiedOn(date)
			}
		}
	}

	/**
	 * Sets the status of payment to {@link PaymentStatus#SUCCESS}, and sets the success statuses to the related invoice ( {@link PaymentStatus#SUCCESS} ) and
	 * enrolment ( {@link EnrolmentStatus#SUCCESS} ). Invoked when the payment gateway processing is succeed.
	 */
	void succeed() {
		setStatus(PaymentStatus.SUCCESS)
		for (PaymentInLine pl : getPaymentInLines()) {
			Invoice invoice = pl.getInvoice()
			for (InvoiceLine il : invoice.getInvoiceLines()) {
				Enrolment enrol = il.getEnrolment()
				if (enrol != null && (enrol.getStatus() == null || !EnrolmentStatus.STATUSES_FINAL.contains(enrol.getStatus()))) {
					enrol.setStatus(EnrolmentStatus.SUCCESS)
				}
				for (ProductItem productItem : il.getProductItems()) {
					if (ProductStatus.NEW == productItem.getStatus()) {
						productItem.setStatus(ProductStatus.ACTIVE)
					}
				}
			}
		}
	}

	void removeFromPaymentLines(PaymentLineInterface pLine) {
		removeFromPaymentInLines((PaymentInLine) pLine)
	}

	void addToPaymentLines(PaymentLineInterface pLine) {
		addToPaymentInLines((PaymentInLine) pLine)
	}

	/**
	 * @return list of payment lines linked to this payment
	 */
	@Nonnull
	@API
	List<? extends PaymentLineInterface> getPaymentLines() {
		return getPaymentInLines()
	}

	@Nonnull
	ContactInterface getContact() {
		return getPayer()
	}

	@Override
	boolean isAsyncReplicationAllowed() {
		return getStatus() != null && PaymentStatus.NEW != getStatus() && PaymentStatus.QUEUED != getStatus() && PaymentStatus.IN_TRANSACTION != getStatus()
	}

	/**
	 * @return amount of money paid
	 */
	@Nonnull
	@API
	@Override
	Money getAmount() {
		return super.getAmount()
	}

	/**
	 * @return bank information of a cheque
	 */
	@API
	@Override
	String getChequeBank() {
		return super.getChequeBank()
	}

	/**
	 * @return branch information for a cheque
	 */
	@API
	@Override
	String getChequeBranch() {
		return super.getChequeBranch()
	}

	/**
	 * @return drawer name for a cheque
	 */
	@API
	@Override
	String getChequeDrawer() {
		return super.getChequeDrawer()
	}

	/**
	 * @return confirmation email sending status: not sent, sent or suppressed from sending
	 */
	@Nonnull
	@API
	@Override
	ConfirmationStatus getConfirmationStatus() {
		return super.getConfirmationStatus()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	@Override
	String getInteractionName() {
		return amount.toPlainString()
	}

	@Override
	String getCreditCardExpiry() {
		return super.getCreditCardExpiry()
	}

	@Override
	String getCreditCardName() {
		return super.getCreditCardName()
	}

	@Override
	String getCreditCardNumber() {
		return super.getCreditCardNumber()
	}

	@Override
	CreditCardType getCreditCardType() {
		return super.getCreditCardType()
	}

	/**
	 * @return date when payment was banked
	 */
	@Nullable
	@API
	LocalDate getDateBanked() {
		return getBanking() != null ? getBanking().getSettlementDate() : null
	}

	/**
	 * @return the reference code returned by the banking system
	 */
	@API
	@Override
	String getGatewayReference() {
		return super.getGatewayReference()
	}

	/**
	 * This is typically only useful for debugging since the data here can vary a lot.
	 *
	 * @return the details of the response received from the banking system.
	 */
	@API
	@Override
	String getGatewayResponse() {
		return super.getGatewayResponse()
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
	 * Private notes are typically not to be shared with the customer.
	 *
	 * @return any private notes stored on the payment
	 */
	@API
	@Override
	String getPrivateNotes() {
		return super.getPrivateNotes()
	}

	/**
	 * @return true if this payment is reconciled
	 */
	@Nonnull
	@API
	@Override
	Boolean getReconciled() {
		return super.getReconciled()
	}

	/**
	 * @return
	 */
	@Override
	String getSessionId() {
		return super.getSessionId()
	}

	/**
	 * @return this records whether the payment was created online or in the office
	 */
	@Nonnull
	@API
	@Override
	PaymentSource getSource() {
		return super.getSource()
	}

	/**
	 * During processing in onCourse payment undergoes a number of status changes until it settles on one of the final
	 * statuses. On creation payment receives NEW status immediately, after payment details are settled and processing
	 * begins payment is set to IN TRANSACTION status, then after processing is finished payment receives final status
	 * which may be one of the FAILED statuses or a SUCCESSFUL status.
	 *
	 * @return current status of processing for this payment
	 */
	@API
	@Override
	PaymentStatus getStatus() {
		return super.getStatus()
	}

	/**
	 * @return the method used to make this payment
	 */
	@Nonnull
	@API
	@Override
	PaymentMethod getPaymentMethod() {
		return super.getPaymentMethod()
	}

	/**
	 * This account is copied from the PaymentMethod object when the PaymentIn is created. We keep a copy here
	 * because the PaymentMethod account might change from time to time and we need to store the value at time
	 * of creation.
	 *
	 * @return this is the asset account into which the payment is deposited
	 */
	@Nonnull
	@API
	@Override
	Account	getAccountIn() {
		return super.getAccountIn()
	}

	/**
	 * Some sites can be designated as "administration centres" meaning that they are allowed to accept payments
	 * and perform banking functions. A user can choose their site when they log in and all payments created by them
	 * from that point are linked to that Site. This is then used to group payments when performing banking.
	 *
	 * @return the site which received the payment
	 */
	@Nullable
	@API
	@Override
	Site getAdministrationCentre() {
		return super.getAdministrationCentre()
	}

	/**
	 * @return user who created this payment
	 */
	@Nonnull
	@API
	@Override
	SystemUser getCreatedBy() {
		return super.getCreatedBy()
	}

	/**
	 * @return contact who is the payer
	 */
	@Nonnull
	@API
	@Override
	Contact getPayer() {
		return super.getPayer()
	}

	/**
	 * @return list of payment lines linked to this payment
	 */
	@Nonnull
	@API
	@Override
	List<PaymentInLine> getPaymentInLines() {
		return super.getPaymentInLines()
	}

	/**
	 * @return another payment instance which current payment reverses, null if this payment is not a reversal
	 */
	@API
	@Override
	PaymentIn getReversalOf() {
		return super.getReversalOf()
	}

	/**
	 * A user can reverse a payment, which cancels it and reverses the general ledger transactions.
	 *
	 * @return payment in which reversed this payment, null if payment is not reversed
	 */
	@Nullable
	@API
	@Override
	PaymentIn getReversedBy() {
		return super.getReversedBy()
	}

	/**
	 * @return list of vouchers linked to this payments
	 */
	@Nonnull
	@API
	@Override
	List<VoucherPaymentIn> getVoucherPayments() {
		return super.getVoucherPayments()
	}

	@Override
	void setBanking(BankingInterface banking) {
		super.setBanking((Banking) banking)
	}

	@Override
	void setUndepositedFundsAccount(AccountInterface accountInterface) {
		super.setUndepositedFundsAccount((Account)accountInterface)
	}

	/**
	 * @return payment method name
	 */
	@Nonnull
	@Deprecated
	String getType() {
		return getPaymentMethod().getName()
	}

	String getStatusString() {
		if (status == PaymentStatus.SUCCESS && relatedToReverse) {
			return "Success (reversed)"
		}
		return status.displayName
	}

	boolean isRelatedToReverse() {
		return getReversalOf() != null || getReversedBy() != null || (PaymentType.CREDIT_CARD == paymentMethod.type && !PaymentOutDao.getReversedFor(this).empty)
	}
}
