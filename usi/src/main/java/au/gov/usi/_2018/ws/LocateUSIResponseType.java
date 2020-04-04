
package au.gov.usi._2018.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocateUSIResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LocateUSIResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Result" type="{http://usi.gov.au/2018/ws}LocateResultType"/&gt;
 *         &lt;element name="USI" type="{http://usi.gov.au/2018/ws}USIType"/&gt;
 *         &lt;element name="ContactDetailsMessage" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Errors" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Error" type="{http://usi.gov.au/2018/ws}ErrorType" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "LocateUSIResponseType", propOrder = {
    "result",
    "usi",
    "contactDetailsMessage",
    "errors"
})
public class LocateUSIResponseType {

    @XmlElement(name = "Result", required = true)
    @XmlSchemaType(name = "string")
    protected LocateResultType result;
    @XmlElement(name = "USI", required = true)
    protected String usi;
    @XmlElement(name = "ContactDetailsMessage", required = true)
    protected String contactDetailsMessage;
    @XmlElement(name = "Errors")
    protected LocateUSIResponseType.Errors errors;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link LocateResultType }
     *     
     */
    public LocateResultType getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocateResultType }
     *     
     */
    public void setResult(LocateResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the usi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSI() {
        return usi;
    }

    /**
     * Sets the value of the usi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSI(String value) {
        this.usi = value;
    }

    /**
     * Gets the value of the contactDetailsMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactDetailsMessage() {
        return contactDetailsMessage;
    }

    /**
     * Sets the value of the contactDetailsMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactDetailsMessage(String value) {
        this.contactDetailsMessage = value;
    }

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link LocateUSIResponseType.Errors }
     *     
     */
    public LocateUSIResponseType.Errors getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocateUSIResponseType.Errors }
     *     
     */
    public void setErrors(LocateUSIResponseType.Errors value) {
        this.errors = value;
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
     *         &lt;element name="Error" type="{http://usi.gov.au/2018/ws}ErrorType" maxOccurs="unbounded"/&gt;
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
        "error"
    })
    public static class Errors {

        @XmlElement(name = "Error", required = true)
        protected List<ErrorType> error;

        /**
         * Gets the value of the error property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the error property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getError().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ErrorType }
         * 
         * 
         */
        public List<ErrorType> getError() {
            if (error == null) {
                error = new ArrayList<ErrorType>();
            }
            return this.error;
        }

    }

}
