package ish.oncourse.webservices.services.replication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;
import ish.common.types.AvetmissStudentDisabilityType;
import ish.common.types.AvetmissStudentEnglishProficiency;
import ish.common.types.AvetmissStudentIndigenousStatus;
import ish.common.types.AvetmissStudentPriorEducation;
import ish.common.types.AvetmissStudentSchoolLevel;
import ish.common.types.PaymentSource;
import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.EnrolmentStatus;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedRecordAction;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.builders.replication.IWillowStubBuilder;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassStub;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.StudentStub;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.junit.Test;

public class WillowStubBuilderTest {

	@Test
	public void testOnlyFullStubs() {
		WillowStubBuilderFactory factory = new WillowStubBuilderFactory();
		IWillowQueueService queueService = mock(IWillowQueueService.class);
		
		ObjectContext ctx =  mock(ObjectContext.class);

		List<QueuedRecord> queue = new LinkedList<QueuedRecord>();

		QueuedRecord enrolmentRecord = new QueuedRecord(QueuedRecordAction.CREATE, "Enrolment", 1l);
		QueuedRecord courseClassRecord = new QueuedRecord(QueuedRecordAction.CREATE, "CourseClass", 1l);
		QueuedRecord invoiceLineRecord = new QueuedRecord(QueuedRecordAction.CREATE, "InvoiceLine", 1l);
		QueuedRecord studentRecord = new QueuedRecord(QueuedRecordAction.CREATE, "Student", 1l);

		queue.add(courseClassRecord);
		queue.add(invoiceLineRecord);
		queue.add(studentRecord);

		Enrolment enrlMock = enrolmentMock(ctx);
		CourseClass courseClassStub = courseClassMock(ctx);
		InvoiceLine invoiceLineStub = invoiceLineMock(ctx);
		Student studentStub = studentMock(ctx);
		
		
		when(queueService.findRelatedEntity(eq("Enrolment"), eq(1l))).thenReturn(enrlMock);
		when(queueService.findRelatedEntity(eq("CourseClass"), eq(1l))).thenReturn(courseClassStub);
		when(queueService.findRelatedEntity(eq("InvoiceLine"), eq(1l))).thenReturn(invoiceLineStub);
		when(queueService.findRelatedEntity(eq("Student"), eq(1l))).thenReturn(studentStub);

		factory.setQueueService(queueService);

		Map<QueuedKey, QueuedRecord> mapQueue = mapQueue(queue);
		IWillowStubBuilder builder = factory.newReplicationStubBuilder(mapQueue);
		
		ReplicationStub replStub = builder.convert(enrolmentRecord);
		assertNotNull(replStub);
		assertTrue("Expecting EnrolmentStub class.", replStub instanceof EnrolmentStub);
		
		EnrolmentStub enrlStub = (EnrolmentStub) replStub;
		assertTrue("Expecting StudentStub not HollowStub.", enrlStub.getStudent() instanceof StudentStub);
		assertTrue("Expecting InvoiceLineStub not HollowStub", enrlStub.getInvoiceLine() instanceof InvoiceLineStub);
		assertTrue("Expecting CourseClassStub not HollowStub", enrlStub.getCourseClass() instanceof CourseClassStub);
		
		assertTrue("Expecting empty mappedQueue.", mapQueue.isEmpty());
	}

	private static Enrolment enrolmentMock(ObjectContext ctx) {
		Enrolment enrl = mock(Enrolment.class, RETURNS_DEEP_STUBS);
		
		College college = mock(College.class);
		CourseClass c = courseClassMock(ctx);
		InvoiceLine in = invoiceLineMock(ctx);
		Student st = studentMock(ctx);
		
		when(enrl.getId()).thenReturn(1l);
		when(enrl.getObjectId().getEntityName()).thenReturn("Enrolment");
		when(enrl.getCollege()).thenReturn(college);
		when(enrl.getCourseClass()).thenReturn(c);
		when(enrl.getInvoiceLine()).thenReturn(in);
		when(enrl.getStudent()).thenReturn(st);
		when(enrl.getSource()).thenReturn(PaymentSource.SOURCE_WEB);
		when(enrl.getStatus()).thenReturn(EnrolmentStatus.SUCCESS);

		return enrl;
	}

	private static CourseClass courseClassMock(ObjectContext ctx) {
		CourseClass courseClass = mock(CourseClass.class, RETURNS_DEEP_STUBS);
		when(courseClass.getId()).thenReturn(1l);
		when(courseClass.getObjectId().getEntityName()).thenReturn("CourseClass");
		return courseClass;
	}

	private static InvoiceLine invoiceLineMock(ObjectContext ctx) {
		InvoiceLine invLine = mock(InvoiceLine.class, RETURNS_DEEP_STUBS);
		when(invLine.getId()).thenReturn(1l);
		when(invLine.getObjectId().getEntityName()).thenReturn("InvoiceLine");
		return invLine;
	}

	private static Student studentMock(ObjectContext ctx) {
		Student st = mock(Student.class, RETURNS_DEEP_STUBS);
		when(st.getId()).thenReturn(1l);
		when(st.getObjectId().getEntityName()).thenReturn("Student");
		when(st.getCountryOfBirth().getId()).thenReturn(1l);
		
		when(st.getDisabilityType()).thenReturn(AvetmissStudentDisabilityType.HEARING);
		when(st.getEnglishProficiency()).thenReturn(AvetmissStudentEnglishProficiency.VERY_WELL);
		when(st.getHighestSchoolLevel()).thenReturn(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11);
		when(st.getIndigenousStatus()).thenReturn(AvetmissStudentIndigenousStatus.ABORIGINAL);
		when(st.getLabourForceType()).thenReturn(1);
		when(st.getLanguageHome().getId()).thenReturn(1l);
		when(st.getPriorEducationCode()).thenReturn(AvetmissStudentPriorEducation.CERTIFICATE_III);
		
		return st;
	}

	private static LinkedHashMap<QueuedKey, QueuedRecord> mapQueue(List<QueuedRecord> queue) {
		LinkedHashMap<QueuedKey, QueuedRecord> mappedQueue = new LinkedHashMap<QueuedKey, QueuedRecord>();

		for (QueuedRecord r : queue) {
			mappedQueue.put(new QueuedKey(r.getEntityWillowId(), r.getEntityIdentifier()), r);
		}

		return mappedQueue;
	}
}
