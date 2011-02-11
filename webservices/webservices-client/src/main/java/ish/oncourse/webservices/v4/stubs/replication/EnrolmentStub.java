
package ish.oncourse.webservices.v4.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enrolmentStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="enrolmentStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="reasonForStudy" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="collegeId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="courseClass" type="{http://repl.v4.soap.webservices.oncourse.ish/}courseClassStub"/>
 *         &lt;element name="invoiceLine" type="{http://repl.v4.soap.webservices.oncourse.ish/}invoiceLineStub"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "enrolmentStub", propOrder = {
    "reasonForStudy",
    "source",
    "status",
    "collegeId",
    "courseClass",
    "invoiceLine"
})
public class EnrolmentStub
    extends ReplicationStub
{

    protected int reasonForStudy;
    @XmlElement(required = true)
    protected String source;
    @XmlElement(required = true)
    protected String status;
    protected long collegeId;
    @XmlElement(required = true)
    protected CourseClassStub courseClass;
    @XmlElement(required = true)
    protected InvoiceLineStub invoiceLine;

    /**
     * Gets the value of the reasonForStudy property.
     * 
     */
    public int getReasonForStudy() {
        return reasonForStudy;
    }

    /**
     * Sets the value of the reasonForStudy property.
     * 
     */
    public void setReasonForStudy(int value) {
        this.reasonForStudy = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the collegeId property.
     * 
     */
    public long getCollegeId() {
        return collegeId;
    }

    /**
     * Sets the value of the collegeId property.
     * 
     */
    public void setCollegeId(long value) {
        this.collegeId = value;
    }

    /**
     * Gets the value of the courseClass property.
     * 
     * @return
     *     possible object is
     *     {@link CourseClassStub }
     *     
     */
    public CourseClassStub getCourseClass() {
        return courseClass;
    }

    /**
     * Sets the value of the courseClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link CourseClassStub }
     *     
     */
    public void setCourseClass(CourseClassStub value) {
        this.courseClass = value;
    }

    /**
     * Gets the value of the invoiceLine property.
     * 
     * @return
     *     possible object is
     *     {@link InvoiceLineStub }
     *     
     */
    public InvoiceLineStub getInvoiceLine() {
        return invoiceLine;
    }

    /**
     * Sets the value of the invoiceLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvoiceLineStub }
     *     
     */
    public void setInvoiceLine(InvoiceLineStub value) {
        this.invoiceLine = value;
    }

}
