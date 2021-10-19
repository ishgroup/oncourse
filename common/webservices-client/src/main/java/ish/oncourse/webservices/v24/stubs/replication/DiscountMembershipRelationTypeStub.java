
package ish.oncourse.webservices.v24.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for discountMembershipRelationTypeStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="discountMembershipRelationTypeStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v24.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="contactRelationTypeId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="membershipDiscountId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "discountMembershipRelationTypeStub", propOrder = {
    "contactRelationTypeId",
    "membershipDiscountId"
})
public class DiscountMembershipRelationTypeStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long contactRelationTypeId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long membershipDiscountId;

    /**
     * Gets the value of the contactRelationTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getContactRelationTypeId() {
        return contactRelationTypeId;
    }

    /**
     * Sets the value of the contactRelationTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactRelationTypeId(Long value) {
        this.contactRelationTypeId = value;
    }

    /**
     * Gets the value of the membershipDiscountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getMembershipDiscountId() {
        return membershipDiscountId;
    }

    /**
     * Sets the value of the membershipDiscountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMembershipDiscountId(Long value) {
        this.membershipDiscountId = value;
    }

}
