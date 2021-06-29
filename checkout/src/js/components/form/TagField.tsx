import * as React from "react";
import {WrappedFieldProps} from "redux-form";
import classnames from "classnames";
import {CommonFieldProps} from "./CommonFieldProps";
import {FieldLabel} from "./FieldLabel";
import {Item} from "../../model";

/**
 * This implementation is very complicated,
 * @Deprecated Use /components/RadioGroupFields.tsx
 */
export class TagField extends React.Component<TagFieldProps, TagFieldState> {
  constructor(props) {
    super(props);
  }

  render() {
    const {
        items,
        input,
        label,
        classes,
        required,
    } = this.props;

    const showError = this.props.meta.invalid && this.props.meta.touched;

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
              <input
                  {...input}
                  className={classnames(classes, {'t-error': showError})}
                  type="radio"
                  value={item.key}
              />
              <span>
                {' '}
                  {item.value}
              </span>

            </span>
          );
        })}
      </span>
          </div>
    );
  }
}

export interface TagFieldProps extends Partial<WrappedFieldProps>, CommonFieldProps {
  items: Item[];
}

export interface TagFieldState {
}
