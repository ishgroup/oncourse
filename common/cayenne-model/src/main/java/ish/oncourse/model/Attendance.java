package ish.oncourse.model;

import ish.common.types.AttendanceType;
import ish.oncourse.cayenne.AttendanceInterface;
import ish.oncourse.model.auto._Attendance;
import ish.oncourse.utils.QueueableObjectUtils;

public class Attendance extends _Attendance implements Queueable, AttendanceInterface {
	private static final long serialVersionUID = 5699863428784294272L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	public boolean isAbsent() {
		return AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON.getDatabaseValue().equals(this.getAttendanceType()) ||
				AttendanceType.DID_NOT_ATTEND_WITH_REASON.getDatabaseValue().equals(this.getAttendanceType());
	}
}
