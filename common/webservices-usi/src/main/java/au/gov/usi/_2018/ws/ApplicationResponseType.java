
package au.gov.usi._2018.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ApplicationResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ApplicationResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ProcessedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="ApplicationId" type="{http://usi.gov.au/2018/ws}ApplicationIDType"/&gt;
 *         &lt;element name="Result"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="Success"/&gt;
 *               &lt;enumeration value="Failure"/&gt;
 *               &lt;enumeration value="MatchFound"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="IdentityDocumentVerified"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="True"/&gt;
 *               &lt;enumeration value="False"/&gt;
 *               &lt;enumeration value="NotRequired"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="USI" type="{http://usi.gov.au/2018/ws}USIType" minOccurs="0"/&gt;
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
@XmlType(name = "ApplicationResponseType", propOrder = {
    "processedDate",
    "applicationId",
    "result",
    "identityDocumentVerified",
    "usi",
    "errors"
})
public class ApplicationResponseType {

    @XmlElement(name = "ProcessedDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar processedDate;
    @XmlElement(name = "ApplicationId", required = true)
    protected String applicationId;
    @XmlElement(name = "Result", required = true)
    protected String result;
    @XmlElement(name = "IdentityDocumentVerified", required = true)
    protected String identityDocumentVerified;
    @XmlElement(name = "USI")
    protected String usi;
    @XmlElement(name = "Errors")
    protected ApplicationResponseType.Errors errors;

    /**
     * Gets the value of the processedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getProcessedDate() {
        return processedDate;
    }

    /**
     * Sets the value of the processedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setProcessedDate(XMLGregorianCalendar value) {
        this.processedDate = value;
    }

    /**
     * Gets the value of the applicationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Sets the value of the applicationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplicationId(String value) {
        this.applicationId = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResult(String value) {
        this.result = value;
    }

    /**
     * Gets the value of the identityDocumentVerified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentityDocumentVerified() {
        return identityDocumentVerified;
    }

    /**
     * Sets the value of the identityDocumentVerified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentityDocumentVerified(String value) {
        this.identityDocumentVerified = value;
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
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link ApplicationResponseType.Errors }
     *     
     */
    public ApplicationResponseType.Errors getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplicationResponseType.Errors }
     *     
     */
    public void setErrors(ApplicationResponseType.Errors value) {
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
