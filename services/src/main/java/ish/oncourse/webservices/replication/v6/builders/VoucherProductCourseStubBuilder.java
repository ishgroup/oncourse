package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.VoucherProductCourse;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.VoucherProductCourseStub;

public class VoucherProductCourseStubBuilder extends AbstractWillowStubBuilder<VoucherProductCourse, VoucherProductCourseStub> {

	@Override
	protected VoucherProductCourseStub createFullStub(final VoucherProductCourse entity) {
		VoucherProductCourseStub stub = new VoucherProductCourseStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setCourseId(entity.getCourse().getId());
		stub.setVoucherProductId(entity.getVoucherProduct().getId());
		return stub;
	}

}
