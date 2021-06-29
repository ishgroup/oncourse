import * as React from "react";
import classnames from "classnames";
import NumberFormat from "react-number-format";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {MouseHover, WrappedMouseHoverProps} from "./MouseHover";
import {showError, ValidateText} from "./ValidateText";
import {FieldLabel} from "./FieldLabel";

export const MoneyField: React.SFC<MoneyFieldProps> = props => (
  <MouseHover component={inputComponent} componentProps={props}/>
);

function inputComponent(props: any) {
  const {
    input,
    required,
    label,
    placeholder
  } = props;
  const isShowError = showError({...props, meta: props.meta || {}});

  // normalize value if changing from select field to text field
  if (typeof input.value === "object") {
    input.value = input.value.value;
  }

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
        <NumberFormat
          {...input}
          className={classnames('input-fixed', 'contact-field', {'t-error': isShowError})}
          prefix={'$ '}
          decimalScale={0}
          allowNegative={false}
          placeholder={placeholder || label}
        />
        <ValidateText {...props} meta={props.meta || {}}/>
      </span>
    </div>
  );
}

export interface MoneyFieldProps extends Partial<WrappedFieldProps>, CommonFieldProps, WrappedMouseHoverProps {
  type: string;
  required: boolean;
}
