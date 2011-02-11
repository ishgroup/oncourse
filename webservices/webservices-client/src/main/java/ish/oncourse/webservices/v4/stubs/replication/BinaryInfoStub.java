
package ish.oncourse.webservices.v4.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for binaryInfoStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="binaryInfoStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="byteSize" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="isWebVisible" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mimeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pixelHeight" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pixelWidth" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="referenceNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="binaryData" type="{http://repl.v4.soap.webservices.oncourse.ish/}binaryDataStub"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "binaryInfoStub", propOrder = {
    "byteSize",
    "isWebVisible",
    "mimeType",
    "name",
    "pixelHeight",
    "pixelWidth",
    "referenceNumber",
    "binaryData"
})
public class BinaryInfoStub
    extends ReplicationStub
{

    protected long byteSize;
    protected boolean isWebVisible;
    @XmlElement(required = true)
    protected String mimeType;
    @XmlElement(required = true)
    protected String name;
    protected int pixelHeight;
    protected int pixelWidth;
    protected int referenceNumber;
    @XmlElement(required = true)
    protected BinaryDataStub binaryData;

    /**
     * Gets the value of the byteSize property.
     * 
     */
    public long getByteSize() {
        return byteSize;
    }

    /**
     * Sets the value of the byteSize property.
     * 
     */
    public void setByteSize(long value) {
        this.byteSize = value;
    }

    /**
     * Gets the value of the isWebVisible property.
     * 
     */
    public boolean isIsWebVisible() {
        return isWebVisible;
    }

    /**
     * Sets the value of the isWebVisible property.
     * 
     */
    public void setIsWebVisible(boolean value) {
        this.isWebVisible = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the pixelHeight property.
     * 
     */
    public int getPixelHeight() {
        return pixelHeight;
    }

    /**
     * Sets the value of the pixelHeight property.
     * 
     */
    public void setPixelHeight(int value) {
        this.pixelHeight = value;
    }

    /**
     * Gets the value of the pixelWidth property.
     * 
     */
    public int getPixelWidth() {
        return pixelWidth;
    }

    /**
     * Sets the value of the pixelWidth property.
     * 
     */
    public void setPixelWidth(int value) {
        this.pixelWidth = value;
    }

    /**
     * Gets the value of the referenceNumber property.
     * 
     */
    public int getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * Sets the value of the referenceNumber property.
     * 
     */
    public void setReferenceNumber(int value) {
        this.referenceNumber = value;
    }

    /**
     * Gets the value of the binaryData property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryDataStub }
     *     
     */
    public BinaryDataStub getBinaryData() {
        return binaryData;
    }

    /**
     * Sets the value of the binaryData property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryDataStub }
     *     
     */
    public void setBinaryData(BinaryDataStub value) {
        this.binaryData = value;
    }

}
