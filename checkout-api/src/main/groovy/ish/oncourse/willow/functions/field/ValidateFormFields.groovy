package ish.oncourse.willow.functions.field

import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.College
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.common.FieldError
import ish.oncourse.willow.model.field.Field
import ish.oncourse.willow.model.field.FieldHeading
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils 

class ValidateFormFields {

    private List<FieldHeading> actual
    private List<FieldHeading> expected
    private String className
    private String formName
    private ObjectContext context
    private College college

    private List<FieldError> fieldErrors = []
    private CommonError commonError
    
    private ValidateFormFields(){}
    
    static ValidateFormFields valueOf(List<FieldHeading> actual, List<FieldHeading> expected, String className, String formName, ObjectContext context, College college) {
        ValidateFormFields validate = new ValidateFormFields()
        validate.className = className
        validate.expected = expected
        validate.actual = actual
        validate.formName = formName
        validate.context = context
        validate.college = college
        validate
    }


    ValidateFormFields validate() {
        List<Field> actualFields = actual.fields.flatten() as List<Field>
        for (Field f : expected.fields.flatten() as List<Field>) { 
            List<Field> correspondingFields = actualFields.findAll { it.key == f.key }
            if (correspondingFields.empty) {
                commonError = new CommonError(message: "$formName form for $className doesn't contain followed field: $f.key")
                return this
            } else if (correspondingFields.size() > 1) {
                commonError = new CommonError(message: "$formName form for $className contains more than one followed fields: $f.key")
                return this
            }
            Field correspondingField = correspondingFields[0]
            String value = StringUtils.trimToNull(correspondingField.value)
            if (!value && f.mandatory) {
                fieldErrors << new FieldError(name: f.key, error: "${f.name} for $className is required")
            } else if (value) {
                FieldError error = new FieldValueValidator(FieldProperty.getByKey(f.key), f.key, context, college).validate(value)
                if (error) {
                    fieldErrors << error
                }
            }
            
        }
        return this
    }
    
    List<FieldError> getFieldErrors() {
        return fieldErrors
    }

    CommonError getCommonError() {
        return commonError
    }
    
}
