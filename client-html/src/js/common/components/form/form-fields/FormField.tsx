/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React, { useMemo } from "react";
import { createStringEnum } from "@api/model";
import { Field, BaseFieldProps } from "redux-form";
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
  "tags"
]);

interface Props {
  type?: keyof typeof EditInPlaceTypes;
  required?: boolean;
}

const FormFieldBase = React.forwardRef<any, Props>(({
 type,
 required,
 ...rest
}, ref) => {
  switch (type) {
    case "duration":
      return <EditInPlaceDurationField ref={ref} {...rest} />;
    case "file":
      return <EditInPlaceFileField ref={ref} {...rest} />;
    case "money":
      return <EditInPlaceMoneyField ref={ref} {...rest} />;
    case "select":
      return <EditInPlaceField select ref={ref} {...rest} />;
    case "searchSelect":
      return <EditInPlaceSearchSelect ref={ref} {...rest} />;
    case "remoteDataSearchSelect":
      return <EditInPlaceRemoteDataSearchSelect ref={ref} {...rest} />;
    case "number":
      return <EditInPlaceField ref={ref} {...rest} type="number" />;
    case "persent":
      return <EditInPlaceField ref={ref} {...rest} type="percentage" />;
    case "date":
      return <EditInPlaceDateTimeField ref={ref} {...rest} type="date" />;
    case "time":
      return <EditInPlaceDateTimeField ref={ref} {...rest} type="time" />;
    case "dateTime":
      return <EditInPlaceDateTimeField ref={ref} {...rest} type="datetime" />;
    case "aql":
      return <EditInPlaceQuerySelect ref={ref} {...rest} />;
    case "headerText":
      return <HeaderTextField ref={ref} {...rest} />;
    case "code":
      return <CodeEditorField ref={ref} {...rest} />;
    case "password":
      return <EditInPlaceField ref={ref} {...rest} type="password" />;
    case "switch":
      return <FormSwitch ref={ref} {...rest} />;
    case "checkbox":
      return <CheckboxField ref={ref} {...rest} />;
    case "multilineText":
      return <EditInPlaceField ref={ref} {...rest} multiline />;
    case "stub":
      return <div className="invisible" ref={ref} />;
    case "tags":
      return <SimpleTagList ref={ref} {...rest} />;
    case "text":
    default:
      return <EditInPlaceField ref={ref} {...rest} />;
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
  ...rest
  }, ref) => {
  const validateResolver = useMemo(() => {
    const result = [];
    if (required) {
      result.push(validateSingleMandatoryField);
    }
    if (validate) {
      result.push(validate);
    }

    return result.length > 1 ? result : result.length ? result[0] : undefined;
  }, [validate, required]);

  return (
    <Field
      name={name}
      component={FormFieldBase}
      validate={validateResolver}
      props={{
        ref
      }}
      {...rest}
    />
  );
});

export default FormField;

