package ish.oncourse.webservices.replication.v17.updaters;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.DiscountCourseClassStub;

public class DiscountCourseClassUpdater extends AbstractWillowUpdater<DiscountCourseClassStub, DiscountCourseClass> {

	@Override
	protected void updateEntity(DiscountCourseClassStub stub, DiscountCourseClass entity, RelationShipCallback callback) {
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class));
		entity.setCreated(stub.getCreated());
		entity.setDiscount(callback.updateRelationShip(stub.getDiscountId(), Discount.class));
		entity.setModified(stub.getModified());
		if (stub.getDiscountAmount() != null) {
			entity.setDiscountAmount(Money.valueOf(stub.getDiscountAmount()));
		}
	}
}
