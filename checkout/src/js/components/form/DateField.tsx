import * as React from "react";
import MaskedTextInput from "react-text-mask";
import classnames from "classnames";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {MouseHover, WrappedMouseHoverProps} from "./MouseHover";
import {showError, ValidateText} from "./ValidateText";
import {FieldLabel} from "./FieldLabel";

export const DateField = props => (
  <MouseHover component={inputComponent} componentProps={props}/>
);

function inputComponent(props: DateFieldProps) {
  const {
    input,
    required,
    label,
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
        "has-error": isShowError,
      })}>
        <MaskedTextInput
          mask={[/\d/, /\d/, "/", /\d/, /\d/, "/", /\d/, /\d/, /\d/, /\d/]}
          {...input}
          className={classnames("input-fixed", "contact-field", {"t-error": isShowError})}
          placeholder={label}
          type={type}
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

export interface DateFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps, WrappedMouseHoverProps {
  type: string;
  required: boolean;
}
