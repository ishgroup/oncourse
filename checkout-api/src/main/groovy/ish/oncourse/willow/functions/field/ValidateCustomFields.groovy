package ish.oncourse.willow.functions.field

import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.common.FieldError
import ish.oncourse.willow.model.field.DataType
import ish.oncourse.willow.model.field.Field
import ish.oncourse.willow.model.field.FieldHeading

class ValidateCustomFields {

    private List<FieldHeading> actual
    private List<FieldHeading> expected
    private String className
    private String formName


    private List<FieldError> fieldErrors = []
    private CommonError commonError
    
    private ValidateCustomFields(){}
    
    static ValidateCustomFields valueOf(List<FieldHeading> actual, List<FieldHeading> expected, String className, String formName) {
        ValidateCustomFields validate = new ValidateCustomFields()
        validate.className = className
        validate.expected = expected
        validate.actual = actual
        validate.formName = formName
        validate
    }


    ValidateCustomFields validate() {
        List<Field> actualFields = actual.fields.flatten() as List<Field>
        for (Field f : expected.fields.flatten() as List<Field>) { 
            List<Field> correspondingFields = actualFields.findAll { it.key == f.key }
            if (correspondingFields.empty) {
                commonError = new CommonError(message: "$formName form for $className class doesn't contain followed field: $f.key")
                return this
            } else if (correspondingFields.size() > 1) {
                commonError = new CommonError(message: "$formName form for $className class contains more than on followed fields: $f.key")
                return this
            }
            Field correspondingField = correspondingFields[0]
            String value = correspondingField.value
            if (value && f.dataType == DataType.ENUM && !f.enumItems.collect { it.value }.contains(value)) {
                fieldErrors << new FieldError(name: f.key, error: "Please select ${f.name} value for $className class from the drop-down list")
            } else if (f.mandatory) {
                fieldErrors << new FieldError(name: f.key, error: "${f.name} for class $className is required")
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
