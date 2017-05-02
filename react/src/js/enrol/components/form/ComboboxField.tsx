import * as React from "react";
import classnames from "classnames";
import {Field, WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {Suggestion} from "../../../selectors/autocomplete";
import {showError, ValidateText} from "./ValidateText";
import {MouseHover, WrappedMouseHoverProps} from "../../../components/MouseHover";
import {BaseProps} from "../../../types";
import {FieldLabel} from "./FieldLabel";

export function ComboboxField(props: ComboboxFieldProps) {
  return (
    <MouseHover component={ComboboxFieldComponent} componentProps={props}/>
  );
}

function ComboboxFieldComponent(props: ComboboxFieldProps & WrappedMouseHoverProps) {
  const {
    suggestions,
    ...selectProps
  } = props;

  return (
    <Field
      component={SelectInput}
      {...selectProps}
    >
      {suggestions.map((suggestion: Suggestion) => {
        return (
          <option key={suggestion.key} value={suggestion.value}>{suggestion.key}</option>
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
  suggestions: Suggestion[];
}



