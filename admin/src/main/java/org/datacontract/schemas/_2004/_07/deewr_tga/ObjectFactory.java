
package org.datacontract.schemas._2004._07.deewr_tga;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.datacontract.schemas._2004._07.deewr_tga package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ScopeDecisionType_QNAME = new QName("http://schemas.datacontract.org/2004/07/Deewr.Tga.Model", "ScopeDecisionType");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.datacontract.schemas._2004._07.deewr_tga
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ScopeDecisionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/Deewr.Tga.Model", name = "ScopeDecisionType")
    public JAXBElement<ScopeDecisionType> createScopeDecisionType(ScopeDecisionType value) {
        return new JAXBElement<ScopeDecisionType>(_ScopeDecisionType_QNAME, ScopeDecisionType.class, null, value);
    }

}
