import React from "react";
import {Field, BaseFieldProps} from "redux-form";
import {createStringEnum} from "../../../utils/EnumUtils";
import {validateSingleMandatoryField} from "../../../utils/validation";
import EditInPlaceField from "./EditInPlaceField";
import {CheckboxField} from "./CheckboxField";
import {FormSwitch} from "./Switch";
import {ColorPicker} from "./ColorPicker";

const InputTypes = createStringEnum([
  "select",
  "number",
  "percent",
  "password",
  "switch",
  "checkbox",
  "multilineText",
  "stub",
  "colorPicker",
  "text",
]);

interface Props {
  type?: keyof typeof InputTypes;
  required?: boolean;
}

const FormFieldBase = React.forwardRef<any, Props>(({
  type,
  required,
  ...rest
},                                                  ref) => {
  switch (type) {
    case "select":
      return <EditInPlaceField select ref={ref} {...rest} />;
    case "number":
      return <EditInPlaceField ref={ref} {...rest} type="number" />;
    case "percent":
      return <EditInPlaceField ref={ref} {...rest} type="percentage" />;
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
    case "colorPicker":
      return <ColorPicker ref={ref} {...rest} />;
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
},                                                                      ref) => {

  const validateResolver = React.useMemo(() => {
    const result = [];
    if (required) {
      result.push(validateSingleMandatoryField);
    }
    if (validate) {
      result.push(validate);
    }

    return result.length > 1 ? result : result.length ? result[0] : undefined;
  },                                     [validate, required]);

  return (
    <Field
      name={name}
      component={FormFieldBase}
      validate={validateResolver}
      props={{
        ref,
      }}
      {...rest}
    />
  );
});

export default FormField;
