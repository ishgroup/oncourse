
package ish.oncourse.webservices.v7.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for discountMembershipStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="discountMembershipStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v7.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="applyToMemberOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="discountId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="membershipProductId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "discountMembershipStub", propOrder = {
    "applyToMemberOnly",
    "discountId",
    "membershipProductId"
})
public class DiscountMembershipStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean applyToMemberOnly;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long discountId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long membershipProductId;

    /**
     * Gets the value of the applyToMemberOnly property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isApplyToMemberOnly() {
        return applyToMemberOnly;
    }

    /**
     * Sets the value of the applyToMemberOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplyToMemberOnly(Boolean value) {
        this.applyToMemberOnly = value;
    }

    /**
     * Gets the value of the discountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getDiscountId() {
        return discountId;
    }

    /**
     * Sets the value of the discountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountId(Long value) {
        this.discountId = value;
    }

    /**
     * Gets the value of the membershipProductId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getMembershipProductId() {
        return membershipProductId;
    }

    /**
     * Sets the value of the membershipProductId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMembershipProductId(Long value) {
        this.membershipProductId = value;
    }

}
