import * as React from "react";
import {Item} from "../../model/common/Item";
import Wrapper from "./Wrapper";
import {WrappedFieldInputProps} from "redux-form";
import {inputFrom} from "./FieldsUtils";
/**
 * Redux-Form Fields implementation for Radio Group componenent
 */

interface Props {
  name: string,
  label: string,
  required: boolean,
  items: Item[]
}

class RadioGroup extends React.Component<any, any> {

  private onChange = (value) => {
    const {onChange} = this.props.input;
    if (onChange) {
      onChange(value)
    }
  };

  render() {
    const {name, items} = this.props;
    const input:WrappedFieldInputProps = inputFrom(this.props);

    const fields = items.map((i) => {
      return (
        <span key={i.key}>
          <input name={name} type="radio" value={i.key} checked={input.value == i.key}
                 onChange={() => {this.onChange(i.key)}} onBlur={input.onBlur} onFocus={input.onFocus}/>
          {' '}
          {i.value}
        </span>
      )
    });

    return (<Wrapper {...this.props}>
      <span className="radio-list">
        {fields}
      </span>
    </Wrapper>)
  }
}

export default RadioGroup