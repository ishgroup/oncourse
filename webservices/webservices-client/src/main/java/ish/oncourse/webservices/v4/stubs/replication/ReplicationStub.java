
package ish.oncourse.webservices.v4.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for replicationStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicationStub">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="angelId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="willowId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="state" type="{http://repl.v4.soap.webservices.oncourse.ish/}stubState"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "replicationStub", propOrder = {
    "angelId",
    "willowId",
    "state"
})
@XmlSeeAlso({
    CourseStub.class,
    CourseClassStub.class,
    SessionStub.class,
    BinaryInfoStub.class,
    TutorStub.class,
    AttendanceStub.class,
    CourseModuleStub.class,
    ContactStub.class,
    BinaryDataStub.class,
    SessionTutorStub.class
})
public abstract class ReplicationStub {

    protected long angelId;
    protected long willowId;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String state;

    /**
     * Gets the value of the angelId property.
     * 
     */
    public long getAngelId() {
        return angelId;
    }

    /**
     * Sets the value of the angelId property.
     * 
     */
    public void setAngelId(long value) {
        this.angelId = value;
    }

    /**
     * Gets the value of the willowId property.
     * 
     */
    public long getWillowId() {
        return willowId;
    }

    /**
     * Sets the value of the willowId property.
     * 
     */
    public void setWillowId(long value) {
        this.willowId = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

}
