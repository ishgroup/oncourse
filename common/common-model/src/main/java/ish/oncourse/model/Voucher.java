package ish.oncourse.model;

import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang.StringUtils;

import ish.common.types.VoucherStatus;
import ish.math.Money;
import ish.oncourse.model.auto._Voucher;
import ish.oncourse.utils.QueueableObjectUtils;

public class Voucher extends _Voucher implements Queueable {
	private static final long serialVersionUID = -836996096054884238L;
	public static final int VOUCHER_CODE_LENGTH = 8;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
	
	@Override
	public void setCode(String code) {
		if (!isNewRecord() && getCode() != null) {
			throw new IllegalStateException("Cannot change code for already persisted voucher.");
		}
		super.setCode(code);
	}
	
	/**
	 * @return true if object is new
	 */
	private boolean isNewRecord() {
		return getPersistenceState() == PersistenceState.NEW;
	}
	
	@Override
	public void setRedemptionValue(Money value) {
		if (!isNewRecord() && getRedemptionValue() != null) {
			throw new IllegalStateException("Cannot change redemption value for already persisted voucher.");
		}
		super.setRedemptionValue(value);
	}

	@Override
	public void setStatus(VoucherStatus status) {
		if (!isNewRecord() && !VoucherStatus.ACTIVE.equals(getStatus())) {
			if (VoucherStatus.REDEEMED.equals(getStatus()) && VoucherStatus.ACTIVE.equals(status)) {
				//this can be the discard changes action
			} else {
				throw new IllegalStateException("Cannot change status for already persisted voucher in non active status.");
			}
		}
		super.setStatus(status);
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
	}
	
	public VoucherProduct getVoucherProduct() {
		return (VoucherProduct) getProduct();
	}
	
	public Voucher makeShallowCopy() {
		Voucher voucherCopy = getObjectContext().newObject(Voucher.class);
		voucherCopy.setCode(getCode());
		voucherCopy.setCollege(getCollege());
		voucherCopy.setSource(getSource());
		voucherCopy.setExpiryDate(getExpiryDate());
		voucherCopy.setProduct(getVoucherProduct());
		voucherCopy.setInvoiceLine(getInvoiceLine());
		voucherCopy.setRedemptionValue(getRedemptionValue());
		voucherCopy.setRedeemedCoursesCount(getRedeemedCoursesCount());
		voucherCopy.setStatus(getStatus());
		return voucherCopy;
	}
	
	public boolean isFullyRedeemed() {
		VoucherProduct product = getVoucherProduct();
		if (product.getMaxCoursesRedemption() != null && product.getMaxCoursesRedemption() > 0) {
			return getClassesRemaining() == 0;
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
	
}
