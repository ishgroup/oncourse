
package ish.oncourse.webservices.v17.stubs.replication;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for errorCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="errorCode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="INVALID_SESSION"/&gt;
 *     &lt;enumeration value="INVALID_SECURITY_CODE"/&gt;
 *     &lt;enumeration value="EMPTY_COMMUNICATION_KEY"/&gt;
 *     &lt;enumeration value="HALT_COMMUNICATION_KEY"/&gt;
 *     &lt;enumeration value="INVALID_COMMUNICATION_KEY"/&gt;
 *     &lt;enumeration value="NO_KEYS"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "errorCode")
@XmlEnum
public enum ErrorCode {

    INVALID_SESSION,
    INVALID_SECURITY_CODE,
    EMPTY_COMMUNICATION_KEY,
    HALT_COMMUNICATION_KEY,
    INVALID_COMMUNICATION_KEY,
    NO_KEYS;

    public String value() {
        return name();
    }

    public static ErrorCode fromValue(String v) {
        return valueOf(v);
    }

}
