package ish.oncourse.model;

import ish.oncourse.model.auto._Outcome;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.validation.ValidationResult;

public class Outcome extends _Outcome implements Queueable {
	private static final long serialVersionUID = 6238039938398442623L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}


	@Override
	protected void validateForSave(ValidationResult result) {
		super.validateForSave(result);

		if (getEnrolment() == null && getPriorLearning() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, _Outcome.ENROLMENT.getName(),
					"Can not save Outcome angelId: " + getAngelId() + " without linked Enrolment or PriorLearning"));
		}
	}
}
