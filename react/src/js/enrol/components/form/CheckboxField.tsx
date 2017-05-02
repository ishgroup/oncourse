import * as React from "react";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {FieldLabel} from "./FieldLabel";

export const CheckboxField = ({input, label, classes, required}: CheckboxFieldProps) => {
  return (
    <div>
      <FieldLabel
        name={input.name}
        label={label}
        required={required}
      />
      <input {...input} className={classes} type="checkbox"/>
    </div>
  );
};

export interface CheckboxFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps {
}
