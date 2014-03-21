
package ish.oncourse.webservices.v5.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for messageStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messageStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v5.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="emailBody" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="emailSubject" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smsText" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messageStub", propOrder = {
    "emailBody",
    "emailSubject",
    "smsText"
})
public class MessageStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String emailBody;
    @XmlElement(required = true)
    protected String emailSubject;
    @XmlElement(required = true)
    protected String smsText;

    /**
     * Gets the value of the emailBody property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailBody() {
        return emailBody;
    }

    /**
     * Sets the value of the emailBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailBody(String value) {
        this.emailBody = value;
    }

    /**
     * Gets the value of the emailSubject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailSubject() {
        return emailSubject;
    }

    /**
     * Sets the value of the emailSubject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailSubject(String value) {
        this.emailSubject = value;
    }

    /**
     * Gets the value of the smsText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmsText() {
        return smsText;
    }

    /**
     * Sets the value of the smsText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmsText(String value) {
        this.smsText = value;
    }

}
