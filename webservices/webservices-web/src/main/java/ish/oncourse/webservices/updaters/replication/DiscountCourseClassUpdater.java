package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.webservices.v4.stubs.replication.DiscountCourseClassStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class DiscountCourseClassUpdater extends AbstractWillowUpdater<DiscountCourseClassStub, DiscountCourseClass> {

	@Override
	protected void updateEntity(DiscountCourseClassStub stub, DiscountCourseClass entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(college);
		entity.setCourseClass(updateRelationShip(stub.getCourseClassId(), CourseClass.class, result));
		entity.setCreated(stub.getCreated());
		entity.setDiscount(updateRelationShip(stub.getDiscountId(), Discount.class, result));
		entity.setModified(stub.getModified());
	}
}
