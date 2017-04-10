import * as React from "react";
import classnames from "classnames";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {MouseTrap, MouseTrapChildProps} from "../../MouseTrap";
import {showError, ValidateText} from "./ValidateText";

export const TextAreaField = (props) => (
  <MouseTrap component={inputComponent} componentProps={props}/>
);

function inputComponent(props: TextAreaFieldProps) {
  const {
    input,
    required,
    label,
    rows,
    classes
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
        <textarea {...input}
                  placeholder={label}
                  className={classnames(classes, 'input-fixed', {'t-error': isShowError})}
                  rows={rows}
        />
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

export interface TextAreaFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps, MouseTrapChildProps {
  required: boolean;
  rows: number;
}

