
package au.gov.usi._2013.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BulkVerifyUSIResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BulkVerifyUSIResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VerificationResponses">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="VerificationResponse" type="{http://usi.gov.au/2013/ws}VerificationResponseType" maxOccurs="500"/>
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
@XmlType(name = "BulkVerifyUSIResponseType", propOrder = {
    "verificationResponses"
})
public class BulkVerifyUSIResponseType {

    @XmlElement(name = "VerificationResponses", required = true)
    protected BulkVerifyUSIResponseType.VerificationResponses verificationResponses;

    /**
     * Gets the value of the verificationResponses property.
     * 
     * @return
     *     possible object is
     *     {@link BulkVerifyUSIResponseType.VerificationResponses }
     *     
     */
    public BulkVerifyUSIResponseType.VerificationResponses getVerificationResponses() {
        return verificationResponses;
    }

    /**
     * Sets the value of the verificationResponses property.
     * 
     * @param value
     *     allowed object is
     *     {@link BulkVerifyUSIResponseType.VerificationResponses }
     *     
     */
    public void setVerificationResponses(BulkVerifyUSIResponseType.VerificationResponses value) {
        this.verificationResponses = value;
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
     *         &lt;element name="VerificationResponse" type="{http://usi.gov.au/2013/ws}VerificationResponseType" maxOccurs="500"/>
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
        "verificationResponse"
    })
    public static class VerificationResponses {

        @XmlElement(name = "VerificationResponse", required = true)
        protected List<VerificationResponseType> verificationResponse;

        /**
         * Gets the value of the verificationResponse property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the verificationResponse property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVerificationResponse().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VerificationResponseType }
         * 
         * 
         */
        public List<VerificationResponseType> getVerificationResponse() {
            if (verificationResponse == null) {
                verificationResponse = new ArrayList<VerificationResponseType>();
            }
            return this.verificationResponse;
        }

    }

}
