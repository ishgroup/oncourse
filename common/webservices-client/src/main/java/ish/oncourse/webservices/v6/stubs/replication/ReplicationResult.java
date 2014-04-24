
package ish.oncourse.webservices.v6.stubs.replication;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for replicationResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replicationResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="replicatedRecord" type="{http://repl.v6.soap.webservices.oncourse.ish/}replicatedRecord" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "replicationResult", propOrder = {
    "replicatedRecord"
})
public class ReplicationResult {

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
