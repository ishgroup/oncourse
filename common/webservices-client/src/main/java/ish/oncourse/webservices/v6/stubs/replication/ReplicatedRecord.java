
package ish.oncourse.webservices.v6.stubs.replication;

import ish.oncourse.webservices.util.GenericReplicatedRecord;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for replicatedRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicatedRecord">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="status" type="{http://repl.v6.soap.webservices.oncourse.ish/}status"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="stub" type="{http://repl.v6.soap.webservices.oncourse.ish/}hollowStub"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
public class ReplicatedRecord extends GenericReplicatedRecord {

    @XmlElement(required = true)
    protected Status status;
    @XmlElement(required = true)
    protected String message;
    @XmlElement(required = true)
    protected HollowStub stub;

	public boolean isSuccessStatus() {
		return Status.SUCCESS.equals(status);
	}

	public boolean isFailedStatus() {
		return Status.FAILED.equals(status);
	}

	public void setSuccessStatus() {
		setStatus(Status.SUCCESS);
	}

	public void setFailedStatus() {
		setStatus(Status.FAILED);
	}

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
