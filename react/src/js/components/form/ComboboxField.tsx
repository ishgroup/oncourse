import * as React from "react";
import {Component} from "react";
import classnames from "classnames";
import {Field, WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {showError, ValidateText} from "./ValidateText";
import {MouseHover, WrappedMouseHoverProps} from "./MouseHover";
import {BaseProps} from "../../types";
import {FieldLabel} from "./FieldLabel";
import {Item} from "../../model/common/Item";


export class ComboboxField extends Component<any, any> {
  render() {
    return (
      <MouseHover component={ComboboxFieldComponent} componentProps={this.props}/>
    );
  }
}


function ComboboxFieldComponent(props: ComboboxFieldProps & WrappedMouseHoverProps) {
  const {
    items,
    ...selectProps
  } = props;

  return (
    <Field
      component={SelectInput}
      {...selectProps}
    >
      {items.map((item: Item) => {
        return (
          <option key={item.key} value={item.key}>{item.value}</option>
        );
      })}
    </Field>
  );
}

function SelectInput(props: SelectInputProps) {
  const {
    input,
    children,
    required,
    label
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
        "has-error": isShowError
      })}>
      <select
        {...input}
        className={classnames("input-fixed", "contact-field", {"t-error": isShowError})}
      >
        {children}
      </select>
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

interface SelectInputProps extends ComboboxFieldProps, Partial<WrappedFieldProps<{}>>, WrappedMouseHoverProps,
  BaseProps {
}

interface ComboboxFieldProps extends CommonFieldProps {
  items: Item[];
}



