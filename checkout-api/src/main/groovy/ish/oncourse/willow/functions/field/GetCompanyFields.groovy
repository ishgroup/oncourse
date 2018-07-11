package ish.oncourse.willow.functions.field

import ish.oncourse.common.field.ContextType
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Field
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.WebSite
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.willow.model.field.ContactFields
import ish.oncourse.willow.model.field.FieldSet
import org.apache.cayenne.ObjectContext

import static ish.oncourse.common.field.FieldProperty.*
import static ish.oncourse.common.field.FieldProperty.getByKey

class GetCompanyFields {
    
    private static final List<FieldProperty> COMPANY_FIELDS = [STREET, SUBURB, POSTCODE, STATE, COUNTRY, BUSINESS_PHONE_NUMBER, FAX_NUMBER,
                                                               ABN, CUSTOM_FIELD_CONTACT,IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY,
                                                               IS_MARKETING_VIA_POST_ALLOWED_PROPERTY, IS_MARKETING_VIA_SMS_ALLOWED_PROPERTY]

    private static final String ABN_PREFERENCE_KEY = "${FieldSet.ENROLMENT}.${ContextType.CONTACT}.${ABN}.required".toLowerCase()

    private static final String VALUE_Show = "Show"
    private static final String VALUE_Required = "Required"
    private static final String VALUE_Hide = "Hide"

    private Contact company
    private College college
    private ObjectContext context


    GetCompanyFields(Contact company, College college, ObjectContext context) {
        this.company = company
        this.college = college
        this.context = context
    }

    List<Field> getFields() {
        FieldConfiguration configuration = new GetDefaultFieldConfiguration(college, context).get()
        List<Field> companyFields = configuration.fields.findAll { COMPANY_FIELDS.contains(getByKey(it.property)) }
        addAbnField(companyFields)

        companyFields
    }

    void addAbnField(List<Field> companyFields) {
        if (company.abn || companyFields.find {it.property == ABN.key}) {
            return
        }
        
        String preference = new GetPreference(college, ABN_PREFERENCE_KEY,context).value
        if (preference) {
            switch (preference) {
                case VALUE_Show:
                    companyFields << createAbnField(false)
                    break
                case VALUE_Required:
                    companyFields << createAbnField(true)
                    break
                case VALUE_Hide:
                    break
                default: 
                    throw new IllegalArgumentException(preference)
            }
        }
    }

    Field createAbnField(boolean mandatory) {
        return new Field().with { f ->
            f.property = ABN.key
            f.name = ABN.displayName
            f.description = "Business number"
            f.mandatory = mandatory
            f.order = Integer.MAX_VALUE
            f
        }
    }
    
}
