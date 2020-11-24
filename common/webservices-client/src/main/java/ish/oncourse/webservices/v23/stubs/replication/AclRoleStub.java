
package ish.oncourse.webservices.v23.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for aclRoleStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="aclRoleStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v23.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="roleSystemUserId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aclRoleStub", propOrder = {
    "name",
    "roleSystemUserId"
})
public class AclRoleStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long roleSystemUserId;

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
     * Gets the value of the roleSystemUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getRoleSystemUserId() {
        return roleSystemUserId;
    }

    /**
     * Sets the value of the roleSystemUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoleSystemUserId(Long value) {
        this.roleSystemUserId = value;
    }

}
