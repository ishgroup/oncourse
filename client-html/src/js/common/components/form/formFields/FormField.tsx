/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, {
  useCallback, useEffect, useMemo, useState
} from "react";
import { BaseFieldProps, Field } from "redux-form";
import debounce from "lodash.debounce";
import { validateSingleMandatoryField } from "../../../utils/validation";
import SimpleTagList from "../simpleTagListComponent/SimpleTagList";
import { CheckboxField } from "./CheckboxField";
import CodeEditorField from "./CodeEditorField";
import EditInPlaceDateTimeField from "./EditInPlaceDateTimeField";
import EditInPlaceDurationField from "./EditInPlaceDurationField";
import EditInPlaceField from "./EditInPlaceField";
import EditInPlaceFileField from "./EditInPlaceFileField";
import EditInPlaceMoneyField from "./EditInPlaceMoneyField";
import EditInPlaceQuerySelect from "./EditInPlaceQuerySelect";
import EditInPlaceRemoteDataSearchSelect from "./EditInPlaceRemoteDataSearchSelect";
import EditInPlaceSearchSelect from "./EditInPlaceSearchSelect";
import { FormSwitch } from "./Switch";
import { validateTagsList } from "../simpleTagListComponent/validateTagsList";
import EditInPlacePhoneField from "./EditInPlacePhoneField";
import { FormFieldProps } from "../../../../model/common/Fields";
import { stubFunction } from "../../../utils/common";

const stubFieldMocks = { input: { onChange: stubFunction, onBlur: stubFunction }, format: null, debounced: null };

const FormFieldBase = (props: FormFieldProps) => {

  const { type, ...rest } = props;

  const { input, format, debounced = true } = type !== "stub"
    ? props 
    : stubFieldMocks;

  const entity = type === "remoteDataSelect" ? props.entity : null;

  const [value, setValue] = useState(input?.value);

  const debounceChange = useCallback(debounce(input?.onChange, 600), [input?.onChange]);

  const debounceBlur = useCallback(debounce(input?.onBlur, 600), [input?.onBlur]);

  const inputProxy = useMemo(() => ({
    ...input || {},
    value,
    onChange: e => {
      const val = e?.target ? e.target.value : e;
      setValue(format ? format(val) : val);
      debounceChange(e);
    },
    onBlur: e => {
      const val = e?.target ? e.target.value : e;
      setValue(format ? format(val) : val);
      debounceBlur(e);
    },
  }), [value, input]);

  useEffect(() => {
    if (input?.value !== value) {
      setValue(format ? format(input?.value) : input?.value);
    }
  }, [input?.value]);

  const sharedProps = {
    ...rest,
    ...debounced ? { input: inputProxy } : {}
  };

  switch (type) {
    case "phone":
      return <EditInPlacePhoneField  {...sharedProps} />;
    case "duration":
      return <EditInPlaceDurationField  {...sharedProps} />;
    case "file":
      return <EditInPlaceFileField  {...sharedProps} />;
    case "money":
      return <EditInPlaceMoneyField  {...sharedProps} />;
    case "select":
      return <EditInPlaceSearchSelect  {...sharedProps} />;
    case "remoteDataSelect":
      return <EditInPlaceRemoteDataSearchSelect  entity={entity} {...sharedProps} />;
    case "number":
      return <EditInPlaceField  {...sharedProps} type="number" />;
    case "date":
      return <EditInPlaceDateTimeField  {...sharedProps} type="date" />;
    case "time":
      return <EditInPlaceDateTimeField  {...sharedProps} type="time" />;
    case "dateTime":
      return <EditInPlaceDateTimeField  {...sharedProps} type="datetime" />;
    case "aql":
      return <EditInPlaceQuerySelect  {...sharedProps as any} />;
    case "code":
      return <CodeEditorField {...sharedProps} />;
    case "password":
      return <EditInPlaceField  {...sharedProps} type="password" />;
    case "switch":
      return <FormSwitch {...sharedProps} />;
    case "checkbox":
      return <CheckboxField {...sharedProps} />;
    case "multilineText":
      return <EditInPlaceField  {...sharedProps} multiline />;
    case "stub":
      return <div className="invisible" />;
    case "tags":
      return <SimpleTagList  {...sharedProps} />;
    case "text":
    default:
      return <EditInPlaceField  {...sharedProps} />;
  }
};

type BaseProps = FormFieldProps & BaseFieldProps<FormFieldProps>;

const FormField = React.forwardRef<any, BaseProps>((props, ref) => {
  const { name,
    validate,
    type,
    ...rest
  } = props;

  const tags = type === "tags" ? props.tags : [];
  const required = type !== "stub" ? props.required : false;

  const validateResolver = useMemo(() => {
    const result = [];
    if (required) {
      result.push(validateSingleMandatoryField);
    }
    if (validate) {
      result.push(validate);
    }
    if (type === "tags") {
      result.push(
        (value, allValues, formProps) => validateTagsList(tags || [], value, allValues, formProps, (type === "tags"
            ? props.validateEntity
            : null
        ))
      );
    }
    return result.length > 1 ? result : result.length ? result[0] : undefined;
  }, [validate, required, type, type === "tags" && props.validateEntity]);

  return (
    <Field
      type={type}
      name={name}
      component={FormFieldBase}
      validate={validateResolver}
      props={{
        ref,
        format: rest.format
      }}
      {...rest}
    />
  );
});

export default FormField;