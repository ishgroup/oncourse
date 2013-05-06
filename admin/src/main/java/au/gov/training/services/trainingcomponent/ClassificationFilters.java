
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;


/**
 * <p>Java class for ClassificationFilters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClassificationFilters">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ClassificationFilter" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Scheme" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Values" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring"/>
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
@XmlType(name = "ClassificationFilters", propOrder = {
    "classificationFilter"
})
public class ClassificationFilters {

    @XmlElement(name = "ClassificationFilter")
    protected List<ClassificationFilters.ClassificationFilter> classificationFilter;

    /**
     * Gets the value of the classificationFilter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classificationFilter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassificationFilter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassificationFilters.ClassificationFilter }
     * 
     * 
     */
    public List<ClassificationFilters.ClassificationFilter> getClassificationFilter() {
        if (classificationFilter == null) {
            classificationFilter = new ArrayList<ClassificationFilters.ClassificationFilter>();
        }
        return this.classificationFilter;
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
     *         &lt;element name="Scheme" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Values" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring"/>
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
        "scheme",
        "values"
    })
    public static class ClassificationFilter {

        @XmlElement(name = "Scheme", required = true, nillable = true)
        protected String scheme;
        @XmlElement(name = "Values", required = true, nillable = true)
        protected ArrayOfstring values;

        /**
         * Gets the value of the scheme property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getScheme() {
            return scheme;
        }

        /**
         * Sets the value of the scheme property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setScheme(String value) {
            this.scheme = value;
        }

        /**
         * Gets the value of the values property.
         * 
         * @return
         *     possible object is
         *     {@link ArrayOfstring }
         *     
         */
        public ArrayOfstring getValues() {
            return values;
        }

        /**
         * Sets the value of the values property.
         * 
         * @param value
         *     allowed object is
         *     {@link ArrayOfstring }
         *     
         */
        public void setValues(ArrayOfstring value) {
            this.values = value;
        }

    }

}
