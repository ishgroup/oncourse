
package ish.oncourse.webservices.v4.stubs.replication;

import ish.oncourse.webservices.util.GenericBinaryDataStub;
import org.w3._2001.xmlschema.Adapter2;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="binaryInfoId" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
    "content",
    "binaryInfoId"
})
public class BinaryDataStub extends ReplicationStub implements GenericBinaryDataStub {

    @XmlElement(required = true)
    protected byte[] content;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long binaryInfoId;

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    @Override
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
     * Gets the value of the binaryInfoId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getBinaryInfoId() {
        return binaryInfoId;
    }

    /**
     * Sets the value of the binaryInfoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinaryInfoId(Long value) {
        this.binaryInfoId = value;
    }

}
