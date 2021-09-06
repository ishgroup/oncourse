
package au.gov.usi._2020.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocateResultType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LocateResultType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Exact"/&gt;
 *     &lt;enumeration value="MultipleExact"/&gt;
 *     &lt;enumeration value="SingleNoContactMatch"/&gt;
 *     &lt;enumeration value="MultipleNoContactMatch"/&gt;
 *     &lt;enumeration value="NoMatch"/&gt;
 *     &lt;enumeration value="NoIdenticalStrongPartial"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "LocateResultType")
@XmlEnum
public enum LocateResultType {

    @XmlEnumValue("Exact")
    EXACT("Exact"),
    @XmlEnumValue("MultipleExact")
    MULTIPLE_EXACT("MultipleExact"),
    @XmlEnumValue("SingleNoContactMatch")
    SINGLE_NO_CONTACT_MATCH("SingleNoContactMatch"),
    @XmlEnumValue("MultipleNoContactMatch")
    MULTIPLE_NO_CONTACT_MATCH("MultipleNoContactMatch"),
    @XmlEnumValue("NoMatch")
    NO_MATCH("NoMatch"),
    @XmlEnumValue("NoIdenticalStrongPartial")
    NO_IDENTICAL_STRONG_PARTIAL("NoIdenticalStrongPartial");
    private final String value;

    LocateResultType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LocateResultType fromValue(String v) {
        for (LocateResultType c: LocateResultType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
