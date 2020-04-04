
package au.gov.usi._2018.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetNonDvsDocumentTypesResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetNonDvsDocumentTypesResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NonDvsDocumentTypes"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="NonDvsDocumentType" type="{http://usi.gov.au/2018/ws}NonDvsDocumentTypeType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "GetNonDvsDocumentTypesResponseType", propOrder = {
    "nonDvsDocumentTypes"
})
public class GetNonDvsDocumentTypesResponseType {

    @XmlElement(name = "NonDvsDocumentTypes", required = true)
    protected GetNonDvsDocumentTypesResponseType.NonDvsDocumentTypes nonDvsDocumentTypes;

    /**
     * Gets the value of the nonDvsDocumentTypes property.
     * 
     * @return
     *     possible object is
     *     {@link GetNonDvsDocumentTypesResponseType.NonDvsDocumentTypes }
     *     
     */
    public GetNonDvsDocumentTypesResponseType.NonDvsDocumentTypes getNonDvsDocumentTypes() {
        return nonDvsDocumentTypes;
    }

    /**
     * Sets the value of the nonDvsDocumentTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetNonDvsDocumentTypesResponseType.NonDvsDocumentTypes }
     *     
     */
    public void setNonDvsDocumentTypes(GetNonDvsDocumentTypesResponseType.NonDvsDocumentTypes value) {
        this.nonDvsDocumentTypes = value;
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
     *         &lt;element name="NonDvsDocumentType" type="{http://usi.gov.au/2018/ws}NonDvsDocumentTypeType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "nonDvsDocumentType"
    })
    public static class NonDvsDocumentTypes {

        @XmlElement(name = "NonDvsDocumentType")
        protected List<NonDvsDocumentTypeType> nonDvsDocumentType;

        /**
         * Gets the value of the nonDvsDocumentType property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the nonDvsDocumentType property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNonDvsDocumentType().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NonDvsDocumentTypeType }
         * 
         * 
         */
        public List<NonDvsDocumentTypeType> getNonDvsDocumentType() {
            if (nonDvsDocumentType == null) {
                nonDvsDocumentType = new ArrayList<NonDvsDocumentTypeType>();
            }
            return this.nonDvsDocumentType;
        }

    }

}
