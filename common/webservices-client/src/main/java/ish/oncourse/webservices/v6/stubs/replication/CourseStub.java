
package ish.oncourse.webservices.v6.stubs.replication;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for courseStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="courseStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="allowWaitingList" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="detail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="detailTextile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fieldOfEducation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sufficientForQualification" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="VETCourse" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="webVisible" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nominalHours" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="searchText" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="qualificationId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "courseStub", propOrder = {
    "allowWaitingList",
    "code",
    "detail",
    "detailTextile",
    "fieldOfEducation",
    "sufficientForQualification",
    "vetCourse",
    "webVisible",
    "name",
    "nominalHours",
    "searchText",
    "qualificationId"
})
public class CourseStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean allowWaitingList;
    @XmlElement(required = true)
    protected String code;
    @XmlElement(required = true)
    protected String detail;
    @XmlElement(required = true)
    protected String detailTextile;
    @XmlElement(required = true)
    protected String fieldOfEducation;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean sufficientForQualification;
    @XmlElement(name = "VETCourse", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean vetCourse;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean webVisible;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected BigDecimal nominalHours;
    @XmlElement(required = true)
    protected String searchText;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long qualificationId;

    /**
     * Gets the value of the allowWaitingList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isAllowWaitingList() {
        return allowWaitingList;
    }

    /**
     * Sets the value of the allowWaitingList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllowWaitingList(Boolean value) {
        this.allowWaitingList = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the detail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Sets the value of the detail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetail(String value) {
        this.detail = value;
    }

    /**
     * Gets the value of the detailTextile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetailTextile() {
        return detailTextile;
    }

    /**
     * Sets the value of the detailTextile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetailTextile(String value) {
        this.detailTextile = value;
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
     * Gets the value of the sufficientForQualification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isSufficientForQualification() {
        return sufficientForQualification;
    }

    /**
     * Sets the value of the sufficientForQualification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSufficientForQualification(Boolean value) {
        this.sufficientForQualification = value;
    }

    /**
     * Gets the value of the vetCourse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isVETCourse() {
        return vetCourse;
    }

    /**
     * Sets the value of the vetCourse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVETCourse(Boolean value) {
        this.vetCourse = value;
    }

    /**
     * Gets the value of the webVisible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isWebVisible() {
        return webVisible;
    }

    /**
     * Sets the value of the webVisible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWebVisible(Boolean value) {
        this.webVisible = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the nominalHours property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNominalHours() {
        return nominalHours;
    }

    /**
     * Sets the value of the nominalHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNominalHours(BigDecimal value) {
        this.nominalHours = value;
    }

    /**
     * Gets the value of the searchText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * Sets the value of the searchText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchText(String value) {
        this.searchText = value;
    }

    /**
     * Gets the value of the qualificationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getQualificationId() {
        return qualificationId;
    }

    /**
     * Sets the value of the qualificationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualificationId(Long value) {
        this.qualificationId = value;
    }

}
