import * as React from "react";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";

export class RadioGroupField extends React.Component<RadioGroupFieldProps, RadioGroupFieldState> {
  constructor() {
    super();
  }

  render() {
    const {options, input, label, classes} = this.props;

    return (
      <p>
        <label htmlFor={input.name}>{label}</label>
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
      </p>
    );
  }
}

export interface RadioGroupFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps {
  options: string[]
}

export interface RadioGroupFieldState {
}
