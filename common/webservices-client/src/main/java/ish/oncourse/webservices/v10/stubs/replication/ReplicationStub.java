
package ish.oncourse.webservices.v10.stubs.replication;

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
 * &lt;complexType name="replicationStub">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="angelId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="willowId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="entityIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="modified" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
    MessagePersonStub.class,
    DiscountStub.class,
    DiscountMembershipRelationTypeStub.class,
    DiscountMembershipStub.class,
    PaymentInStub.class,
    EmailTemplateStub.class,
    PreferenceStub.class,
    HollowStub.class,
    CourseClassTutorStub.class,
    ScriptStub.class,
    EnrolmentStub.class,
    BinaryDataStub.class,
    ContactRelationTypeStub.class,
    CustomFieldTypeStub.class,
    WaitingListSiteStub.class,
    DocumentStub.class,
    PaymentOutStub.class,
    TutorAttendanceStub.class,
    DiscountCourseClassStub.class,
    ConcessionTypeStub.class,
    CourseStub.class,
    InvoiceLineStub.class,
    SessionModuleStub.class,
    OutcomeStub.class,
    CourseClassStub.class,
    AclRoleStub.class,
    VoucherPaymentInStub.class,
    SiteStub.class,
    DeletedStub.class,
    QueuedStatisticStub.class,
    VoucherProductCourseStub.class,
    EntityRelationStub.class,
    TagRelationStub.class,
    MessageTemplateStub.class,
    CertificateOutcomeStub.class,
    PaymentInLineStub.class,
    DocumentVersionStub.class,
    ContactRelationStub.class,
    ContactStub.class,
    CorporatePassStub.class,
    TagStub.class,
    DiscountConcessionTypeStub.class,
    WaitingListStub.class,
    CustomFieldStub.class,
    TutorStub.class,
    CorporatePassCourseClassStub.class,
    SurveyStub.class,
    CourseClassPaymentPlanLineStub.class,
    StudentStub.class,
    SessionStub.class,
    CertificateStub.class,
    SystemUserStub.class,
    ProductItemStub.class,
    BinaryInfoStub.class,
    CorporatePassProductStub.class,
    StudentConcessionStub.class,
    BinaryInfoRelationStub.class,
    AclAccessKeyStub.class,
    MessageStub.class,
    RoomStub.class,
    InvoiceDueDateStub.class,
    ProductStub.class,
    InvoiceLineDiscountStub.class,
    ApplicationStub.class,
    CourseModuleStub.class,
    TagRequirementStub.class,
    InvoiceStub.class,
    AttendanceStub.class
})
public abstract class ReplicationStub
    extends GenericReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long angelId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long willowId;
    @XmlElement(required = true)
    protected String entityIdentifier;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date created;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
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
