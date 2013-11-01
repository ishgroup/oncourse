package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.WaitingList;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.WaitingListStub;

public class WaitingListStubBuilder extends AbstractWillowStubBuilder<WaitingList, WaitingListStub> {

	@Override
	protected WaitingListStub createFullStub(WaitingList entity) {
		WaitingListStub stub = new WaitingListStub();
		stub.setCourseId(entity.getCourse().getId());
		stub.setCreated(entity.getCreated());
		stub.setDetail(entity.getDetail());
		stub.setModified(entity.getModified());
		stub.setStudentCount(entity.getPotentialStudents());
		stub.setStudentId(entity.getStudent().getId());
		return stub;
	}
}
