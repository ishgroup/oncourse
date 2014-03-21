
package ish.oncourse.webservices.v4.stubs.replication;

import org.w3._2001.xmlschema.Adapter2;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for waitingListSiteStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="waitingListSiteStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="waitingListId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="siteId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "waitingListSiteStub", propOrder = {
    "waitingListId",
    "siteId"
})
public class WaitingListSiteStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long waitingListId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long siteId;

    /**
     * Gets the value of the waitingListId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getWaitingListId() {
        return waitingListId;
    }

    /**
     * Sets the value of the waitingListId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaitingListId(Long value) {
        this.waitingListId = value;
    }

    /**
     * Gets the value of the siteId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getSiteId() {
        return siteId;
    }

    /**
     * Sets the value of the siteId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSiteId(Long value) {
        this.siteId = value;
    }

}
