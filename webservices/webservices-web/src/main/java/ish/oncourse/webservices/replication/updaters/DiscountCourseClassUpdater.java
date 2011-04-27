package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.webservices.v4.stubs.replication.DiscountCourseClassStub;

public class DiscountCourseClassUpdater extends AbstractWillowUpdater<DiscountCourseClassStub, DiscountCourseClass> {

	@Override
	protected void updateEntity(DiscountCourseClassStub stub, DiscountCourseClass entity, RelationShipCallback callback) {
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class));
		entity.setCreated(stub.getCreated());
		entity.setDiscount(callback.updateRelationShip(stub.getDiscountId(), Discount.class));
		entity.setModified(stub.getModified());
	}
}
