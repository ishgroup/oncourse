package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Preference;
import ish.oncourse.model.QueueKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.webservices.builders.replication.BinaryDataStubBuilder;
import ish.oncourse.webservices.builders.replication.BinaryInfoStubBuilder;
import ish.oncourse.webservices.builders.replication.ConcessionTypeStubBuilder;
import ish.oncourse.webservices.builders.replication.ContactStubBuilder;
import ish.oncourse.webservices.builders.replication.CourseClassStubBuilder;
import ish.oncourse.webservices.builders.replication.EnrolmentStubBuilder;
import ish.oncourse.webservices.builders.replication.IWillowStubBuilder;
import ish.oncourse.webservices.builders.replication.InvoiceLineStubBuilder;
import ish.oncourse.webservices.builders.replication.InvoiceStubBuilder;
import ish.oncourse.webservices.builders.replication.PaymentInLineStubBuilder;
import ish.oncourse.webservices.builders.replication.PaymentInStubBuilder;
import ish.oncourse.webservices.builders.replication.PreferenceStubBuilder;
import ish.oncourse.webservices.builders.replication.StudentConcessionStubBuilder;
import ish.oncourse.webservices.builders.replication.StudentStubBuilder;
import ish.oncourse.webservices.exception.BuilderNotFoundException;
import ish.oncourse.webservices.services.ICollegeRequestService;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

public class WillowStubBuilderFactory {

	private class ReplicationStubBuilderImpl implements IWillowStubBuilder {

		private Map<String, IWillowStubBuilder> builderMap = new HashMap<String, IWillowStubBuilder>();

		public ReplicationStubBuilderImpl() {
			builderMap.put(getClassName(BinaryData.class), new BinaryDataStubBuilder());
			builderMap.put(getClassName(BinaryInfo.class), new BinaryInfoStubBuilder());
			builderMap.put(getClassName(ConcessionType.class), new ConcessionTypeStubBuilder());
			builderMap.put(getClassName(CourseClass.class), new CourseClassStubBuilder());
			builderMap.put(getClassName(Contact.class), new ContactStubBuilder());
			builderMap.put(getClassName(Enrolment.class), new EnrolmentStubBuilder());
			builderMap.put(getClassName(Invoice.class), new InvoiceStubBuilder());
			builderMap.put(getClassName(InvoiceLine.class), new InvoiceLineStubBuilder());
			builderMap.put(getClassName(PaymentInLine.class), new PaymentInLineStubBuilder());
			builderMap.put(getClassName(PaymentIn.class), new PaymentInStubBuilder());
			builderMap.put(getClassName(Preference.class), new PreferenceStubBuilder());
			builderMap.put(getClassName(StudentConcession.class), new StudentConcessionStubBuilder());
			builderMap.put(getClassName(Student.class), new StudentStubBuilder());
		}

		public ReplicationStub convert(QueuedRecord entity) {
			String key = entity.getEntityIdentifier();
			IWillowStubBuilder builder = builderMap.get(key);

			if (builder == null) {
				throw new BuilderNotFoundException("Builder not found during record conversion", key);
			}

			return builder.convert(entity);
		}
	}

	public IWillowStubBuilder newReplicationStubBuilder() {
		return new ReplicationStubBuilderImpl();
	}

	private static String getClassName(Class<?> clazz) {
		int index = clazz.getName().lastIndexOf(".") + 1;
		return clazz.getName().substring(index);
	}
}
