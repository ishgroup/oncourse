package ish.oncourse.webservices.replication.updaters;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityName;
import ish.common.types.EntityMapping;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.College;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.ContactRelation;
import ish.oncourse.model.ContactRelationType;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.model.DiscountMembership;
import ish.oncourse.model.DiscountMembershipRelationType;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.Membership;
import ish.oncourse.model.MembershipProduct;
import ish.oncourse.model.Message;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.MessageTemplate;
import ish.oncourse.model.Outcome;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.Preference;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Site;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.model.SystemUser;
import ish.oncourse.model.Tag;
import ish.oncourse.model.TagGroupRequirement;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.model.WaitingList;
import ish.oncourse.model.WaitingListSite;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.exception.UpdaterNotFoundException;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;

public class WillowUpdaterImpl implements IWillowUpdater {
	
	@Inject
	private IWebSiteService webSiteService;
	
	/**
	 * Willow updaters mapping
	 */
	private Map<String, IWillowUpdater> updaterMap = new HashMap<String, IWillowUpdater>();

	public WillowUpdaterImpl(@Inject ITextileConverter textileConverter) {
		updaterMap.put(getEntityName(Attendance.class), new AttendanceUpdater());
		updaterMap.put(getEntityName(BinaryData.class), new BinaryDataUpdater());
		updaterMap.put(getEntityName(BinaryInfo.class), new BinaryInfoUpdater());
		updaterMap.put(getEntityName(BinaryInfoRelation.class), new BinaryInfoRelationUpdater());
		updaterMap.put(getEntityName(Contact.class), new ContactUpdater());
		updaterMap.put(getEntityName(Course.class), new CourseUpdater(textileConverter));
		updaterMap.put(getEntityName(CourseClass.class), new CourseClassUpdater(textileConverter));
		updaterMap.put(getEntityName(CourseModule.class), new CourseModuleUpdater());
		updaterMap.put(getEntityName(ConcessionType.class), new ConcessionTypeUpdater());
		updaterMap.put(getEntityName(Certificate.class), new CertificateUpdater());
		updaterMap.put(getEntityName(CertificateOutcome.class), new CertificateOutcomeUpdater());
		updaterMap.put(getEntityName(Discount.class), new DiscountUpdater());
		updaterMap.put(getEntityName(DiscountConcessionType.class), new DiscountConcessionTypeUpdater());
		updaterMap.put(getEntityName(DiscountCourseClass.class), new DiscountCourseClassUpdater());
		updaterMap.put(getEntityName(Enrolment.class), new EnrolmentUpdater());
		updaterMap.put(getEntityName(Outcome.class), new OutcomeUpdater());
		updaterMap.put(getEntityName(Invoice.class), new InvoiceUpdater());
		updaterMap.put(getEntityName(InvoiceLine.class), new InvoiceLineUpdater());
		updaterMap.put(getEntityName(InvoiceLineDiscount.class), new InvoiceLineDiscountUpdater());
		updaterMap.put(getEntityName(Message.class), new MessageUpdater());
		updaterMap.put(getEntityName(MessagePerson.class), new MessagePersonUpdater());
		updaterMap.put(getEntityName(MessageTemplate.class), new MessageTemplateUpdater());
		updaterMap.put(getEntityName(PaymentIn.class), new PaymentInUpdater());
		updaterMap.put(getEntityName(PaymentInLine.class), new PaymentInLineUpdater());
		updaterMap.put(getEntityName(PaymentOut.class), new PaymentOutUpdater());
		updaterMap.put(getEntityName(Preference.class), new PreferenceUpdater());
		updaterMap.put(getEntityName(Student.class), new StudentUpdater());
		updaterMap.put(getEntityName(SystemUser.class), new SystemUserUpdater());
		updaterMap.put(getEntityName(Tag.class), new TagUpdater(textileConverter));
		updaterMap.put(getEntityName(Taggable.class), new TagRelationUpdater());
		updaterMap.put(getEntityName(TagGroupRequirement.class), new TagGroupRequirementUpdater());
		updaterMap.put(getEntityName(Tutor.class), new TutorUpdater(textileConverter));
		updaterMap.put(getEntityName(WaitingList.class), new WaitingListUpdater());
		updaterMap.put(getEntityName(WaitingListSite.class), new WaitingListSiteUpdater());
		updaterMap.put(getEntityName(Site.class), new SiteUpdater(textileConverter));
		updaterMap.put(getEntityName(Room.class), new RoomUpdater(textileConverter));
		updaterMap.put(getEntityName(StudentConcession.class), new StudentConcessionUpdater());
		updaterMap.put(getEntityName(Session.class), new SessionUpdater());
		updaterMap.put(getEntityName(TutorRole.class), new TutorRoleUpdater());
		updaterMap.put(getEntityName(SessionTutor.class), new SessionTutorUpdater());
		updaterMap.put(getEntityName(ContactRelationType.class), new ContactRelationTypeUpdater());
		updaterMap.put(getEntityName(ContactRelation.class), new ContactRelationUpdater());
		updaterMap.put(getEntityName(DiscountMembership.class), new DiscountMembershipUpdater());
		updaterMap.put(getEntityName(DiscountMembershipRelationType.class), new DiscountMembershipRelationTypeUpdater());
		updaterMap.put(getEntityName(Membership.class), new MembershipUpdater());
		updaterMap.put(getEntityName(MembershipProduct.class), new MembershipProductUpdater());
		updaterMap.put(getEntityName(Product.class), new ProductUpdater());
		updaterMap.put(getEntityName(ProductItem.class), new ProductItemUpdater());
		updaterMap.put(getEntityName(VoucherProduct.class), new VoucherProductUpdater());
		updaterMap.put(getEntityName(Voucher.class), new VoucherUpdater());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ish.oncourse.webservices.replication.updaters.IWillowUpdater#
	 * updateEntityFromStub
	 * (ish.oncourse.webservices.v4.stubs.replication.ReplicationStub,
	 * ish.oncourse.model.Queueable,
	 * ish.oncourse.webservices.replication.updaters.RelationShipCallback)
	 */
	@Override
	public void updateEntityFromStub(ReplicationStub stub, Queueable entity, RelationShipCallback callback) {
		
		String key = EntityMapping.getWillowEntityIdentifer(stub.getEntityIdentifier());
		IWillowUpdater updater = updaterMap.get(key);

		if (updater == null) {
			throw new UpdaterNotFoundException(String.format("Updater not found for entity with key:%s", key), key);
		}

		updater.updateEntityFromStub(stub, entity, callback);
		
		if (entity.getCollege() == null) {
			College currentCollege = webSiteService.getCurrentCollege();
			if (currentCollege != null) {
				entity.setCollege((College) entity.getObjectContext().localObject(currentCollege.getObjectId(), null));
			}	
		}
	}
}
