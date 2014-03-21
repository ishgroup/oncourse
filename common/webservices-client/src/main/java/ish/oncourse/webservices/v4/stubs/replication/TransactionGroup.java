
package ish.oncourse.webservices.v4.stubs.replication;

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
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}attendance"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}binaryData"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}binaryInfo"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}binaryInfoRelation"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}certificate"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}certificateOutcome"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}concessionType"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}contact"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}course"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}courseClass"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}courseClassTutor"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}courseModule"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}discount"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}discountConcessionType"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}discountCourseClass"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}discountMembership"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}discountMembershipRelationType"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}contactRelation"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}contactRelationType"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}product"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}membershipProduct"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}voucherProduct"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}productItem"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}membership"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}voucher"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}enrolment"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}message"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}messagePerson"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}messageTemplate"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}invoice"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}invoiceLine"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}invoiceLineDiscount"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}outcome"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}paymentIn"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}paymentInLine"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}paymentOut"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}preference"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}room"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}session"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}sessionTutor"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}tutorAttendance"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}site"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}student"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}studentConcession"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}systemUser"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}queuedStatistic"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}waitingList"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}waitingListSite"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}tag"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}tagRequirement"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}tagRelation"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}tutor"/>
 *           &lt;element ref="{http://repl.v4.soap.webservices.oncourse.ish/}deleted"/>
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
        @XmlElement(name = "certificate", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = CertificateStub.class),
        @XmlElement(name = "systemUser", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = SystemUserStub.class),
        @XmlElement(name = "product", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = ProductStub.class),
        @XmlElement(name = "invoiceLineDiscount", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = InvoiceLineDiscountStub.class),
        @XmlElement(name = "session", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = SessionStub.class),
        @XmlElement(name = "contactRelationType", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = ContactRelationTypeStub.class),
        @XmlElement(name = "paymentOut", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = PaymentOutStub.class),
        @XmlElement(name = "binaryData", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = BinaryDataStub.class),
        @XmlElement(name = "discountMembership", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = DiscountMembershipStub.class),
        @XmlElement(name = "contact", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = ContactStub.class),
        @XmlElement(name = "waitingList", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = WaitingListStub.class),
        @XmlElement(name = "tagRelation", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = TagRelationStub.class),
        @XmlElement(name = "voucherProduct", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = VoucherProductStub.class),
        @XmlElement(name = "courseModule", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = CourseModuleStub.class),
        @XmlElement(name = "site", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = SiteStub.class),
        @XmlElement(name = "messagePerson", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = MessagePersonStub.class),
        @XmlElement(name = "messageTemplate", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = MessageTemplateStub.class),
        @XmlElement(name = "voucher", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = VoucherStub.class),
        @XmlElement(name = "membershipProduct", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = MembershipProductStub.class),
        @XmlElement(name = "attendance", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = AttendanceStub.class),
        @XmlElement(name = "binaryInfo", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = BinaryInfoStub.class),
        @XmlElement(name = "binaryInfoRelation", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = BinaryInfoRelationStub.class),
        @XmlElement(name = "paymentInLine", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = PaymentInLineStub.class),
        @XmlElement(name = "discountMembershipRelationType", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = DiscountMembershipRelationTypeStub.class),
        @XmlElement(name = "concessionType", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = ConcessionTypeStub.class),
        @XmlElement(name = "invoiceLine", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = InvoiceLineStub.class),
        @XmlElement(name = "membership", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = MembershipStub.class),
        @XmlElement(name = "tagRequirement", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = TagRequirementStub.class),
        @XmlElement(name = "tag", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = TagStub.class),
        @XmlElement(name = "paymentIn", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = PaymentInStub.class),
        @XmlElement(name = "productItem", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = ProductItemStub.class),
        @XmlElement(name = "invoice", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = InvoiceStub.class),
        @XmlElement(name = "student", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = StudentStub.class),
        @XmlElement(name = "discount", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = DiscountStub.class),
        @XmlElement(name = "enrolment", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = EnrolmentStub.class),
        @XmlElement(name = "sessionTutor", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = SessionTutorStub.class),
        @XmlElement(name = "message", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = MessageStub.class),
        @XmlElement(name = "queuedStatistic", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = QueuedStatisticStub.class),
        @XmlElement(name = "preference", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = PreferenceStub.class),
        @XmlElement(name = "deleted", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = DeletedStub.class),
        @XmlElement(name = "waitingListSite", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = WaitingListSiteStub.class),
        @XmlElement(name = "outcome", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = OutcomeStub.class),
        @XmlElement(name = "contactRelation", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = ContactRelationStub.class),
        @XmlElement(name = "tutor", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = TutorStub.class),
        @XmlElement(name = "discountConcessionType", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = DiscountConcessionTypeStub.class),
        @XmlElement(name = "studentConcession", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = StudentConcessionStub.class),
        @XmlElement(name = "courseClass", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = CourseClassStub.class),
        @XmlElement(name = "room", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = RoomStub.class),
        @XmlElement(name = "certificateOutcome", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = CertificateOutcomeStub.class),
        @XmlElement(name = "courseClassTutor", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = CourseClassTutorStub.class),
        @XmlElement(name = "course", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = CourseStub.class),
        @XmlElement(name = "discountCourseClass", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = DiscountCourseClassStub.class),
        @XmlElement(name = "tutorAttendance", namespace = "http://repl.v4.soap.webservices.oncourse.ish/", type = TutorAttendanceStub.class)
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
     * {@link CertificateStub }
     * {@link SystemUserStub }
     * {@link ProductStub }
     * {@link InvoiceLineDiscountStub }
     * {@link SessionStub }
     * {@link ContactRelationTypeStub }
     * {@link PaymentOutStub }
     * {@link BinaryDataStub }
     * {@link DiscountMembershipStub }
     * {@link ContactStub }
     * {@link WaitingListStub }
     * {@link TagRelationStub }
     * {@link VoucherProductStub }
     * {@link CourseModuleStub }
     * {@link SiteStub }
     * {@link MessagePersonStub }
     * {@link MessageTemplateStub }
     * {@link VoucherStub }
     * {@link MembershipProductStub }
     * {@link AttendanceStub }
     * {@link BinaryInfoStub }
     * {@link BinaryInfoRelationStub }
     * {@link PaymentInLineStub }
     * {@link DiscountMembershipRelationTypeStub }
     * {@link ConcessionTypeStub }
     * {@link InvoiceLineStub }
     * {@link MembershipStub }
     * {@link TagRequirementStub }
     * {@link TagStub }
     * {@link PaymentInStub }
     * {@link ProductItemStub }
     * {@link InvoiceStub }
     * {@link StudentStub }
     * {@link DiscountStub }
     * {@link EnrolmentStub }
     * {@link SessionTutorStub }
     * {@link MessageStub }
     * {@link QueuedStatisticStub }
     * {@link PreferenceStub }
     * {@link DeletedStub }
     * {@link WaitingListSiteStub }
     * {@link OutcomeStub }
     * {@link ContactRelationStub }
     * {@link TutorStub }
     * {@link DiscountConcessionTypeStub }
     * {@link StudentConcessionStub }
     * {@link CourseClassStub }
     * {@link RoomStub }
     * {@link CertificateOutcomeStub }
     * {@link CourseClassTutorStub }
     * {@link CourseStub }
     * {@link DiscountCourseClassStub }
     * {@link TutorAttendanceStub }
     * 
     * 
     */
    public List<ReplicationStub> getAttendanceOrBinaryDataOrBinaryInfo() {
        if (attendanceOrBinaryDataOrBinaryInfo == null) {
            attendanceOrBinaryDataOrBinaryInfo = new ArrayList<>();
        }
        return this.attendanceOrBinaryDataOrBinaryInfo;
    }

}
