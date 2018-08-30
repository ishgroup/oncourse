
package ish.oncourse.webservices.v17.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for corporatePassCourseClassStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="corporatePassCourseClassStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v17.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="corporatePassId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="courseClassId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "corporatePassCourseClassStub", propOrder = {
    "corporatePassId",
    "courseClassId"
})
public class CorporatePassCourseClassStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long corporatePassId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long courseClassId;

    /**
     * Gets the value of the corporatePassId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCorporatePassId() {
        return corporatePassId;
    }

    /**
     * Sets the value of the corporatePassId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorporatePassId(Long value) {
        this.corporatePassId = value;
    }

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

}
