
package ish.oncourse.webservices.v23.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkoutStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkoutStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v23.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fileUUID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="shoppingCart" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkoutStub", propOrder = {
    "fileUUID",
    "shoppingCart"
})
public class CheckoutStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String fileUUID;
    @XmlElement(required = true)
    protected String shoppingCart;

    /**
     * Gets the value of the fileUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileUUID() {
        return fileUUID;
    }

    /**
     * Sets the value of the fileUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileUUID(String value) {
        this.fileUUID = value;
    }

    /**
     * Gets the value of the shoppingCart property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Sets the value of the shoppingCart property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShoppingCart(String value) {
        this.shoppingCart = value;
    }

}
