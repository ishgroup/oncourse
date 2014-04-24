
package ish.oncourse.webservices.v5.stubs.replication;

import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for transactionGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transactionGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transactionKeys" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}attendance"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}binaryData"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}binaryInfo"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}aclAccessKey"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}aclRole"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}binaryInfoRelation"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}certificate"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}certificateOutcome"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}concessionType"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}contact"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}course"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}courseClass"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}courseClassTutor"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}courseModule"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}discount"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}discountConcessionType"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}discountCourseClass"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}discountMembership"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}discountMembershipRelationType"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}contactRelation"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}contactRelationType"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}product"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}membershipProduct"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}voucherProduct"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}productItem"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}membership"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}voucher"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}voucherProductCourse"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}voucherPaymentIn"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}enrolment"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}message"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}messagePerson"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}messageTemplate"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}invoice"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}invoiceLine"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}invoiceLineDiscount"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}outcome"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}paymentIn"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}paymentInLine"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}paymentOut"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}preference"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}room"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}session"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}tutorAttendance"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}site"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}student"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}systemUser"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}survey"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}sessionModule"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}entityRelation"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}corporatePass"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}corporatePassCourseClass"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}queuedStatistic"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}studentConcession"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}waitingList"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}waitingListSite"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}tag"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}tagRequirement"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}tagRelation"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}tutor"/>
 *           &lt;element ref="{http://repl.v5.soap.webservices.oncourse.ish/}deleted"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transactionGroup", propOrder = {
    "transactionKeys",
    "attendanceOrBinaryDataOrBinaryInfo"
})
public class TransactionGroup extends GenericTransactionGroup {

    @XmlElement(required = true)
    protected List<String> transactionKeys;
    @XmlElements({
        @XmlElement(name = "attendance", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = AttendanceStub.class),
        @XmlElement(name = "binaryData", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = BinaryDataStub.class),
        @XmlElement(name = "binaryInfo", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = BinaryInfoStub.class),
        @XmlElement(name = "aclAccessKey", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = AclAccessKeyStub.class),
        @XmlElement(name = "aclRole", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = AclRoleStub.class),
        @XmlElement(name = "binaryInfoRelation", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = BinaryInfoRelationStub.class),
        @XmlElement(name = "certificate", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = CertificateStub.class),
        @XmlElement(name = "certificateOutcome", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = CertificateOutcomeStub.class),
        @XmlElement(name = "concessionType", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = ConcessionTypeStub.class),
        @XmlElement(name = "contact", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = ContactStub.class),
        @XmlElement(name = "course", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = CourseStub.class),
        @XmlElement(name = "courseClass", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = CourseClassStub.class),
        @XmlElement(name = "courseClassTutor", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = CourseClassTutorStub.class),
        @XmlElement(name = "courseModule", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = CourseModuleStub.class),
        @XmlElement(name = "discount", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = DiscountStub.class),
        @XmlElement(name = "discountConcessionType", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = DiscountConcessionTypeStub.class),
        @XmlElement(name = "discountCourseClass", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = DiscountCourseClassStub.class),
        @XmlElement(name = "discountMembership", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = DiscountMembershipStub.class),
        @XmlElement(name = "discountMembershipRelationType", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = DiscountMembershipRelationTypeStub.class),
        @XmlElement(name = "contactRelation", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = ContactRelationStub.class),
        @XmlElement(name = "contactRelationType", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = ContactRelationTypeStub.class),
        @XmlElement(name = "product", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = ProductStub.class),
        @XmlElement(name = "membershipProduct", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = MembershipProductStub.class),
        @XmlElement(name = "voucherProduct", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = VoucherProductStub.class),
        @XmlElement(name = "productItem", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = ProductItemStub.class),
        @XmlElement(name = "membership", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = MembershipStub.class),
        @XmlElement(name = "voucher", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = VoucherStub.class),
        @XmlElement(name = "voucherProductCourse", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = VoucherProductCourseStub.class),
        @XmlElement(name = "voucherPaymentIn", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = VoucherPaymentInStub.class),
        @XmlElement(name = "enrolment", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = EnrolmentStub.class),
        @XmlElement(name = "message", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = MessageStub.class),
        @XmlElement(name = "messagePerson", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = MessagePersonStub.class),
        @XmlElement(name = "messageTemplate", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = MessageTemplateStub.class),
        @XmlElement(name = "invoice", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = InvoiceStub.class),
        @XmlElement(name = "invoiceLine", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = InvoiceLineStub.class),
        @XmlElement(name = "invoiceLineDiscount", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = InvoiceLineDiscountStub.class),
        @XmlElement(name = "outcome", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = OutcomeStub.class),
        @XmlElement(name = "paymentIn", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = PaymentInStub.class),
        @XmlElement(name = "paymentInLine", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = PaymentInLineStub.class),
        @XmlElement(name = "paymentOut", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = PaymentOutStub.class),
        @XmlElement(name = "preference", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = PreferenceStub.class),
        @XmlElement(name = "room", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = RoomStub.class),
        @XmlElement(name = "session", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = SessionStub.class),
        @XmlElement(name = "tutorAttendance", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = TutorAttendanceStub.class),
        @XmlElement(name = "site", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = SiteStub.class),
        @XmlElement(name = "student", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = StudentStub.class),
        @XmlElement(name = "systemUser", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = SystemUserStub.class),
        @XmlElement(name = "survey", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = SurveyStub.class),
        @XmlElement(name = "sessionModule", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = SessionModuleStub.class),
        @XmlElement(name = "entityRelation", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = EntityRelationStub.class),
        @XmlElement(name = "corporatePass", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = CorporatePassStub.class),
        @XmlElement(name = "corporatePassCourseClass", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = CorporatePassCourseClassStub.class),
        @XmlElement(name = "queuedStatistic", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = QueuedStatisticStub.class),
        @XmlElement(name = "studentConcession", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = StudentConcessionStub.class),
        @XmlElement(name = "waitingList", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = WaitingListStub.class),
        @XmlElement(name = "waitingListSite", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = WaitingListSiteStub.class),
        @XmlElement(name = "tag", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = TagStub.class),
        @XmlElement(name = "tagRequirement", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = TagRequirementStub.class),
        @XmlElement(name = "tagRelation", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = TagRelationStub.class),
        @XmlElement(name = "tutor", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = TutorStub.class),
        @XmlElement(name = "deleted", namespace = "http://repl.v5.soap.webservices.oncourse.ish/", type = DeletedStub.class)
    })
    protected List<ReplicationStub> attendanceOrBinaryDataOrBinaryInfo;

