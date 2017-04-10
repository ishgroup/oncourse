import * as React from "react";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";

export const CheckboxField = ({input, label, classes}: CheckboxFieldProps) => {
  return (
    <p>
      <label htmlFor={input.name}>{label}</label>
      <input {...input} className={classes} type="checkbox"/>
    </p>
  );
};


export interface CheckboxFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps {
}
