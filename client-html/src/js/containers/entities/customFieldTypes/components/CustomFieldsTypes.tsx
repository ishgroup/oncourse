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
import { validateEmail, validatePattern, validateURL } from "../../../../common/utils/validation";
import { FormFieldWrapperProps } from "../../../../model/common/Fields";
import { EntityName } from "../../../../model/entities/common";
import { State } from "../../../../reducers/state";
import { CustomFieldTypesState } from "../reducers/state";

const customFieldComponentResolver = (type: CustomFieldType, onCreateOption, items) => {
  const validateFieldPattern = val => validatePattern(val, type.pattern);
  let componentProps: Partial<FormFieldWrapperProps>  = { type: "text", required: type.mandatory };

  switch (type.dataType) {
    case "Portal subdomain":
      componentProps = {
        ...componentProps,
        getCustomSearch: s => s ? `subDomain like “${s}”` : '',
        type: 'remoteDataSelect',
        preloadEmpty: true,
        entity: 'PortalWebsite',
        aqlColumns: 'subDomain',
        selectValueMark: 'subDomain',
        selectLabelMark: 'subDomain'
      };
      break;
    case "Checkbox": {
      componentProps = {
        ...componentProps,
        type: 'checkbox',
        stringValue: true
      };
      break;
    }
    case "Date": {
      componentProps = {
        ...componentProps,
        type: 'date'
      };
      break;
    }
    case "Date time": {
      componentProps = {
        ...componentProps,
        type: 'dateTime'
      };
      break;
    }
    case "Email": {
      componentProps = {
        ...componentProps,
        validate: validateEmail,
      };
      break;
    }
    case "Long text": {
      componentProps = {
        ...componentProps,
        type: 'multilineText'
      };
      break;
    }
    case "List": {
      const isCreatable = type.defaultValue && type.defaultValue.includes("*");
      componentProps = {
        ...componentProps,
        type: 'select',
        items,
        allowEmpty: !type.mandatory,
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
      componentProps = {
        ...componentProps,
        type: 'select',
        items,
        allowEmpty: !type.mandatory,
      };
      break;
    }
    case "Money": {
      componentProps = {
        type: 'money'
      };
      break;
    }
    case "URL": {
      componentProps = {
        ...componentProps,
        validate: validateURL
      };
      break;
    }
    case "Pattern text": {
      componentProps = {
        ...componentProps,
        validate: validateFieldPattern,
      };
      break;
    }
  }

  return componentProps;
};

interface CustomFieldProps {
  type: CustomFieldType;
  value?: string;
  dispatch?: any;
  form?: string;
  fieldName?: string;
}

export const CustomField: React.FC<CustomFieldProps> = ({
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
  }, [type, value]);

  const onCreate = value => {
    setItems(prev => [...prev, { value, label: value }]);
    dispatch(change(form, `${fieldName}[${type.fieldKey}]`, value));
  };

  const componentProps = useMemo(() => customFieldComponentResolver(type, onCreate, items), [type, items]);
  
  return (
    componentProps.type === "checkbox"
    ? (
      <FormControlLabel
        control={<FormField type={componentProps.type} name={`${fieldName}.${type.fieldKey}`} color="primary" {...componentProps} />}
        label={type.name}
      />
      )
      : (
        <FormField
          type={componentProps.type as any}
          name={`${fieldName}.${type.fieldKey}`}
          label={type.name}
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
    
    return <>{value && customFieldTypes && customFieldTypes[entityName]
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
      : null}</>;
  }
);

const mapStateToProps = (state: State) => ({
  customFieldTypes: state.customFieldTypes.types
});

export default connect(mapStateToProps)(CustomFieldsTypes);