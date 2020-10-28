
package ish.oncourse.webservices.v22.stubs.replication;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ish.oncourse.webservices.util.GenericReplicationResult;


/**
 * <p>Java class for replicationResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicationResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="replicatedRecord" type="{http://repl.v22.soap.webservices.oncourse.ish/}replicatedRecord" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "replicationResult", propOrder = {
    "replicatedRecord"
})
public class ReplicationResult
    extends GenericReplicationResult
{

    @XmlElement(required = true)
    protected List<ReplicatedRecord> replicatedRecord;

    /**
     * Gets the value of the replicatedRecord property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the replicatedRecord property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReplicatedRecord().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReplicatedRecord }
     * 
     * 
     */
    public List<ReplicatedRecord> getReplicatedRecord() {
        if (replicatedRecord == null) {
            replicatedRecord = new ArrayList<ReplicatedRecord>();
        }
        return this.replicatedRecord;
    }

}
