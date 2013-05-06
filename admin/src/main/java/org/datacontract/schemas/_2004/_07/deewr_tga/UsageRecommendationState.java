
package org.datacontract.schemas._2004._07.deewr_tga;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UsageRecommendationState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UsageRecommendationState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="Current"/>
 *     &lt;enumeration value="Superseded"/>
 *     &lt;enumeration value="Deleted"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UsageRecommendationState", namespace = "http://schemas.datacontract.org/2004/07/Deewr.Tga.Model")
@XmlEnum
public enum UsageRecommendationState {

    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("Current")
    CURRENT("Current"),
    @XmlEnumValue("Superseded")
    SUPERSEDED("Superseded"),
    @XmlEnumValue("Deleted")
    DELETED("Deleted");
    private final String value;

    UsageRecommendationState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UsageRecommendationState fromValue(String v) {
        for (UsageRecommendationState c: UsageRecommendationState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
