/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { FormControlLabel } from "@material-ui/core";
import React, { useEffect, useMemo, useState } from "react";
import { change, Field as FormField } from "redux-form";
import { CustomFieldType } from "@api/model";
import { connect } from "react-redux";
import { Dispatch } from "redux";
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
import { getCustomFieldTypes } from "../actions";
import { EntityName } from "../../../../model/entities/common";

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

  const getItems = (cfType: CustomFieldType, val: string) => {
    let result = [];

    if (cfType.dataType) {
      try {
        switch (cfType.dataType) {
          case "Map": {
            result = cfType.defaultValue
              ? JSON.parse(cfType.defaultValue).map(v => ({ ...v, label: `${v.label} (${v.value})` }))
              : [];
            break;
          }
          case "List": {
            result = cfType.defaultValue ? JSON.parse(cfType.defaultValue)
              .filter(v => v && !v.value.includes("*"))
              .map(v => (v.label ? v : { ...v, label: v.value })) : [];
          }
        }
      } catch (e) {
        console.error(e);
      }
    }
    if (val && Array.isArray(result) && result.findIndex(item => item.value === val) < 0) {
      result.unshift({ label: val, value: val });
    }

    return result;
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
  getCustomFieldTypes?: (entity: EntityName) => void;
  entityName: EntityName;
  fieldName: string;
  entityValues: any;
  dispatch?: any;
  form?: string;
  fullWidth?: boolean;
}

const CustomFieldsTypes = React.memo<CustomFieldsProps>(
  ({
     getCustomFieldTypes,
     entityName,
     customFieldTypes,
     fieldName,
     entityValues,
     dispatch,
     form,
     fullWidth
  }) => {
    useEffect(() => {
      if (!customFieldTypes || !customFieldTypes[entityName]) {
        getCustomFieldTypes(entityName);
      }
    }, [entityName, customFieldTypes]);

    return (entityValues && entityValues[fieldName] && customFieldTypes && customFieldTypes[entityName]
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
      : null);
  }
);

const mapStateToProps = (state: State) => ({
  customFieldTypes: state.customFieldTypes.types
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  getCustomFieldTypes: entity => dispatch(getCustomFieldTypes(entity))
});

export default connect(mapStateToProps, mapDispatchToProps)(CustomFieldsTypes);
