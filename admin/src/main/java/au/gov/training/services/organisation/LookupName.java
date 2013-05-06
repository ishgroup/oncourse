
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LookupName.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LookupName">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Country"/>
 *     &lt;enumeration value="ContactType"/>
 *     &lt;enumeration value="RegistrationEndReason"/>
 *     &lt;enumeration value="ScopeExtent"/>
 *     &lt;enumeration value="TrainingComponentReleaseCurrency"/>
 *     &lt;enumeration value="TrainingComponentUsageRecommendation"/>
 *     &lt;enumeration value="TrainingComponentType"/>
 *     &lt;enumeration value="ClassificationPurpose"/>
 *     &lt;enumeration value="ScopeDecision"/>
 *     &lt;enumeration value="RestrictionTypeCode"/>
 *     &lt;enumeration value="TrainingComponentEndReason"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LookupName")
@XmlEnum
public enum LookupName {

    @XmlEnumValue("Country")
    COUNTRY("Country"),
    @XmlEnumValue("ContactType")
    CONTACT_TYPE("ContactType"),
    @XmlEnumValue("RegistrationEndReason")
    REGISTRATION_END_REASON("RegistrationEndReason"),
    @XmlEnumValue("ScopeExtent")
    SCOPE_EXTENT("ScopeExtent"),
    @XmlEnumValue("TrainingComponentReleaseCurrency")
    TRAINING_COMPONENT_RELEASE_CURRENCY("TrainingComponentReleaseCurrency"),
    @XmlEnumValue("TrainingComponentUsageRecommendation")
    TRAINING_COMPONENT_USAGE_RECOMMENDATION("TrainingComponentUsageRecommendation"),
    @XmlEnumValue("TrainingComponentType")
    TRAINING_COMPONENT_TYPE("TrainingComponentType"),
    @XmlEnumValue("ClassificationPurpose")
    CLASSIFICATION_PURPOSE("ClassificationPurpose"),
    @XmlEnumValue("ScopeDecision")
    SCOPE_DECISION("ScopeDecision"),
    @XmlEnumValue("RestrictionTypeCode")
    RESTRICTION_TYPE_CODE("RestrictionTypeCode"),
    @XmlEnumValue("TrainingComponentEndReason")
    TRAINING_COMPONENT_END_REASON("TrainingComponentEndReason");
    private final String value;

    LookupName(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LookupName fromValue(String v) {
        for (LookupName c: LookupName.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
