
package au.gov.usi._2013.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StateListType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StateListType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NSW"/>
 *     &lt;enumeration value="VIC"/>
 *     &lt;enumeration value="QLD"/>
 *     &lt;enumeration value="SA"/>
 *     &lt;enumeration value="WA"/>
 *     &lt;enumeration value="TAS"/>
 *     &lt;enumeration value="NT"/>
 *     &lt;enumeration value="ACT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StateListType")
@XmlEnum
public enum StateListType {

    NSW,
    VIC,
    QLD,
    SA,
    WA,
    TAS,
    NT,
    ACT;

    public String value() {
        return name();
    }

    public static StateListType fromValue(String v) {
        return valueOf(v);
    }

}
