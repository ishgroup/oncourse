
package au.gov.usi._2018.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StateListType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StateListType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NSW"/&gt;
 *     &lt;enumeration value="VIC"/&gt;
 *     &lt;enumeration value="QLD"/&gt;
 *     &lt;enumeration value="SA"/&gt;
 *     &lt;enumeration value="WA"/&gt;
 *     &lt;enumeration value="TAS"/&gt;
 *     &lt;enumeration value="NT"/&gt;
 *     &lt;enumeration value="ACT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
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
