
package ish.oncourse.webservices.v6.stubs.replication;

import ish.oncourse.webservices.util.GenericReplicationStub;
import org.w3._2001.xmlschema.Adapter1;
import org.w3._2001.xmlschema.Adapter2;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;


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
    EnrolmentStub.class,
    TagStub.class,
    InvoiceStub.class,
    DiscountConcessionTypeStub.class,
    SystemUserStub.class,
    MessageTemplateStub.class,
    PaymentOutStub.class,
    CourseStub.class,
    CertificateStub.class,
    CourseClassStub.class,
    InvoiceLineDiscountStub.class,
    StudentStub.class,
    TagRelationStub.class,
    SurveyStub.class,
    VoucherPaymentInStub.class,
    MessagePersonStub.class,
    AttendanceStub.class,
    CorporatePassCourseClassStub.class,
    DeletedStub.class,
    DiscountCourseClassStub.class,
    BinaryDataStub.class,
    DiscountStub.class,
    DiscountMembershipStub.class,
    SessionStub.class,
    DiscountMembershipRelationTypeStub.class,
    PaymentInLineStub.class,
    RoomStub.class,
    VoucherProductCourseStub.class,
    ContactRelationTypeStub.class,
    CourseClassTutorStub.class,
    AclRoleStub.class,
    CorporatePassStub.class,
    CourseModuleStub.class,
    SessionModuleStub.class,
    PaymentInStub.class,
    ContactStub.class,
    WaitingListStub.class,
    ContactRelationStub.class,
    TutorStub.class,
    QueuedStatisticStub.class,
    BinaryInfoRelationStub.class,
    TagRequirementStub.class,
    SiteStub.class,
    AclAccessKeyStub.class,
    CertificateOutcomeStub.class,
    ConcessionTypeStub.class,
    TutorAttendanceStub.class,
    BinaryInfoStub.class,
    OutcomeStub.class,
    EntityRelationStub.class,
    MessageStub.class,
    ProductStub.class,
    StudentConcessionStub.class,
    HollowStub.class,
    PreferenceStub.class,
    WaitingListSiteStub.class,
    InvoiceLineStub.class,
    ProductItemStub.class
})
public abstract class ReplicationStub extends GenericReplicationStub {

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
