
package au.gov.usi._2013.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BulkVerifyUSIType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BulkVerifyUSIType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrgCode" type="{http://usi.gov.au/2013/ws}OrgCodeType"/>
 *         &lt;element name="NoOfVerifications">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;maxInclusive value="500"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Verifications">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Verification" type="{http://usi.gov.au/2013/ws}VerificationType" maxOccurs="500"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BulkVerifyUSIType", propOrder = {
    "orgCode",
    "noOfVerifications",
    "verifications"
})
public class BulkVerifyUSIType {

    @XmlElement(name = "OrgCode", required = true)
    protected String orgCode;
    @XmlElement(name = "NoOfVerifications")
    protected int noOfVerifications;
    @XmlElement(name = "Verifications", required = true)
    protected BulkVerifyUSIType.Verifications verifications;

    /**
     * Gets the value of the orgCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * Sets the value of the orgCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgCode(String value) {
        this.orgCode = value;
    }

    /**
     * Gets the value of the noOfVerifications property.
     * 
     */
    public int getNoOfVerifications() {
        return noOfVerifications;
    }

    /**
     * Sets the value of the noOfVerifications property.
     * 
     */
    public void setNoOfVerifications(int value) {
        this.noOfVerifications = value;
    }

    /**
     * Gets the value of the verifications property.
     * 
     * @return
     *     possible object is
     *     {@link BulkVerifyUSIType.Verifications }
     *     
     */
    public BulkVerifyUSIType.Verifications getVerifications() {
        return verifications;
    }

    /**
     * Sets the value of the verifications property.
     * 
     * @param value
     *     allowed object is
     *     {@link BulkVerifyUSIType.Verifications }
     *     
     */
    public void setVerifications(BulkVerifyUSIType.Verifications value) {
        this.verifications = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Verification" type="{http://usi.gov.au/2013/ws}VerificationType" maxOccurs="500"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "verification"
    })
    public static class Verifications {

        @XmlElement(name = "Verification", required = true)
        protected List<VerificationType> verification;

        /**
         * Gets the value of the verification property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the verification property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVerification().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VerificationType }
         * 
         * 
         */
        public List<VerificationType> getVerification() {
            if (verification == null) {
                verification = new ArrayList<VerificationType>();
            }
            return this.verification;
        }

    }

}
