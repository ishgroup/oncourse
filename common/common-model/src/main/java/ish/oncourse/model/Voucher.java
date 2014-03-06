package ish.oncourse.model;

import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.auto._Voucher;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang.StringUtils;

public class Voucher extends _Voucher implements Queueable {
	private static final long serialVersionUID = -836996096054884238L;
	public static final int VOUCHER_CODE_LENGTH = 8;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/**
	 * @return true if object is new
	 */
	private boolean isNewRecord() {
		return getPersistenceState() == PersistenceState.NEW;
	}
	
	@Override
	public void validateForSave(ValidationResult result) {
		super.validateForSave(result);
		if (StringUtils.trimToNull(getCode()) == null) {
			result.addFailure(ValidationFailure.validationFailure(this, CODE_PROPERTY, "Code cannot be null."));
		}
		if (getExpiryDate() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, EXPIRY_DATE_PROPERTY, "Expiry date cannot be null."));
		}
		if (getStatus() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, STATUS_PROPERTY, "Status cannot be null."));
		}
		if (getSource() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, SOURCE_PROPERTY, "Source cannot be null."));
		}

		if (getVoucherProduct() != null && getVoucherProduct().getMaxCoursesRedemption() == null) {
			if (getValueOnPurchase() == null) {
				result.addFailure(ValidationFailure.validationFailure(
						this, VALUE_ON_PURCHASE_PROPERTY, "Value on purchase must be set for money vouchers."));
			} else if (!getValueOnPurchase().isGreaterThan(Money.ZERO)) {
				result.addFailure(ValidationFailure.validationFailure(
						this, VALUE_ON_PURCHASE_PROPERTY, "Voucher purchase value must be greater than zero."));
			}
		}
	}
	
	public VoucherProduct getVoucherProduct() {
		return (VoucherProduct) getProduct();
	}
	
	public boolean isFullyRedeemed() {
		VoucherProduct product = getVoucherProduct();
		if (product.getMaxCoursesRedemption() != null && product.getMaxCoursesRedemption() > 0) {
			return getClassesRemaining() <= 0;
		} else {
			return getValueRemaining().isZero();
		}
	}
	
	/**
	 * Calculate remaining value for voucher
	 * 
	 * @return remaining value
	 */
	public Money getValueRemaining() {
		return getRedemptionValue();
	}
	
	/**
	 * Get number of classes left to redeem
	 * 
	 * @return remaining classes count
	 */
	public Integer getClassesRemaining() {
		if (getVoucherProduct().getMaxCoursesRedemption() == null || getRedeemedCoursesCount() == null) {
			throw new IllegalStateException("This voucher is for money redemption only");
		}
		return getVoucherProduct().getMaxCoursesRedemption() - getRedeemedCoursesCount();
	}
	
	/**
	 * Determines whether voucher has not finished redemption payments.
	 * 
	 * @return if voucher is in use
	 */
	public boolean isInUse() {
		for (PaymentIn p : getPayments()) {
			if (!PaymentStatus.STATUSES_FINAL.contains(p.getStatus())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines whether voucher is applicable to specified enrolment.
	 * 
	 * @param enrolment
	 * @return if voucher is applicable to enrolment
	 */
	public boolean isApplicableTo(Enrolment enrolment) {
		if (getVoucherProduct().getRedemptionCourses().isEmpty()) {
			return true;
		}
		return getVoucherProduct().getRedemptionCourses().contains(enrolment.getCourseClass().getCourse());
	}
	
	/**
	 * Determines whether voucher can be used by specific contact.
	 * 
	 * @return if voucher can be used by contact
	 */
	public boolean canBeUsedBy(Contact contact) {
		return getContact() == null || getContact().equals(contact);
	}

	/**
	 * Returns true if voucher can be redeemed for money, false if it can be redeemed for
	 * enrolments count.
	 */
	public boolean isMoneyVoucher() {
		return getVoucherProduct().getMaxCoursesRedemption() == null;
	}
}
