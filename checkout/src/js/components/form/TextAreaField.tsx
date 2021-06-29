import * as React from "react";
import classnames from "classnames";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {MouseHover, WrappedMouseHoverProps} from "./MouseHover";
import {showError, ValidateText} from "./ValidateText";
import {FieldLabel} from "./FieldLabel";

export const TextAreaField = props => (
  <MouseHover component={inputComponent} componentProps={props}/>
);

function inputComponent(props: any) {
  const {
    input,
    required,
    label,
    rows,
    classes,
    placeholder,
  } = props;
  const isShowError = showError({...props, meta: props.meta || {}});

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
        'has-error': isShowError,
      })}>
        <textarea {...input}
                  placeholder={placeholder || label}
                  className={classnames(classes, 'input-fixed', {'t-error': isShowError})}
                  rows={rows}
        />
        <ValidateText {...props} meta={props.meta || {}}/>
      </span>
    </div>
  );
}

export interface TextAreaFieldProps extends Partial<WrappedFieldProps>, CommonFieldProps, WrappedMouseHoverProps {
  required: boolean;
  rows: number;
}

