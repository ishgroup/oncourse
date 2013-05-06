
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeleteOperation.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DeleteOperation">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PhysicalDelete"/>
 *     &lt;enumeration value="LogicalDelete"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DeleteOperation")
@XmlEnum
public enum DeleteOperation {

    @XmlEnumValue("PhysicalDelete")
    PHYSICAL_DELETE("PhysicalDelete"),
    @XmlEnumValue("LogicalDelete")
    LOGICAL_DELETE("LogicalDelete");
    private final String value;

    DeleteOperation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DeleteOperation fromValue(String v) {
        for (DeleteOperation c: DeleteOperation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
