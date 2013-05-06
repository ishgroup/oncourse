
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActionOnEntity.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActionOnEntity">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Update"/>
 *     &lt;enumeration value="Delete"/>
 *     &lt;enumeration value="Add"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActionOnEntity")
@XmlEnum
public enum ActionOnEntity {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Update")
    UPDATE("Update"),
    @XmlEnumValue("Delete")
    DELETE("Delete"),
    @XmlEnumValue("Add")
    ADD("Add");
    private final String value;

    ActionOnEntity(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActionOnEntity fromValue(String v) {
        for (ActionOnEntity c: ActionOnEntity.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
