
package au.gov.usi._2013.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BulkUploadRetrieveResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BulkUploadRetrieveResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestId" type="{http://usi.gov.au/2013/ws}RequestIDType"/>
 *         &lt;element name="NoOfApplications">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;maxInclusive value="500"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NoOfApplicationsFailed">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;maxInclusive value="500"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Applications">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Application" type="{http://usi.gov.au/2013/ws}ApplicationResponseType" maxOccurs="500"/>
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
@XmlType(name = "BulkUploadRetrieveResponseType", propOrder = {
    "requestId",
    "noOfApplications",
    "noOfApplicationsFailed",
    "applications"
})
public class BulkUploadRetrieveResponseType {

    @XmlElement(name = "RequestId", required = true)
    protected String requestId;
    @XmlElement(name = "NoOfApplications")
    protected int noOfApplications;
    @XmlElement(name = "NoOfApplicationsFailed")
    protected int noOfApplicationsFailed;
    @XmlElement(name = "Applications", required = true)
    protected BulkUploadRetrieveResponseType.Applications applications;

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the noOfApplications property.
     * 
     */
    public int getNoOfApplications() {
        return noOfApplications;
    }

    /**
     * Sets the value of the noOfApplications property.
     * 
     */
    public void setNoOfApplications(int value) {
        this.noOfApplications = value;
    }

    /**
     * Gets the value of the noOfApplicationsFailed property.
     * 
     */
    public int getNoOfApplicationsFailed() {
        return noOfApplicationsFailed;
    }

    /**
     * Sets the value of the noOfApplicationsFailed property.
     * 
     */
    public void setNoOfApplicationsFailed(int value) {
        this.noOfApplicationsFailed = value;
    }

    /**
     * Gets the value of the applications property.
     * 
     * @return
     *     possible object is
     *     {@link BulkUploadRetrieveResponseType.Applications }
     *     
     */
    public BulkUploadRetrieveResponseType.Applications getApplications() {
        return applications;
    }

    /**
     * Sets the value of the applications property.
     * 
     * @param value
     *     allowed object is
     *     {@link BulkUploadRetrieveResponseType.Applications }
     *     
     */
    public void setApplications(BulkUploadRetrieveResponseType.Applications value) {
        this.applications = value;
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
     *         &lt;element name="Application" type="{http://usi.gov.au/2013/ws}ApplicationResponseType" maxOccurs="500"/>
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
        "application"
    })
    public static class Applications {

        @XmlElement(name = "Application", required = true)
        protected List<ApplicationResponseType> application;

        /**
         * Gets the value of the application property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the application property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getApplication().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ApplicationResponseType }
         * 
         * 
         */
        public List<ApplicationResponseType> getApplication() {
            if (application == null) {
                application = new ArrayList<ApplicationResponseType>();
            }
            return this.application;
        }

    }

}
