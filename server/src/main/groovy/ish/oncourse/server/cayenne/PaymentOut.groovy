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
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.AccountInterface
import ish.oncourse.cayenne.BankingInterface
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.cayenne.PaymentMethodInterface
import ish.oncourse.cayenne.PaymentOutInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._PaymentOut
import ish.util.CreditCardUtil
import ish.validation.ValidationFailure
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate
/**
 * A payment or refund from college to someone else. Each payment can be linked to one or more invoices (or credit notes), and may
 * partially pay an invoice or fully discharge the amount owing.
 */
@API
@QueueableEntity
class PaymentOut extends _PaymentOut implements PaymentOutInterface, Queueable, ContactActivityTrait {

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
	}

	@Override
	protected void preUpdate() {
		super.preUpdate()
	}

	@Nullable
	protected PaymentIn getCorrespondingPaymentIn() {
		logger.debug("testing corresponding paymentIn:{}", getPaymentInGatewayReference())
		if (!StringUtils.isBlank(getPaymentInGatewayReference()) && getPayee() != null) {

			List<PaymentIn> paymentIns = (ObjectSelect.query(PaymentIn.class).
					where(PaymentIn.GATEWAY_REFERENCE.like(getPaymentInGatewayReference())) & PaymentIn.PAYER.eq(getPayee())).
					orderBy(PaymentIn.CREATED_ON.desc()).
					select(getObjectContext())

			logger.debug("found:{}", paymentIns.size())
			if (paymentIns.size() >= 1) {
				return paymentIns.get(0)
			}
		}
		return null
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

	@Nonnull
	private List<Invoice> getInvoices() {
		final List<Invoice> invoices = new ArrayList<>()
		final List<PaymentOutLine> payOutLines = getPaymentOutLines()
		for (final PaymentOutLine payOutLine : payOutLines) {
			if (!invoices.contains(payOutLine.getInvoice())) {
				invoices.add(payOutLine.getInvoice())
			}
		}
		return invoices
	}

	void obfuscateCreditCardNumber() {
		setCreditCardNumber(CreditCardUtil.obfuscateCCNumber(getCreditCardNumber()))
	}

	boolean isSuccess() {
		return PaymentStatus.SUCCESS == getStatus()
	}

	/**
	 * @see PaymentInterface#getTypeOfPayment()
	 * @return
	 */
	String getTypeOfPayment() {
		return TYPE_OUT
	}

	@Override
	void setPaymentMethod(PaymentMethodInterface method) {
		super.setPaymentMethod((PaymentMethod) method)
	}

	@Override
	void setAccount(AccountInterface accountInterface) {
		setAccountOut((Account) accountInterface)
	}

	/**
	 * The accountOut can be only of ASSET type, as the payment is representation of a real world payment and real world money
	 *
	 */
	@Override
	void setAccountOut(@Nonnull Account accountOut) {
		if (!accountOut.isAsset()) {
			throw new IllegalStateException("Only Asset account can be linked to payment out")
		}
		super.setAccountOut(accountOut)
	}

	boolean isFinalised() {
		return PaymentStatus.STATUSES_FINAL.contains(getStatus())
	}

	/**
	 * @return
	 */
	@Nullable
	String getGatewayReferenceForRefundingACCPayment() {
		if (getPayee() == null) {
			return null
		}
		List<PaymentIn> list = getPayee().getPaymentsIn()
		for (PaymentIn pIn : list) {
			if (PaymentType.CREDIT_CARD == pIn.getPaymentMethod().getType()) {
				if (getAmount() > pIn.getAmount()) {
					if (pIn.getGatewayReference() != null) {
						return pIn.getGatewayReference()
					}
				}
			}
		}
		return null
	}

	@Override
	void validateForSave(@Nonnull ValidationResult result) {
		super.validateForSave(result)

		// Amount - mandatory field
		if (getAmount() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, AMOUNT_PROPERTY, "The payment amount cannot be empty."))
		} else {
			Money sum = Money.ZERO
			List<PaymentOutLine> list = getPaymentOutLines()
			if (list != null) {
				for (PaymentOutLine poutl : list) {
					sum = sum.add(poutl.getAmount())
				}
			}

			if (getAmount() != sum) {
				result.addFailure(ValidationFailure.validationFailure(this, AMOUNT_PROPERTY,
						"The payment amount does not match the sum of amounts allocated for invoices/credit notes."))
			}

		}
	}

	String getShortRecordDescription() {
		return null
	}

	@Override
	Object getValueForKey(String key) {

		if (PAYMENT_TYPE_PROPERTY == key) {
			return TYPE_OUT
		}

		return readProperty(key)
	}

	/**
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
	 */
	@Override
	void setStatus(@Nullable final PaymentStatus status) {

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
			for (PaymentOutLine pol : getPaymentOutLines()) {
				pol.setModifiedOn(date)
			}
		}
	}

	void removeFromPaymentLines(PaymentLineInterface pLine) {
		removeFromPaymentOutLines((PaymentOutLine) pLine)
	}

	void addToPaymentLines(PaymentLineInterface pLine) {
		addToPaymentOutLines((PaymentOutLine) pLine)
	}

	/**
	 * @return list of linked payment lines
	 */
	@Nonnull
	@API
	List<? extends PaymentLineInterface> getPaymentLines() {
		return getPaymentOutLines()
	}

	/**
	 * @return contact who receives the payment
	 */
	@Nonnull
	@API
	Contact getContact() {
		return getPayee()
	}

	/**
	 * Checks if async replication allowed on entity.
	 *
	 * @return isAsyncReplicationAllowed
	 */
	@Override
	boolean isAsyncReplicationAllowed() {
		return getStatus() != null && PaymentStatus.QUEUED != getStatus() && PaymentStatus.IN_TRANSACTION != getStatus() &&
				PaymentStatus.CARD_DETAILS_REQUIRED != getStatus()
	}

	@Override
	protected void postAdd() {
		super.postAdd()
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND)
		}
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
	 * @return
	 */
	@API
	@Override
	String getChequeBank() {
		return super.getChequeBank()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getChequeBranch() {
		return super.getChequeBranch()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getChequeDrawer() {
		return super.getChequeDrawer()
	}

	/**
	 * @return
	 */
	@API
	@Override
	String getChequeNumber() {
		return super.getChequeNumber()
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
	 * @return
	 */
	@API
	@Nonnull
	@Override
	LocalDate getPaymentDate() {
		return super.getPaymentDate()
	}

	/**
	 * @return payment reference code in the banking system
	 */
	@API
	@Override
	String getGatewayReference() {
		return super.getGatewayReference()
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
	 * @return
	 */
	@API
	@Override
	String getPaymentInGatewayReference() {
		return super.getPaymentInGatewayReference()
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
	 * @return true if this payment has been reconciled
	 */
	@Nonnull
	@API
	@Override
	Boolean getReconciled() {
		return super.getReconciled()
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
	 * @return account linked with this PaymentOut
	 */
	@Nonnull
	@API
	@Override
	Account getAccountOut() {
		return super.getAccountOut()
	}

	/**
	 * Some sites can be designated as "administration centres" meaning that they are allowed to accept payments
	 * and perform banking functions. A user can choose their site when they log in and all payments created by them
	 * from that point are linked to that Site.
	 *
	 * @return the site which issued the PaymentOut
	 */
	@Nonnull
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
	 * @return contact who receives the payment
	 */
	@Nonnull
	@API
	@Override
	Contact getPayee() {
		return super.getPayee()
	}

	/**
	 * @return list of payment lines linked to this payment
	 */
	@Nonnull
	@API
	@Override
	List<PaymentOutLine> getPaymentOutLines() {
		return super.getPaymentOutLines()
	}

	@Override
	void setBanking(BankingInterface banking) {
		super.setBanking((Banking) banking)
	}

	@Override
	void setUndepositedFundsAccount(AccountInterface accountInterface) {
		super.setUndepositedFundsAccount((Account) accountInterface)
	}

	/**
	 * @return payment method name
	 */
	@Nonnull
	@API
	String getType() {
		return getPaymentMethod().getName()
	}

	String getStatusString() {
		return getStatus().getDisplayName()
	}
}
