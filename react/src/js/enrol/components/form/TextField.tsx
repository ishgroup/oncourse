import * as React from "react";
import classnames from "classnames";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {MouseHover, WrappedMouseHoverProps} from "../../../components/MouseHover";
import {showError, ValidateText} from "./ValidateText";
import {FieldLabel} from "./FieldLabel";

export const TextField = (props) => (
  <MouseHover component={inputComponent} componentProps={props}/>
);

function inputComponent(props: TextFieldProps) {
  const {
    input,
    required,
    label,
    placeholder,
    type,
  } = props;
  const isShowError = showError(props);

  return (
    <div>
      <FieldLabel
        name={input.name}
        label={label}
        required={required}
      />
      <span className={classnames({
        valid: !isShowError,
        validate: isShowError,
        'has-error': isShowError
      })}>
        <input {...input}
             className={classnames('input-fixed', 'contact-field', {'t-error': isShowError})}
             placeholder={placeholder}
             type={type}/>
        <ValidateText {...props}/>
      </span>
    </div>
  );
}

export interface TextFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps, WrappedMouseHoverProps {
  type: string;
  required: boolean;
}
