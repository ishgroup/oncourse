
package ish.oncourse.webservices.v4.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for binaryDataStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="binaryDataStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="collegeId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="isDeleted" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="binaryInfo" type="{http://repl.v4.soap.webservices.oncourse.ish/}binaryInfoStub"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "binaryDataStub", propOrder = {
    "collegeId",
    "content",
    "isDeleted",
    "binaryInfo"
})
public class BinaryDataStub
    extends ReplicationStub
{

    protected long collegeId;
    @XmlElement(required = true)
    protected byte[] content;
    protected short isDeleted;
    @XmlElement(required = true)
    protected BinaryInfoStub binaryInfo;

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
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setContent(byte[] value) {
        this.content = ((byte[]) value);
    }

    /**
     * Gets the value of the isDeleted property.
     * 
     */
    public short getIsDeleted() {
        return isDeleted;
    }

    /**
     * Sets the value of the isDeleted property.
     * 
     */
    public void setIsDeleted(short value) {
        this.isDeleted = value;
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

}
