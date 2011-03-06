package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;

import java.util.Map;

public class EnrolmentStubBuilder extends AbstractWillowStubBuilder<Enrolment, EnrolmentStub> {
	
	public EnrolmentStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowQueueService queueService, IWillowStubBuilder next) {
		super(queue, queueService, next);
	}

	@Override
	protected EnrolmentStub createFullStub(Enrolment entity) {
		EnrolmentStub stub = new EnrolmentStub();
		
		stub.setAngelId(entity.getAngelId());
		stub.setCourseClass(findRelationshipStub(entity.getCourseClass()));
		stub.setCreated(entity.getCreated());
		stub.setInvoiceLine(findRelationshipStub(entity.getInvoiceLine()));
		stub.setModified(entity.getModified());
		stub.setReasonForStudy(entity.getReasonForStudy());
		stub.setSource(entity.getSource().name());
		stub.setStatus(entity.getStatus().name());
		stub.setStudent(findRelationshipStub(entity.getStudent()));
		stub.setWillowId(entity.getId());
		
		return stub;
	}
}
