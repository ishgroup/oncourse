
package ish.oncourse.webservices.v9.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for courseClassTutorStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="courseClassTutorStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v9.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="courseClassId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="tutorId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="confirmedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="inPublicity" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "courseClassTutorStub", propOrder = {
    "courseClassId",
    "tutorId",
    "confirmedOn",
    "inPublicity"
})
public class CourseClassTutorStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long courseClassId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long tutorId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date confirmedOn;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean inPublicity;

    /**
     * Gets the value of the courseClassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCourseClassId() {
        return courseClassId;
    }

    /**
     * Sets the value of the courseClassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCourseClassId(Long value) {
        this.courseClassId = value;
    }

    /**
     * Gets the value of the tutorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getTutorId() {
        return tutorId;
    }

    /**
     * Sets the value of the tutorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTutorId(Long value) {
        this.tutorId = value;
    }

    /**
     * Gets the value of the confirmedOn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getConfirmedOn() {
        return confirmedOn;
    }

    /**
     * Sets the value of the confirmedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfirmedOn(Date value) {
        this.confirmedOn = value;
    }

    /**
     * Gets the value of the inPublicity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isInPublicity() {
        return inPublicity;
    }

    /**
     * Sets the value of the inPublicity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInPublicity(Boolean value) {
        this.inPublicity = value;
    }

}
