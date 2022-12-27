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
import { createStringEnum } from "@api/model";
import { BaseFieldProps, Field, WrappedFieldProps } from "redux-form";
import debounce from "lodash.debounce";
import { validateSingleMandatoryField } from "../../../utils/validation";
import SimpleTagList from "../simpleTagListComponent/SimpleTagList";
import { CheckboxField } from "./CheckboxField";
import CodeEditorField from "./CodeEditorField";
import HeaderTextField from "./HeaderTextField";
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
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";

const EditInPlaceTypes = createStringEnum([
  "text",
  "multilineText",
  "headerText",
  "date",
  "dateTime",
  "duration",
  "time",
  "file",
  "money",
  "password",
  "aql",
  "select",
  "remoteDataSelect",
  "number",
  "persent",
  "code",
  "checkbox",
  "switch",
  "stub",
  "tags",
  "phone"
]);

interface Props extends Partial<WrappedFieldProps> {
  type?: keyof typeof EditInPlaceTypes;
  required?: boolean;
  format?: AnyArgFunction;

  // Performance optimization property that handles field value rendering outside of Redux Form life circle.
  // It is true by default
  // Should be set to false when Redux Form lificircle handlers are used (onChange,onBlur and etc)
  debounced?: boolean;
}

const FormFieldBase = React.forwardRef<any, Props>(({
 type,
 required,
 debounced = true,
 format,
 ...rest
}, ref) => {
  const [value, setValue] = useState(rest.input?.value);

  const debounceChange = useCallback(debounce(rest.input.onChange, 600), [rest.input.onChange]);

  const debounceBlur = useCallback(debounce(rest.input.onBlur, 600), [rest.input.onBlur]);

  const inputProxy = useMemo(() => ({
    ...rest.input || {},
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
  }), [value, rest.input]);

  useEffect(() => {
    if (rest.input?.value !== value) {
      setValue(format ? format(rest.input?.value) : rest.input?.value);
    }
  }, [rest.input?.value]);

  const sharedProps = {
    ...rest,
    ...debounced ? { input: inputProxy } : {}
  };

  switch (type) {
    case "phone":
      return <EditInPlacePhoneField ref={ref} {...sharedProps} />;
    case "duration":
      return <EditInPlaceDurationField ref={ref} {...sharedProps} />;
    case "file":
      return <EditInPlaceFileField ref={ref} {...sharedProps} />;
    case "money":
      return <EditInPlaceMoneyField ref={ref} {...sharedProps} />;
    case "select":
      return <EditInPlaceSearchSelect inputRef={ref} {...sharedProps} />;
    case "remoteDataSelect":
      return <EditInPlaceRemoteDataSearchSelect ref={ref} {...sharedProps} />;
    case "number":
      return <EditInPlaceField ref={ref} {...sharedProps} type="number" />;
    case "persent":
      return <EditInPlaceField ref={ref} {...sharedProps} type="percentage" />;
    case "date":
      return <EditInPlaceDateTimeField ref={ref} {...sharedProps} type="date" />;
    case "time":
      return <EditInPlaceDateTimeField ref={ref} {...sharedProps} type="time" />;
    case "dateTime":
      return <EditInPlaceDateTimeField ref={ref} {...sharedProps} type="datetime" />;
    case "aql":
      return <EditInPlaceQuerySelect ref={ref} {...sharedProps as any} />;
    case "headerText":
      return <HeaderTextField ref={ref} {...sharedProps} />;
    case "code":
      return <CodeEditorField ref={ref} {...sharedProps} />;
    case "password":
      return <EditInPlaceField ref={ref} {...sharedProps} type="password" />;
    case "switch":
      return <FormSwitch ref={ref} {...sharedProps} />;
    case "checkbox":
      return <CheckboxField ref={ref} {...sharedProps} />;
    case "multilineText":
      return <EditInPlaceField ref={ref} {...sharedProps} multiline />;
    case "stub":
      return <div className="invisible" ref={ref} />;
    case "tags":
      return <SimpleTagList ref={ref} {...sharedProps} />;
    case "text":
    default:
      return <EditInPlaceField ref={ref} {...sharedProps} />;
  }
});

type BaseProps = Props & BaseFieldProps<Props> & {
  [prop: string]: any;
  props?: any;
};

const FormField:React.FC<BaseProps> = React.forwardRef<any, BaseProps>(({
  name,
  required,
  validate,
  tags,
  type,
  ...rest
  }, ref) => {
  const validateTags = useCallback((...args: [any, any, any]) => validateTagsList(tags || [], ...args), [tags]);
  
  const validateResolver = useMemo(() => {
    const result = [];
    if (required) {
      result.push(validateSingleMandatoryField);
    }
    if (validate) {
      result.push(validate);
    }
    if (type === "tags") {
      result.push(validateTags);
    }

    return result.length > 1 ? result : result.length ? result[0] : undefined;
  }, [validate, required, type, validateTags]);

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
      tags={tags}
      {...rest}
    />
  );
});

export default FormField;