
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActionOnCollection.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActionOnCollection">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Current"/>
 *     &lt;enumeration value="Replace"/>
 *     &lt;enumeration value="Granular"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActionOnCollection")
@XmlEnum
public enum ActionOnCollection {

    @XmlEnumValue("Current")
    CURRENT("Current"),
    @XmlEnumValue("Replace")
    REPLACE("Replace"),
    @XmlEnumValue("Granular")
    GRANULAR("Granular");
    private final String value;

    ActionOnCollection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActionOnCollection fromValue(String v) {
        for (ActionOnCollection c: ActionOnCollection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
