import * as React from "react";
import classnames from "classnames";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {MouseTrap, MouseTrapChildProps} from "../../MouseTrap";
import {showError, ValidateText} from "./ValidateText";
import {AutocompleteField} from "./AutocompleteField";

export const TextField = (props) => (
  <MouseTrap component={inputComponent} componentProps={props}/>
);

function inputComponent(props: TextFieldProps) {
  const {
    input,
    required,
    label,
    type
  } = props;
  const isShowError = showError(props);

  return (
    <div>
      <label htmlFor={input.name}>{label}{required && requiredText()}</label>
      <span className={classnames({
        valid: !isShowError,
        validate: isShowError,
        'has-error': isShowError
      })}>
        <input {...input}
             className={classnames('input-fixed', 'contact-field', {'t-error': isShowError})}
             placeholder={label}
             type={type}/>
        <ValidateText {...props}/>
      </span>
    </div>
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
}
