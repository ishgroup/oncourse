
package ish.oncourse.webservices.v23.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for voucherProductCourseStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voucherProductCourseStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v23.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="courseId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="voucherProductId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voucherProductCourseStub", propOrder = {
    "courseId",
    "voucherProductId"
})
public class VoucherProductCourseStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long courseId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long voucherProductId;

    /**
     * Gets the value of the courseId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * Sets the value of the courseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCourseId(Long value) {
        this.courseId = value;
    }

    /**
     * Gets the value of the voucherProductId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getVoucherProductId() {
        return voucherProductId;
    }

    /**
     * Sets the value of the voucherProductId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoucherProductId(Long value) {
        this.voucherProductId = value;
    }

}
