/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FormControlLabel } from "@material-ui/core";
import React, { useEffect, useMemo, useState } from "react";
import { change, Field as FormField } from "redux-form";
import { CustomFieldType } from "@api/model";
import { connect } from "react-redux";
import { CheckboxField } from "../../../../common/components/form/form-fields/CheckboxField";
import EditInPlaceDateTimeField from "../../../../common/components/form/form-fields/EditInPlaceDateTimeField";
import EditInPlaceField from "../../../../common/components/form/form-fields/EditInPlaceField";
import EditInPlaceMoneyField from "../../../../common/components/form/form-fields/EditInPlaceMoneyField";
import {
  validateEmail,
  validateSingleMandatoryField,
  validateURL,
  validatePattern
} from "../../../../common/utils/validation";
import { State } from "../../../../reducers/state";
import EditInPlaceSearchSelect from "../../../../common/components/form/form-fields/EditInPlaceSearchSelect";

const Field: any = FormField;

const customFieldComponentResolver = (type: CustomFieldType, onCreateOption) => {
  const validate = type.mandatory ? validateSingleMandatoryField : undefined;
  const validateFieldPattern = val => validatePattern(val, type.pattern);

  let component = EditInPlaceField;
  let componentProps: any = { validate };

  switch (type.dataType) {
    case "Checkbox": {
      component = props => (
        <FormControlLabel
          className="checkbox mb-2"
          control={<CheckboxField {...props} color="primary" />}
          label={type.name}
        />
      );
      componentProps = {
        stringValue: true
      };
      break;
    }
    case "Date": {
      component = EditInPlaceDateTimeField;
      componentProps = {
        type: "date",
        validate
      };
      break;
    }
    case "Date time": {
      component = EditInPlaceDateTimeField;
      componentProps = {
        type: "datetime",
        validate
      };
      break;
    }
    case "Email": {
      component = EditInPlaceField;
      componentProps = {
        validate: type.mandatory ? [validateSingleMandatoryField, validateEmail] : validateEmail
      };
      break;
    }
    case "Long text": {
      component = EditInPlaceField;
      componentProps = {
        multiline: true,
        validate
      };
      break;
    }
    case "List": {
      const isCreatable = type.defaultValue && type.defaultValue.includes("*");
      component = isCreatable ? EditInPlaceSearchSelect : EditInPlaceField;
      componentProps = {
        select: true,
        allowEmpty: !type.mandatory,
        validate,
        ...(isCreatable
          ? {
            creatable: true,
            onCreateOption,
            createLabel: "Other"
          }
          : {})
      };
      break;
    }
    case "Map": {
      component = EditInPlaceField;
      componentProps = {
        select: true,
        allowEmpty: !type.mandatory,
        validate
      };
      break;
    }
    case "Money": {
      component = EditInPlaceMoneyField;
      componentProps = {
        validate
      };
      break;
    }
    case "URL": {
      component = EditInPlaceField;
      componentProps = {
        validate: type.mandatory ? [validateSingleMandatoryField, validateURL] : validateURL
      };
      break;
    }
    case "Text": {
      component = EditInPlaceField;
      componentProps = {
        validate
      };
      break;
    }
    case "Pattern text": {
      component = EditInPlaceField;
      componentProps = {
        validate: type.mandatory ? [validateSingleMandatoryField, validateFieldPattern] : validateFieldPattern
      };
      break;
    }
  }

  return { component, componentProps };
};

interface CustomFieldProps {
  type: CustomFieldType;
  value?: string;
  dispatch?: any;
  form?: string;
  fieldName?: string;
  fullWidth?: boolean;
}

const CustomField: React.FC<CustomFieldProps> = ({
 type,
 value,
 dispatch,
 form,
 fieldName,
 fullWidth
}) => {
  const [items, setItems] = useState([]);

  const getItems = (type: CustomFieldType, value: string) => {
    let items = [];

    if (type.dataType) {
      switch (type.dataType) {
        case "Map": {
          items = type.defaultValue
            ? JSON.parse(type.defaultValue).map(v => ({ ...v, label: `${v.label} (${v.value})` }))
            : [];
          break;
        }
        case "List": {
          items = type.defaultValue ? JSON.parse(type.defaultValue)
            .filter(v => v && !v.value.includes("*"))
            .map(v => (v.label ? v : { ...v, label: v.value })) : [];
        }
      }
    }
    if (value && Array.isArray(items) && items.findIndex(item => item.value === value) < 0) {
      items.unshift({ label: value, value });
    }

    return items;
  };

  useEffect(() => {
    setItems(getItems(type, value));
    return () => {
      setItems([]);
    };
  }, [type]);

  const onCreate = value => {
    setItems(prev => [...prev, { value, label: value }]);
    dispatch(change(form, `${fieldName}[${type.fieldKey}]`, value));
  };

  const { component, componentProps } = useMemo(() => customFieldComponentResolver(type, onCreate), [type]);
    return (
      <Field
        name={`${fieldName}.${type.fieldKey}`}
        label={type.name}
        component={component}
        items={items}
        {...componentProps}
        fullWidth={fullWidth}
      />
    );
};

interface CustomFieldsProps {
  customFieldTypes?: { key: string; value: CustomFieldType[] };
  entityName: string;
  fieldName: string;
  entityValues: any;
  dispatch?: any;
  form?: string;
  fullWidth?: boolean;
}

const CustomFieldsTypes = React.memo<CustomFieldsProps>(
  ({
     entityName,
     customFieldTypes,
     fieldName,
     entityValues,
     dispatch,
     form,
     fullWidth
  }) => (entityValues && entityValues[fieldName] && customFieldTypes && customFieldTypes[entityName]
    ? customFieldTypes[entityName].map((type, i) => (
      <CustomField
        key={i}
        type={type}
        value={entityValues[fieldName][type.fieldKey]}
        fieldName={fieldName}
        dispatch={dispatch}
        form={form}
        fullWidth={fullWidth}
      />
    ))
    : null)
);

const mapStateToProps = (state: State) => ({
  customFieldTypes: state.customFieldTypes.types
});

export default connect<any, any, any>(mapStateToProps)(CustomFieldsTypes);
