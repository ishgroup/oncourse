
package ish.oncourse.webservices.v6.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ish.oncourse.webservices.util.adapters.StringToBooleanAdapter;
import ish.oncourse.webservices.util.adapters.StringToLongAdapter;


/**
 * <p>Java class for aclAccessKeyStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="aclAccessKeyStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="includesChildren" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="keycode" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="mask" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="roleId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aclAccessKeyStub", propOrder = {
    "includesChildren",
    "keycode",
    "mask",
    "roleId"
})
public class AclAccessKeyStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToBooleanAdapter.class)
    @XmlSchemaType(name = "boolean")
    protected Boolean includesChildren;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToLongAdapter.class)
    @XmlSchemaType(name = "long")
    protected Long keycode;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToBooleanAdapter.class)
    @XmlSchemaType(name = "boolean")
    protected Boolean mask;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToLongAdapter.class)
    @XmlSchemaType(name = "long")
    protected Long roleId;

    /**
     * Gets the value of the includesChildren property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isIncludesChildren() {
        return includesChildren;
    }

    /**
     * Sets the value of the includesChildren property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludesChildren(Boolean value) {
        this.includesChildren = value;
    }

    /**
     * Gets the value of the keycode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getKeycode() {
        return keycode;
    }

    /**
     * Sets the value of the keycode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeycode(Long value) {
        this.keycode = value;
    }

    /**
     * Gets the value of the mask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isMask() {
        return mask;
    }

    /**
     * Sets the value of the mask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMask(Boolean value) {
        this.mask = value;
    }

    /**
     * Gets the value of the roleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Sets the value of the roleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoleId(Long value) {
        this.roleId = value;
    }

}
