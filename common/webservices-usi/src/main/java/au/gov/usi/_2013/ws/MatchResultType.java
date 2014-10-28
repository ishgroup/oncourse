
package au.gov.usi._2013.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MatchResultType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MatchResultType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Match"/>
 *     &lt;enumeration value="NoMatch"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MatchResultType")
@XmlEnum
public enum MatchResultType {

    @XmlEnumValue("Match")
    MATCH("Match"),
    @XmlEnumValue("NoMatch")
    NO_MATCH("NoMatch");
    private final String value;

    MatchResultType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MatchResultType fromValue(String v) {
        for (MatchResultType c: MatchResultType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
