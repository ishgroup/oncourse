package ish.oncourse.webservices.replication.v18.updaters;

import ish.oncourse.model.Course;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.model.VoucherProductCourse;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v18.stubs.replication.VoucherProductCourseStub;

public class VoucherProductCourseUpdater extends AbstractWillowUpdater<VoucherProductCourseStub, VoucherProductCourse> {

	@Override
	protected void updateEntity(final VoucherProductCourseStub stub, final VoucherProductCourse entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setCourse(callback.updateRelationShip(stub.getCourseId(), Course.class));
		entity.setVoucherProduct(callback.updateRelationShip(stub.getVoucherProductId(), VoucherProduct.class));
	}

}
