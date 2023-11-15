/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { CustomFieldType } from "@api/model";
import { Grid } from "@mui/material";
import FormControlLabel from "@mui/material/FormControlLabel";
import { GridProps } from "@mui/material/Grid";
import React, { useEffect, useMemo, useState } from "react";
import { connect } from "react-redux";
import { change } from "redux-form";
import FormField from "../../../../common/components/form/formFields/FormField";
import { getDeepValue } from "../../../../common/utils/common";
import {
  validateEmail,
  validatePattern,
  validateSingleMandatoryField,
  validateURL
} from "../../../../common/utils/validation";
import { EntityName } from "../../../../model/entities/common";
import { State } from "../../../../reducers/state";
import { CustomFieldTypesState } from "../reducers/state";

const customFieldComponentResolver = (type: CustomFieldType, onCreateOption) => {
  const validate = type.mandatory ? validateSingleMandatoryField : undefined;
  const validateFieldPattern = val => validatePattern(val, type.pattern);

  let fieldType = "text";
  let componentProps: any = { validate };

  switch (type.dataType) {
    case "Checkbox": {
      fieldType = "checkbox";
      componentProps = {
        stringValue: true
      };
      break;
    }
    case "Date": {
      fieldType = "date";
      componentProps = {
        validate
      };
      break;
    }
    case "Date time": {
      fieldType = "dateTime";
      componentProps = {
        validate
      };
      break;
    }
    case "Email": {
      componentProps = {
        validate: type.mandatory ? [validateSingleMandatoryField, validateEmail] : validateEmail
      };
      break;
    }
    case "Long text": {
      fieldType = "multiline";
      componentProps = {
        validate
      };
      break;
    }
    case "List": {
      const isCreatable = type.defaultValue && type.defaultValue.includes("*");
      fieldType = "select";
      componentProps = {
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
      fieldType = "select";
      componentProps = {
        allowEmpty: !type.mandatory,
        validate
      };
      break;
    }
    case "Money": {
      fieldType = "money";
      componentProps = {
        validate
      };
      break;
    }
    case "URL": {
      componentProps = {
        validate: type.mandatory ? [validateSingleMandatoryField, validateURL] : validateURL
      };
      break;
    }
    case "Text": {
      componentProps = {
        validate
      };
      break;
    }
    case "Pattern text": {
      componentProps = {
        validate: type.mandatory ? [validateSingleMandatoryField, validateFieldPattern] : validateFieldPattern
      };
      break;
    }
  }

  return { fieldType, componentProps };
};

interface CustomFieldProps {
  type: CustomFieldType;
  value?: string;
  dispatch?: any;
  form?: string;
  fieldName?: string;
}

const CustomField: React.FC<CustomFieldProps> = ({
 type,
 value,
 dispatch,
 form,
 fieldName
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

  const { fieldType, componentProps } = useMemo(() => customFieldComponentResolver(type, onCreate), [type]);

  return (
    fieldType === "checkbox"
    ? (
      <FormControlLabel
        control={<FormField type="checkbox" name={`${fieldName}.${type.fieldKey}`} color="primary" {...componentProps} />}
        label={type.name}
      />
      )
      : (
        <FormField
          name={`${fieldName}.${type.fieldKey}`}
          label={type.name}
          type={fieldType}
          items={items}
          {...componentProps}
        />
      )
  );
};

interface CustomFieldsProps {
  customFieldTypes?: CustomFieldTypesState['types'];
  entityName: EntityName;
  fieldName: string;
  entityValues: any;
  dispatch?: any;
  form?: string;
  gridItemProps: GridProps;
}

const CustomFieldsTypes = React.memo<CustomFieldsProps>(
  ({
     gridItemProps,
     entityName,
     customFieldTypes,
     fieldName,
     entityValues,
     dispatch,
     form,
  }) => {
    const value = getDeepValue(entityValues, fieldName);
    
    return (value && customFieldTypes && customFieldTypes[entityName]
      ? customFieldTypes[entityName].map((type, i) => (
        <Grid key={i} item {...gridItemProps} className="pr-2">
          <CustomField
            type={type}
            value={value[type.fieldKey]}
            fieldName={fieldName}
            dispatch={dispatch}
            form={form}
          />
        </Grid>))
      : null);
  }
);

const mapStateToProps = (state: State) => ({
  customFieldTypes: state.customFieldTypes.types
});

export default connect(mapStateToProps)(CustomFieldsTypes);