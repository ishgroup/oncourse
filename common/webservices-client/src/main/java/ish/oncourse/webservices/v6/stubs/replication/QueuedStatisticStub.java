
package ish.oncourse.webservices.v6.stubs.replication;

import ish.oncourse.webservices.util.GenericQueuedStatisticStub;
import org.w3._2001.xmlschema.Adapter1;
import org.w3._2001.xmlschema.Adapter2;
import org.w3._2001.xmlschema.Adapter4;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;


/**
 * <p>Java class for queuedStatisticStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="queuedStatisticStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="stackedTransactionsCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="stackedCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="stackedEntityIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="receivedTimestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="cleanupStub" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "queuedStatisticStub", propOrder = {
    "stackedTransactionsCount",
    "stackedCount",
    "stackedEntityIdentifier",
    "receivedTimestamp",
    "cleanupStub"
})
public class QueuedStatisticStub extends ReplicationStub implements GenericQueuedStatisticStub {

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long stackedTransactionsCount;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long stackedCount;
    @XmlElement(required = true)
    protected String stackedEntityIdentifier;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date receivedTimestamp;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean cleanupStub;

    /**
     * Gets the value of the stackedTransactionsCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getStackedTransactionsCount() {
        return stackedTransactionsCount;
    }

    /**
     * Sets the value of the stackedTransactionsCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStackedTransactionsCount(Long value) {
        this.stackedTransactionsCount = value;
    }

    /**
     * Gets the value of the stackedCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getStackedCount() {
        return stackedCount;
    }

    /**
     * Sets the value of the stackedCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStackedCount(Long value) {
        this.stackedCount = value;
    }

    /**
     * Gets the value of the stackedEntityIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStackedEntityIdentifier() {
        return stackedEntityIdentifier;
    }

    /**
     * Sets the value of the stackedEntityIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStackedEntityIdentifier(String value) {
        this.stackedEntityIdentifier = value;
    }

    /**
     * Gets the value of the receivedTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getReceivedTimestamp() {
        return receivedTimestamp;
    }

    /**
     * Sets the value of the receivedTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceivedTimestamp(Date value) {
        this.receivedTimestamp = value;
    }

    /**
     * Gets the value of the cleanupStub property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isCleanupStub() {
        return cleanupStub;
    }

    /**
     * Sets the value of the cleanupStub property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCleanupStub(Boolean value) {
        this.cleanupStub = value;
    }

	/**
	 * @see ish.oncourse.webservices.v6.stubs.replication.ReplicationStub#getAngelId()
	 */
	@Override
	public Long getAngelId() {
		return 1L;
	}

	/**
	 * @see ish.oncourse.webservices.v6.stubs.replication.ReplicationStub#getEntityIdentifier()
	 */
	@Override
	public String getEntityIdentifier() {
		return QUEUED_STATISTIC_ENTITY;
	}

}
