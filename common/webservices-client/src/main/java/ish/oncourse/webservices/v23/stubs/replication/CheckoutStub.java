
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
 *         &lt;element name="cartUUID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
    "cartUUID",
    "shoppingCart"
})
public class CheckoutStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String cartUUID;
    @XmlElement(required = true)
    protected String shoppingCart;

    /**
     * Gets the value of the cartUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCartUUID() {
        return cartUUID;
    }

    /**
     * Sets the value of the cartUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCartUUID(String value) {
        this.cartUUID = value;
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
