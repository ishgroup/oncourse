package ish.oncourse.model;

import ish.oncourse.model.auto._CourseClassPaymentPlanLine;
import ish.oncourse.utils.QueueableObjectUtils;

public class CourseClassPaymentPlanLine extends _CourseClassPaymentPlanLine  implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
