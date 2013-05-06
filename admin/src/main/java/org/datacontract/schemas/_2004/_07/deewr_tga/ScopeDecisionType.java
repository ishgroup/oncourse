
package org.datacontract.schemas._2004._07.deewr_tga;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ScopeDecisionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ScopeDecisionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Granted"/>
 *     &lt;enumeration value="Refused"/>
 *     &lt;enumeration value="Suspended"/>
 *     &lt;enumeration value="Cancelled"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ScopeDecisionType", namespace = "http://schemas.datacontract.org/2004/07/Deewr.Tga.Model")
@XmlEnum
public enum ScopeDecisionType {

    @XmlEnumValue("Granted")
    GRANTED("Granted"),
    @XmlEnumValue("Refused")
    REFUSED("Refused"),
    @XmlEnumValue("Suspended")
    SUSPENDED("Suspended"),
    @XmlEnumValue("Cancelled")
    CANCELLED("Cancelled");
    private final String value;

    ScopeDecisionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScopeDecisionType fromValue(String v) {
        for (ScopeDecisionType c: ScopeDecisionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
