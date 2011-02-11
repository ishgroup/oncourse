
package ish.oncourse.webservices.v4.stubs.replication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for contactStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="businessPhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cookieHash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="countryId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="familyName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="faxNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="givenName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="homePhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="isCompany" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isMale" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isMarketingViaEmailAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isMarketingViaPostAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isMarketingViaSMSAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mobilePhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="modified" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="passwordHash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="suburb" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="taxFileNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="uniqueCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="collegeId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="student" type="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub"/>
 *         &lt;element name="tutor" type="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactStub", propOrder = {
    "rest"
})
public class ContactStub
    extends ReplicationStub
{

    @XmlElementRefs({
        @XmlElementRef(name = "collegeId", type = JAXBElement.class),
        @XmlElementRef(name = "isMarketingViaSMSAllowed", type = JAXBElement.class),
        @XmlElementRef(name = "cookieHash", type = JAXBElement.class),
        @XmlElementRef(name = "faxNumber", type = JAXBElement.class),
        @XmlElementRef(name = "givenName", type = JAXBElement.class),
        @XmlElementRef(name = "mobilePhoneNumber", type = JAXBElement.class),
        @XmlElementRef(name = "street", type = JAXBElement.class),
        @XmlElementRef(name = "isMarketingViaPostAllowed", type = JAXBElement.class),
        @XmlElementRef(name = "password", type = JAXBElement.class),
        @XmlElementRef(name = "isCompany", type = JAXBElement.class),
        @XmlElementRef(name = "tutor", type = JAXBElement.class),
        @XmlElementRef(name = "taxFileNumber", type = JAXBElement.class),
        @XmlElementRef(name = "emailAddress", type = JAXBElement.class),
        @XmlElementRef(name = "state", type = JAXBElement.class),
        @XmlElementRef(name = "isMale", type = JAXBElement.class),
        @XmlElementRef(name = "familyName", type = JAXBElement.class),
        @XmlElementRef(name = "passwordHash", type = JAXBElement.class),
        @XmlElementRef(name = "isMarketingViaEmailAllowed", type = JAXBElement.class),
        @XmlElementRef(name = "student", type = JAXBElement.class),
        @XmlElementRef(name = "suburb", type = JAXBElement.class),
        @XmlElementRef(name = "modified", type = JAXBElement.class),
        @XmlElementRef(name = "businessPhoneNumber", type = JAXBElement.class),
        @XmlElementRef(name = "uniqueCode", type = JAXBElement.class),
        @XmlElementRef(name = "homePhoneNumber", type = JAXBElement.class),
        @XmlElementRef(name = "postcode", type = JAXBElement.class),
        @XmlElementRef(name = "countryId", type = JAXBElement.class),
        @XmlElementRef(name = "dateOfBirth", type = JAXBElement.class)
    })
    protected List<JAXBElement<?>> rest;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "Modified" is used by two different parts of a schema. See: 
     * line 0 of file:/Users/anton/willow/code/webservices/webservices-client/src/main/resources/wsdl/v4_replication.wsdl#types1
     * line 0 of file:/Users/anton/willow/code/webservices/webservices-client/src/main/resources/wsdl/v4_replication.wsdl#types1
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the rest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Long }{@code >}
     * {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * {@link JAXBElement }{@code <}{@link ReplicationStub }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * {@link JAXBElement }{@code <}{@link ReplicationStub }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Date }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Date }{@code >}
     * {@link JAXBElement }{@code <}{@link Long }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getRest() {
        if (rest == null) {
            rest = new ArrayList<JAXBElement<?>>();
        }
        return this.rest;
    }

}
