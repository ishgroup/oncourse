import * as React from "react";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {FieldLabel} from "./FieldLabel";

export class RadioGroupField extends React.Component<RadioGroupFieldProps, RadioGroupFieldState> {
  constructor() {
    super();
  }

  render() {
    const {
      options,
      input,
      label,
      classes,
      required
    } = this.props;

    return (
      <div>
        <FieldLabel
          name={input.name}
          label={label}
          required={required}
        />
        <span className="radio-list">
          {options.map(option => {
            return (
              <span key={option}>
                <input {...input} className={classes} type="radio"/>
                {' '}
                {option}
              </span>
            );
          })}
        </span>
      </div>
    );
  }
}

export interface RadioGroupFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps {
  options: string[]
}

export interface RadioGroupFieldState {
}
