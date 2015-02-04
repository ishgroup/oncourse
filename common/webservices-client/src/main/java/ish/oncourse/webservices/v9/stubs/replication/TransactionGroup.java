
package ish.oncourse.webservices.v9.stubs.replication;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ish.oncourse.webservices.util.GenericTransactionGroup;


/**
 * <p>Java class for transactionGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transactionGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transactionKeys" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://repl.v9.soap.webservices.oncourse.ish/}replicationStub" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transactionGroup", propOrder = {
    "transactionKeys",
    "replicationStub"
})
public class TransactionGroup
    extends GenericTransactionGroup
{

    @XmlElement(required = true)
    protected List<String> transactionKeys;
    @XmlElement(namespace = "http://repl.v9.soap.webservices.oncourse.ish/")
    protected List<ReplicationStub> replicationStub;

    /**
     * Gets the value of the transactionKeys property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionKeys property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTransactionKeys() {
        if (transactionKeys == null) {
            transactionKeys = new ArrayList<String>();
        }
        return this.transactionKeys;
    }

    /**
     * Gets the value of the replicationStub property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the replicationStub property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReplicationStub().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReplicationStub }
     * 
     * 
     */
    public List<ReplicationStub> getReplicationStub() {
        if (replicationStub == null) {
            replicationStub = new ArrayList<ReplicationStub>();
        }
        return this.replicationStub;
    }

}
