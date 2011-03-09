package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.DiscountCourseClassStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class DiscountCourseClassUpdater extends AbstractWillowUpdater<DiscountCourseClassStub, DiscountCourseClass> {
	
	public DiscountCourseClassUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}

	@Override
	protected void updateEntity(DiscountCourseClassStub stub, DiscountCourseClass entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(getCollege(entity.getObjectContext()));
		entity.setCourseClass((CourseClass) updateRelatedEntity(entity.getObjectContext(), stub.getCourseClass(), relationStubs));
		entity.setCreated(stub.getCreated());
		entity.setDiscount((Discount) updateRelatedEntity(entity.getObjectContext(), stub.getDiscount(), relationStubs));
		entity.setModified(stub.getModified());		
	}
}
