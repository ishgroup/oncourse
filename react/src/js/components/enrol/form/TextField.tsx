import * as React from "react";
import classnames from "classnames";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {MouseTrap, MouseTrapChildProps} from "../../MouseTrap";
import {showError, ValidateText} from "./ValidateText";

export const TextField = (props) => (
  <MouseTrap component={inputComponent} componentProps={props}/>
);

function inputComponent(props: TextFieldProps) {
  const {
    input,
    required,
    label,
    type,
    autocomplete
  } = props;
  const isShowError = showError(props);

  return (
    <p>
      <label htmlFor={input.name}>{label}{required && requiredText()}</label>
      <span className={classnames({
        valid: !isShowError,
        validate: isShowError,
        'has-error': isShowError
      })}>
        <input {...input}
             className={classnames('input-fixed', 'contact-field', {'t-error': isShowError})}
             autoComplete={autocomplete}
             placeholder={label}
             type={type}/>
        <ValidateText {...props}/>
      </span>
    </p>
  );
}

function requiredText() {
  return (
    <em title="This field is required">*</em>
  );
}

export interface TextFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps, MouseTrapChildProps {
  type: string;
  required: boolean;
  autocomplete: string;
}
