package ish.oncourse.webservices.replication.v4.builders;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityName;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.Outcome;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.Preference;
import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.model.SystemUser;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.model.WaitingList;

import java.util.HashMap;
import java.util.Map;

public class V4BuildersMap {
	private Map<String, IWillowStubBuilder> builderMap;
	
	/**
	 * @return the builderMap
	 */
	public Map<String, IWillowStubBuilder> getBuilderMap() {
		if (builderMap == null) {
			builderMap = new HashMap<String, IWillowStubBuilder>();
		}
		return builderMap;
	}
	
	public void initMap() {
		getBuilderMap().clear();
		builderMap.put(getEntityName(Attendance.class), new AttendanceStubBuilder());
		builderMap.put(getEntityName(BinaryData.class), new BinaryDataStubBuilder());
		builderMap.put(getEntityName(BinaryInfo.class), new BinaryInfoStubBuilder());
		builderMap.put(getEntityName(ConcessionType.class), new ConcessionTypeStubBuilder());
		builderMap.put(getEntityName(CourseClass.class), new CourseClassStubBuilder());
		builderMap.put(getEntityName(Contact.class), new ContactStubBuilder());
		builderMap.put(getEntityName(Enrolment.class), new EnrolmentStubBuilder());
		builderMap.put(getEntityName(Discount.class), new DiscountStubBuilder());
		builderMap.put(getEntityName(Invoice.class), new InvoiceStubBuilder());
		builderMap.put(getEntityName(InvoiceLine.class), new InvoiceLineStubBuilder());
		builderMap.put(getEntityName(InvoiceLineDiscount.class), new InvoiceLineDiscountStubBuilder());
		builderMap.put(getEntityName(MessagePerson.class), new MessagePersonStubBuilder());
		builderMap.put(getEntityName(PaymentInLine.class), new PaymentInLineStubBuilder());
		builderMap.put(getEntityName(PaymentIn.class), new PaymentInStubBuilder());
		builderMap.put(getEntityName(PaymentOut.class), new PaymentOutStubBuilder());
		builderMap.put(getEntityName(Preference.class), new PreferenceStubBuilder());
		builderMap.put(getEntityName(StudentConcession.class), new StudentConcessionStubBuilder());
		builderMap.put(getEntityName(Student.class), new StudentStubBuilder());
		builderMap.put(getEntityName(SystemUser.class), new SystemUserStubBuilder());
		builderMap.put(getEntityName(Tutor.class), new TutorStubBuilder());
		builderMap.put(getEntityName(TutorRole.class), new TutorRoleStubBuilder());
		builderMap.put(getEntityName(Site.class), new SiteStubBuilder());
		builderMap.put(getEntityName(Course.class), new CourseStubBuilder());
		builderMap.put(getEntityName(Room.class), new RoomStubBuilder());
		builderMap.put(getEntityName(WaitingList.class), new WaitingListStubBuilder());
		builderMap.put(getEntityName(Taggable.class), new TaggableStubBuilder());
		builderMap.put(getEntityName(Outcome.class), new OutcomeStubBuilder());
        builderMap.put(getEntityName(Certificate.class), new CertificateStubBuilder());
        builderMap.put(getEntityName(CertificateOutcome.class), new CertificateOutcomeStubBuilder());
	}
}
