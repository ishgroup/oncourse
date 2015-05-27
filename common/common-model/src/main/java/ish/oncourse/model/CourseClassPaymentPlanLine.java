package ish.oncourse.model;

import ish.math.Money;
import ish.oncourse.model.auto._CourseClassPaymentPlanLine;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.validation.ValidationResult;

public class CourseClassPaymentPlanLine extends _CourseClassPaymentPlanLine implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	public void validateForSave(ValidationResult result) {
		super.validateForSave(result);

		if (getAmount() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, AMOUNT.getName(), "Amount must be set."));
		} else if (!getAmount().isGreaterThan(Money.ZERO)) {
			result.addFailure(ValidationFailure.validationFailure(this, AMOUNT.getName(), "Amount should not be positive."));
		}
	}
}
