
package ish.oncourse.webservices.v17.stubs.replication;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for status.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="status"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="EMPTY_ANGELID"/&gt;
 *     &lt;enumeration value="EMPTY_WILLOWID"/&gt;
 *     &lt;enumeration value="UNIQUES_FAILURE"/&gt;
 *     &lt;enumeration value="WILLOWID_NOT_MATCH"/&gt;
 *     &lt;enumeration value="ANGELID_NOT_MATCH"/&gt;
 *     &lt;enumeration value="SUCCESS"/&gt;
 *     &lt;enumeration value="FAILED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
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
