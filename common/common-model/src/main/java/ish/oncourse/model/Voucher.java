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
			throw new IllegalStateException("Cannot change status for already persisted voucher in non active status.");
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
}
