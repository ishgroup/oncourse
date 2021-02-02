import * as React from "react";
import MaskedTextInput from "react-text-mask";
import classnames from "classnames";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {MouseHover, WrappedMouseHoverProps} from "./MouseHover";
import {showError, ValidateText} from "./ValidateText";
import {FieldLabel} from "./FieldLabel";

export const MaskedField: React.SFC<MaskedFieldProps> = props => (
  <MouseHover component={inputComponent} componentProps={props}/>
);

function inputComponent(props: MaskedFieldProps) {
  const {
    input,
    required,
    label,
    type,
    placeholder,
    mask
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
          mask={mask}
          {...input}
          className={classnames("input-fixed", "contact-field", {"t-error": isShowError})}
          placeholder={placeholder || label}
          type={type}
        />
        <ValidateText {...props}/>
      </span>
    </div>
  );
}

export interface MaskedFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps, WrappedMouseHoverProps {
  type: string;
  required: boolean;
  hint: string;
  mask: (string | RegExp)[]
}
