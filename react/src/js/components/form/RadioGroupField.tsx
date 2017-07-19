import * as React from "react";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {FieldLabel} from "./FieldLabel";
import {Item} from "../../model";

/**
 * This implementation is very complicated,
 * @Deprecated Use /components/RadioGroupFields.tsx
 */
export class RadioGroupField extends React.Component<RadioGroupFieldProps, RadioGroupFieldState> {
  constructor() {
    super();
  }

  render() {
    const {
      items,
      input,
      label,
      classes,
      required,
    } = this.props;

    return (
      <div>
        <FieldLabel
          name={input.name}
          label={label}
          required={required}
        />
        <span className="radio-list">
          {items.map(item => {
            return (
              <span key={item.key}>
                <input {...input} className={classes} type="radio" value={item.key}/>
                {' '}
                {item.value}
              </span>
            );
          })}
        </span>
      </div>
    );
  }
}

export interface RadioGroupFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps {
  items: Item[];
}

export interface RadioGroupFieldState {
}
