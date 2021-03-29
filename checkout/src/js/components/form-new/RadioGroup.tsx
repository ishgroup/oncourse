import * as React from "react";
import {Item} from "../../model";
import Wrapper from "./Wrapper";
import {WrappedFieldInputProps} from "redux-form";
import {inputFrom} from "./FieldsUtils";
/**
 * Redux-Form Fields implementation for Radio Group componenent
 */

interface Props {
  name: string;
  label: string;
  required: boolean;
  items: Item[];
}

class RadioGroup extends React.Component<any, any> {

  private handleChange = value => {
    const {onChange} = this.props;
    if (onChange) {
      onChange(value);
    }
  }

  render() {
    const {name, items, disabled} = this.props;
    const input:WrappedFieldInputProps = inputFrom(this.props);

    const fields = items.map(i => {
      return (
        <span key={i.key}>
          <input
            id={`${i.key}`}
            name={name}
            type="radio"
            value={i.key}
            checked={input.value === i.key}
            onChange={() => { this.handleChange(i.key);}}
            onBlur={input.onBlur}
            onFocus={input.onFocus}
            disabled={disabled || i.disabled}
          />
          <span>&nbsp; {i.value}</span>
        </span>
      );
    });

    return (<Wrapper {...this.props}>
      <span className="radio-list">
        {fields}
      </span>
    </Wrapper>);
  }
}

export default RadioGroup;
