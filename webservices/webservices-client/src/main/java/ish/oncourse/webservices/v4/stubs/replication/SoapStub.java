
package ish.oncourse.webservices.v4.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for soapStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="soapStub">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="angelId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="willowId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "soapStub", propOrder = {
    "angelId",
    "willowId"
})
@XmlSeeAlso({
    CourseStub.class,
    CourseClassStub.class,
    SessionStub.class,
    CourseModuleStub.class,
    SessionTutorStub.class
})
public abstract class SoapStub {

    @XmlElement(namespace = "")
    protected Long angelId;
    @XmlElement(namespace = "")
    protected Long willowId;

    /**
     * Gets the value of the angelId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAngelId() {
        return angelId;
    }

    /**
     * Sets the value of the angelId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAngelId(Long value) {
        this.angelId = value;
    }

    /**
     * Gets the value of the willowId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getWillowId() {
        return willowId;
    }

    /**
     * Sets the value of the willowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setWillowId(Long value) {
        this.willowId = value;
    }

}