    /**
     * Gets the value of the transactionKeys property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionKeys property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTransactionKeys() {
        if (transactionKeys == null) {
            transactionKeys = new ArrayList<>();
        }
        return this.transactionKeys;
    }

    /**
     * Gets the value of the attendanceOrBinaryDataOrBinaryInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attendanceOrBinaryDataOrBinaryInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttendanceOrBinaryDataOrBinaryInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttendanceStub }
     * {@link BinaryDataStub }
     * {@link BinaryInfoStub }
     * {@link AclAccessKeyStub }
     * {@link AclRoleStub }
     * {@link BinaryInfoRelationStub }
     * {@link CertificateStub }
     * {@link CertificateOutcomeStub }
     * {@link ConcessionTypeStub }
     * {@link ContactStub }
     * {@link CourseStub }
     * {@link CourseClassStub }
     * {@link CourseClassTutorStub }
     * {@link CourseModuleStub }
     * {@link DiscountStub }
     * {@link DiscountConcessionTypeStub }
     * {@link DiscountCourseClassStub }
     * {@link DiscountMembershipStub }
     * {@link DiscountMembershipRelationTypeStub }
     * {@link ContactRelationStub }
     * {@link ContactRelationTypeStub }
     * {@link ProductStub }
     * {@link MembershipProductStub }
     * {@link VoucherProductStub }
     * {@link ProductItemStub }
     * {@link MembershipStub }
     * {@link VoucherStub }
     * {@link VoucherProductCourseStub }
     * {@link VoucherPaymentInStub }
     * {@link EnrolmentStub }
     * {@link MessageStub }
     * {@link MessagePersonStub }
     * {@link MessageTemplateStub }
     * {@link InvoiceStub }
     * {@link InvoiceLineStub }
     * {@link InvoiceLineDiscountStub }
     * {@link OutcomeStub }
     * {@link PaymentInStub }
     * {@link PaymentInLineStub }
     * {@link PaymentOutStub }
     * {@link PreferenceStub }
     * {@link RoomStub }
     * {@link SessionStub }
     * {@link TutorAttendanceStub }
     * {@link SiteStub }
     * {@link StudentStub }
     * {@link SystemUserStub }
     * {@link SurveyStub }
     * {@link SessionModuleStub }
     * {@link EntityRelationStub }
     * {@link CorporatePassStub }
     * {@link CorporatePassCourseClassStub }
     * {@link QueuedStatisticStub }
     * {@link StudentConcessionStub }
     * {@link WaitingListStub }
     * {@link WaitingListSiteStub }
     * {@link TagStub }
     * {@link TagRequirementStub }
     * {@link TagRelationStub }
     * {@link TutorStub }
     * {@link DeletedStub }
     * 
     * 
     */
    public List<ReplicationStub> getAttendanceOrBinaryDataOrBinaryInfo() {
        if (attendanceOrBinaryDataOrBinaryInfo == null) {
            attendanceOrBinaryDataOrBinaryInfo = new ArrayList<>();
        }
        return this.attendanceOrBinaryDataOrBinaryInfo;
    }

	@Override
	public List<? extends GenericReplicationStub> getReplicationStub() {
		return getAttendanceOrBinaryDataOrBinaryInfo();
	}
}
