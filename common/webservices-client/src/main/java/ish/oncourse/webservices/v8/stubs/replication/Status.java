
package ish.oncourse.webservices.v8.stubs.replication;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for status.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="status">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EMPTY_ANGELID"/>
 *     &lt;enumeration value="EMPTY_WILLOWID"/>
 *     &lt;enumeration value="UNIQUES_FAILURE"/>
 *     &lt;enumeration value="WILLOWID_NOT_MATCH"/>
 *     &lt;enumeration value="ANGELID_NOT_MATCH"/>
 *     &lt;enumeration value="SUCCESS"/>
 *     &lt;enumeration value="FAILED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "status")
@XmlEnum
public enum Status {

    EMPTY_ANGELID,
    EMPTY_WILLOWID,
    UNIQUES_FAILURE,
    WILLOWID_NOT_MATCH,
    ANGELID_NOT_MATCH,
    SUCCESS,
    FAILED;

    public String value() {
        return name();
    }

    public static Status fromValue(String v) {
        return valueOf(v);
    }

}
