package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.webservices.v4.stubs.replication.DiscountCourseClassStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class DiscountCourseClassUpdater extends AbstractWillowUpdater<DiscountCourseClassStub, DiscountCourseClass> {
	
	public DiscountCourseClassUpdater(College college, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, next);
	}

	@Override
	protected void updateEntity(DiscountCourseClassStub stub, DiscountCourseClass entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollegeId(college.getId());
		entity.setCourseClass((CourseClass) updateRelatedEntity(stub.getCourseClass(), relationStubs));
		entity.setCreated(stub.getCreated());
		entity.setDiscount((Discount) updateRelatedEntity(stub.getDiscount(), relationStubs));
		entity.setModified(stub.getModified());		
	}
}
