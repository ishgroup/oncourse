
package ish.oncourse.webservices.v4.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


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
 *         &lt;element name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="modified" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
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
    "created",
    "modified",
    "state"
})
@XmlSeeAlso({
    EnrolmentStub.class,
    BinaryInfoStub.class,
    InvoiceStub.class,
    AttendanceStub.class,
    CourseModuleStub.class,
    PaymentInStub.class,
    BinaryDataStub.class,
    ContactStub.class,
    CourseStub.class,
    CourseClassStub.class,
    SessionStub.class,
    TutorStub.class,
    InvoiceLineStub.class,
    BinaryInfoRelationStub.class,
    PaymentInLineStub.class,
    SessionTutorStub.class
})
public abstract class ReplicationStub {

    protected long angelId;
    protected long willowId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date created;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date modified;
    @XmlElement(required = true)
    protected StubState state;

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
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreated(Date value) {
        this.created = value;
    }

    /**
     * Gets the value of the modified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getModified() {
        return modified;
    }

    /**
     * Sets the value of the modified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModified(Date value) {
        this.modified = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link StubState }
     *     
     */
    public StubState getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link StubState }
     *     
     */
    public void setState(StubState value) {
        this.state = value;
    }

}
