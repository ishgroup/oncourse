
package au.gov.usi._2018.ws;

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
 * &lt;complexType name="BulkVerifyUSIResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="VerificationResponses"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="VerificationResponse" type="{http://usi.gov.au/2018/ws}VerificationResponseType" maxOccurs="500"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="VerificationResponse" type="{http://usi.gov.au/2018/ws}VerificationResponseType" maxOccurs="500"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
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
