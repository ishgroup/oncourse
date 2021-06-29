import * as React from "react";
import {WrappedMouseHoverProps} from "./MouseHover";
import {WrappedFieldProps} from "redux-form";

export const ValidateText = (props: ValidateTextProps) => {
  const {mouseInside, meta: {error, warning}} = props;

  return (
    <span className="validate-text">
      {mouseInside && showError(props) && errorText(error)}
      {mouseInside && showWarning(props) && warningText(warning)}
    </span>
  );
};

function errorText(error) {
  if (!error) {
    return null;
  }

  return (
    <span className="reason">
      {error} <span className="reason-pointer"/>
    </span>
  );
}

function warningText(warning) {
  if (!warning) {
    return null;
  }

  return (
    <span className="hint">
      {warning} <span className="hint-pointer"/>
    </span>
  );
}

export function showError({meta: {invalid, touched}}: ValidateTextProps) {
  return touched && invalid;
}

function showWarning({meta: {touched, valid, warning}}: ValidateTextProps) {
  return (!touched && warning) || (valid && warning);
}

interface ValidateTextProps extends Partial<WrappedFieldProps>, WrappedMouseHoverProps {
}

