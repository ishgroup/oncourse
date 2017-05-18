import * as React from "react";
import Select from "react-select";

import {Item} from "../../model/common/Item";
import Wrapper from "./Wrapper";
import {inputFrom} from "./FieldsUtils";
import {WrappedFieldInputProps} from "redux-form";

interface Props {
  name: string,
  label: string,
  required: boolean,
  value: string,
  loadOptions: (input) => Promise<Item[]>
}

/**
 *
 */
class SelectField extends React.Component<any, any> {
  private select: Select.Async;

  private loadOptions = (input: string): Promise<any> => {
    const {loadOptions} = this.props;
    if (loadOptions) {
      return loadOptions(input).then((data: Item[]) => {
        return {options: data}
      });
    } else {
      return Promise.resolve([]);
    }
  };

  private onBlur = (e) => {
    const input:WrappedFieldInputProps = inputFrom(this.props);
    const {onBlur} = input;
    if (onBlur) {
      onBlur(e)
    }
  };

  private onFocus = (e) => {
    const input:WrappedFieldInputProps = inputFrom(this.props);
    const {onFocus} = input;
    if (onFocus) {
      onFocus(e)
    }
  };

  private onChange = (value) => {
    const input:WrappedFieldInputProps = inputFrom(this.props);
    const {onChange} = input;
    if (onChange) {
      onChange(value.key)
    }
  };

  render() {
    const input:WrappedFieldInputProps = inputFrom(this.props);
    return (
      <Wrapper {...this.props}>
          <Select.Async
            name={name}
            labelKey={'value'}
            valueKey={'key'}
            searchable={true}
            clearable={false}
            value={input.value}
            loadOptions={this.loadOptions}
            onBlur={this.onBlur}
            onFocus={this.onFocus}
            onChange={this.onChange}
            ref={(input) => { this.select = input; }}
          />
      </Wrapper>
    );
  }
}

export default SelectField;

