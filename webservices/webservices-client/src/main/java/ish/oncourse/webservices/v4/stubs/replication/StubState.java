
package ish.oncourse.webservices.v4.stubs.replication;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for stubState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="stubState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FULL"/>
 *     &lt;enumeration value="HOLLOW"/>
 *     &lt;enumeration value="DELETED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "stubState")
@XmlEnum
public enum StubState {

    FULL,
    HOLLOW,
    DELETED;

    public String value() {
        return name();
    }

    public static StubState fromValue(String v) {
        return valueOf(v);
    }

}
