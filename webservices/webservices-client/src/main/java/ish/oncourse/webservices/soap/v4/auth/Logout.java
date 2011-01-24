
package ish.oncourse.webservices.soap.v4.auth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for logout complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="logout">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="communicationKey" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "logout", propOrder = {
    "communicationKey"
})
public class Logout {

    protected Long communicationKey;

    /**
     * Gets the value of the communicationKey property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCommunicationKey() {
        return communicationKey;
    }

    /**
     * Sets the value of the communicationKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCommunicationKey(Long value) {
        this.communicationKey = value;
    }

}
