
package ish.oncourse.webservices.v23.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ish.oncourse.webservices.util.GenericReplicationStub;


/**
 * <p>Java class for replicationStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicationStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="angelId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="willowId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="entityIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="modified" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "replicationStub", propOrder = {
    "angelId",
    "willowId",
    "entityIdentifier",
    "created",
    "modified"
})
@XmlSeeAlso({
    AclAccessKeyStub.class,
    AclRoleStub.class,
    ApplicationStub.class,
    AttendanceStub.class,
    BinaryDataStub.class,
    BinaryInfoStub.class,
    BinaryInfoRelationStub.class,
    CertificateStub.class,
    CertificateOutcomeStub.class,
    CheckoutStub.class,
    ConcessionTypeStub.class,
    ContactStub.class,
    CourseStub.class,
    CourseClassStub.class,
    CourseClassTutorStub.class,
    CourseModuleStub.class,
    DiscountStub.class,
    DiscountConcessionTypeStub.class,
    DiscountCourseClassStub.class,
    DiscountMembershipStub.class,
    DiscountMembershipRelationTypeStub.class,
    ContactRelationStub.class,
    ContactRelationTypeStub.class,
    ProductStub.class,
    ProductItemStub.class,
    VoucherProductCourseStub.class,
    VoucherPaymentInStub.class,
    EnrolmentStub.class,
    SurveyStub.class,
    SessionModuleStub.class,
    EntityRelationStub.class,
    EntityRelationTypeStub.class,
    CorporatePassStub.class,
    CorporatePassCourseClassStub.class,
    CorporatePassProductStub.class,
    MessageStub.class,
    MessagePersonStub.class,
    MessageTemplateStub.class,
    ModuleStub.class,
    InvoiceStub.class,
    InvoiceLineStub.class,
    InvoiceLineDiscountStub.class,
    OutcomeStub.class,
    PaymentInStub.class,
    PaymentInLineStub.class,
    PaymentOutStub.class,
    PreferenceStub.class,
    RoomStub.class,
    SessionStub.class,
    TutorAttendanceStub.class,
    SiteStub.class,
    StudentStub.class,
    StudentConcessionStub.class,
    SystemUserStub.class,
    QueuedStatisticStub.class,
    TagStub.class,
    TagRelationStub.class,
    TagRequirementStub.class,
    TutorStub.class,
    WaitingListStub.class,
    WaitingListSiteStub.class,
    DocumentStub.class,
    DocumentVersionStub.class,
    CustomFieldTypeStub.class,
    CustomFieldStub.class,
    ScriptStub.class,
    EmailTemplateStub.class,
    CourseClassPaymentPlanLineStub.class,
    InvoiceDueDateStub.class,
    CorporatePassDiscountStub.class,
    PriorLearningStub.class,
    ContactDuplicateStub.class,
    AssessmentStub.class,
    AssessmentClassStub.class,
    AssessmentClassTutorStub.class,
    AssessmentSubmissionStub.class,
    AssessmentClassModuleStub.class,
    FieldStub.class,
    FieldConfigurationStub.class,
    FieldConfigurationLinkStub.class,
    FieldConfigurationSchemeStub.class,
    FieldHeadingStub.class,
    TaxStub.class,
    HollowStub.class,
    DeletedStub.class,
    QualificationStub.class
})
public abstract class ReplicationStub
    extends GenericReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long angelId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long willowId;
    @XmlElement(required = true)
    protected String entityIdentifier;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date created;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date modified;

    /**
     * Gets the value of the angelId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getAngelId() {
        return angelId;
    }

    /**
     * Sets the value of the angelId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAngelId(Long value) {
        this.angelId = value;
    }

    /**
     * Gets the value of the willowId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getWillowId() {
        return willowId;
    }

    /**
     * Sets the value of the willowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWillowId(Long value) {
        this.willowId = value;
    }

    /**
     * Gets the value of the entityIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    /**
     * Sets the value of the entityIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntityIdentifier(String value) {
        this.entityIdentifier = value;
    }

    /**
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreated(Date value) {
        this.created = value;
    }

    /**
     * Gets the value of the modified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getModified() {
        return modified;
    }

    /**
     * Sets the value of the modified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModified(Date value) {
        this.modified = value;
    }

}
