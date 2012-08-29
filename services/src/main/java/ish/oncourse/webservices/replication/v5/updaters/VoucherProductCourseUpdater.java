package ish.oncourse.webservices.replication.v5.updaters;

import ish.oncourse.model.Course;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.model.VoucherProductCourse;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.VoucherProductCourseStub;

public class VoucherProductCourseUpdater extends AbstractWillowUpdater<VoucherProductCourseStub, VoucherProductCourse> {

	@Override
	protected void updateEntity(final VoucherProductCourseStub stub, final VoucherProductCourse entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		final Course course = callback.updateRelationShip(stub.getCourseId(), Course.class);
		entity.setCourse(course);
		final VoucherProduct voucherProduct = callback.updateRelationShip(stub.getVoucherProductId(), VoucherProduct.class);
		entity.setVoucherProduct(voucherProduct);
	}

}
