
package ish.oncourse.webservices.v4.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for binaryInfoRelationStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="binaryInfoRelationStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="entityAngelId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="entityWillowId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="binaryInfo" type="{http://repl.v4.soap.webservices.oncourse.ish/}binaryInfoStub"/>
 *         &lt;element name="collegeId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "binaryInfoRelationStub", propOrder = {
    "entityAngelId",
    "entityWillowId",
    "binaryInfo",
    "collegeId"
})
public class BinaryInfoRelationStub
    extends ReplicationStub
{

    protected long entityAngelId;
    protected long entityWillowId;
    @XmlElement(required = true)
    protected BinaryInfoStub binaryInfo;
    protected long collegeId;

    /**
     * Gets the value of the entityAngelId property.
     * 
     */
    public long getEntityAngelId() {
        return entityAngelId;
    }

    /**
     * Sets the value of the entityAngelId property.
     * 
     */
    public void setEntityAngelId(long value) {
        this.entityAngelId = value;
    }

    /**
     * Gets the value of the entityWillowId property.
     * 
     */
    public long getEntityWillowId() {
        return entityWillowId;
    }

    /**
     * Sets the value of the entityWillowId property.
     * 
     */
    public void setEntityWillowId(long value) {
        this.entityWillowId = value;
    }

    /**
     * Gets the value of the binaryInfo property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryInfoStub }
     *     
     */
    public BinaryInfoStub getBinaryInfo() {
        return binaryInfo;
    }

    /**
     * Sets the value of the binaryInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryInfoStub }
     *     
     */
    public void setBinaryInfo(BinaryInfoStub value) {
        this.binaryInfo = value;
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

}
