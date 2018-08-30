
package ish.oncourse.webservices.v17.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import ish.oncourse.webservices.util.GenericReplicatedRecord;


/**
 * <p>Java class for replicatedRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicatedRecord"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="status" type="{http://repl.v17.soap.webservices.oncourse.ish/}status"/&gt;
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="stub" type="{http://repl.v17.soap.webservices.oncourse.ish/}hollowStub"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "replicatedRecord", propOrder = {
    "status",
    "message",
    "stub"
})
public class ReplicatedRecord
    extends GenericReplicatedRecord
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Status status;
    @XmlElement(required = true)
    protected String message;
    @XmlElement(required = true)
    protected HollowStub stub;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link Status }
     *     
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link Status }
     *     
     */
    public void setStatus(Status value) {
        this.status = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the stub property.
     * 
     * @return
     *     possible object is
     *     {@link HollowStub }
     *     
     */
    public HollowStub getStub() {
        return stub;
    }

    /**
     * Sets the value of the stub property.
     * 
     * @param value
     *     allowed object is
     *     {@link HollowStub }
     *     
     */
    public void setStub(HollowStub value) {
        this.stub = value;
    }

}
