import * as React from "react";
import classnames from "classnames";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {MouseHover, WrappedMouseHoverProps} from "../../../components/MouseHover";
import {showError, ValidateText} from "./ValidateText";
import {FieldLabel} from "./FieldLabel";

export const TextAreaField = (props) => (
  <MouseHover component={inputComponent} componentProps={props}/>
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
        <textarea {...input}
                  placeholder={label}
                  className={classnames(classes, 'input-fixed', {'t-error': isShowError})}
                  rows={rows}
        />
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

export interface TextAreaFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps, WrappedMouseHoverProps {
  required: boolean;
  rows: number;
}

