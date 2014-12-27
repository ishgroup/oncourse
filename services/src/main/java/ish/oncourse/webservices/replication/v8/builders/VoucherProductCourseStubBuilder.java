package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.VoucherProductCourse;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.VoucherProductCourseStub;

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
