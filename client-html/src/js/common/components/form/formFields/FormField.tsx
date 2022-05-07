/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, { useCallback, useMemo, useState } from "react";
import { createStringEnum } from "@api/model";
import { BaseFieldProps, Field, WrappedFieldProps } from "redux-form";
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
import debounce from "lodash.debounce";

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
  "searchSelect",
  "remoteDataSearchSelect",
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
}

const FormFieldBase = React.forwardRef<any, Props>(({
 type,
 required,
 ...rest
}, ref) => {
  const [value, setValue] = useState(rest.input?.value);

  const debounceChange = useCallback(debounce(rest.input.onChange, 600), [rest.input.onChange]);

  const debounceBlur = useCallback(debounce(rest.input.onBlur, 600), [rest.input.onBlur]);

  const inputProxy = useMemo(() => ({
    ...rest.input || {},
    value,
    onChange: (e) => {
      setValue(e.target ? e.target.value : e);
      debounceChange(e);
    },
    onBlur: (e) => {
      setValue(e.target ? e.target.value : e);
      debounceBlur(e);
    },
  }), [value, rest.input]);


  switch (type) {
    case "phone":
      return <EditInPlacePhoneField ref={ref} {...rest} input={inputProxy} />;
    case "duration":
      return <EditInPlaceDurationField ref={ref} {...rest} input={inputProxy} />;
    case "file":
      return <EditInPlaceFileField ref={ref} {...rest} input={inputProxy} />;
    case "money":
      return <EditInPlaceMoneyField ref={ref} {...rest} input={inputProxy} />;
    case "select":
      return <EditInPlaceField select ref={ref} {...rest} input={inputProxy} />;
    case "searchSelect":
      return <EditInPlaceSearchSelect ref={ref} {...rest} input={inputProxy} />;
    case "remoteDataSearchSelect":
      return <EditInPlaceRemoteDataSearchSelect ref={ref} {...rest} input={inputProxy} />;
    case "number":
      return <EditInPlaceField ref={ref} {...rest} type="number" input={inputProxy} />;
    case "persent":
      return <EditInPlaceField ref={ref} {...rest} type="percentage" input={inputProxy} />;
    case "date":
      return <EditInPlaceDateTimeField ref={ref} {...rest} type="date" input={inputProxy} />;
    case "time":
      return <EditInPlaceDateTimeField ref={ref} {...rest} type="time" input={inputProxy} />;
    case "dateTime":
      return <EditInPlaceDateTimeField ref={ref} {...rest} type="datetime" input={inputProxy} />;
    case "aql":
      return <EditInPlaceQuerySelect ref={ref} {...rest as any} input={inputProxy} />;
    case "headerText":
      return <HeaderTextField ref={ref} {...rest} input={inputProxy} />;
    case "code":
      return <CodeEditorField ref={ref} {...rest} input={inputProxy} />;
    case "password":
      return <EditInPlaceField ref={ref} {...rest} type="password" input={inputProxy} />;
    case "switch":
      return <FormSwitch ref={ref} {...rest} input={inputProxy} />;
    case "checkbox":
      return <CheckboxField ref={ref} {...rest} input={inputProxy} />;
    case "multilineText":
      return <EditInPlaceField ref={ref} {...rest} multiline input={inputProxy} />;
    case "stub":
      return <div className="invisible" ref={ref} />;
    case "tags":
      return <SimpleTagList ref={ref} {...rest} input={inputProxy} />;
    case "text":
    default:
      return <EditInPlaceField ref={ref} {...rest} input={inputProxy} />;
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
  const validateTags = useCallback((...args: [any, any, any]) => validateTagsList(tags && tags.length > 0 ? tags : [], ...args), [tags]);
  
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
        ref
      }}
      tags={tags}
      {...rest}
    />
  );
});

export default FormField;

