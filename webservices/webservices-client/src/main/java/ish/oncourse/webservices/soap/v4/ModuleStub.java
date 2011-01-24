
package ish.oncourse.webservices.soap.v4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for moduleStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="moduleStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://v4.soap.webservices.oncourse.ish/}soapReferenceStub">
 *       &lt;sequence>
 *         &lt;element name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="disciplineCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fieldOfEducation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isModule" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ishVersion" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="modified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="nationalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="trainingPackageId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "moduleStub", propOrder = {
    "created",
    "disciplineCode",
    "fieldOfEducation",
    "isModule",
    "ishVersion",
    "modified",
    "nationalCode",
    "title",
    "trainingPackageId"
})
public class ModuleStub
    extends SoapReferenceStub
{

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar created;
    protected String disciplineCode;
    protected String fieldOfEducation;
    protected Boolean isModule;
    protected Long ishVersion;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modified;
    protected String nationalCode;
    protected String title;
    protected Long trainingPackageId;

    /**
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreated(XMLGregorianCalendar value) {
        this.created = value;
    }

    /**
     * Gets the value of the disciplineCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisciplineCode() {
        return disciplineCode;
    }

    /**
     * Sets the value of the disciplineCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisciplineCode(String value) {
        this.disciplineCode = value;
    }

    /**
     * Gets the value of the fieldOfEducation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldOfEducation() {
        return fieldOfEducation;
    }

    /**
     * Sets the value of the fieldOfEducation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldOfEducation(String value) {
        this.fieldOfEducation = value;
    }

    /**
     * Gets the value of the isModule property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsModule() {
        return isModule;
    }

    /**
     * Sets the value of the isModule property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsModule(Boolean value) {
        this.isModule = value;
    }

    /**
     * Gets the value of the ishVersion property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIshVersion() {
        return ishVersion;
    }

    /**
     * Sets the value of the ishVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIshVersion(Long value) {
        this.ishVersion = value;
    }

    /**
     * Gets the value of the modified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModified() {
        return modified;
    }

    /**
     * Sets the value of the modified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModified(XMLGregorianCalendar value) {
        this.modified = value;
    }

    /**
     * Gets the value of the nationalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationalCode() {
        return nationalCode;
    }

    /**
     * Sets the value of the nationalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationalCode(String value) {
        this.nationalCode = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the trainingPackageId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTrainingPackageId() {
        return trainingPackageId;
    }

    /**
     * Sets the value of the trainingPackageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTrainingPackageId(Long value) {
        this.trainingPackageId = value;
    }

}
