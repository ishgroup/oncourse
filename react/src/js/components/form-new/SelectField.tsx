import * as React from "react";
import Select from "react-select";

import {Item} from "../../model/common/Item";
import Wrapper from "./Wrapper";
import {inputFrom} from "./FieldsUtils";
import {WrappedFieldInputProps} from "redux-form";

interface Props {
  input: WrappedFieldInputProps;
  labelKey: string;
  valueKey: string;
  newOptionEnable: boolean;
}

/**
 *
 */
class SelectField extends React.Component<any, any> {

  private loadOptions = (input: string): Promise<any> => {
    const {loadOptions} = this.props;
    if (loadOptions) {
      return loadOptions(input).then((data: Item[]) => {
        return {options: data.map(item => ({key: item.key, value: item.value}))};
      });
    } else {
      return Promise.resolve([]);
    }
  }

  private onChange = res => {
    const input: WrappedFieldInputProps = inputFrom(this.props);
    input.onChange(res.value);
  }

  private onBlur = e => {
    const val = e.target.value;
    const props: Props = this.toProps();

    if (val && props.newOptionEnable) {
      this.onChange({key: val, value: val});
    }
  }

  toProps = (): Props => {
    const input: WrappedFieldInputProps = inputFrom(this.props);

    return {
      input,
      labelKey: this.props.labelKey ? this.props.labelKey : "value",
      valueKey: this.props.valueKey ? this.props.valueKey : "key",
      newOptionEnable: this.props.newOptionEnable || false,
    };
  }

  render() {
    const props: Props = this.toProps();
    const RenderSelectWrapper = props.newOptionEnable ? Select.AsyncCreatable : Select.Async;
    return (
      <Wrapper {...this.props}>
        <RenderSelectWrapper
          name={name}
          labelKey={props.labelKey}
          valueKey={props.valueKey}
          searchable={true}
          clearable={false}
          value={props.input}
          loadOptions={this.loadOptions}
          onBlur={this.onBlur}
          onChange={this.onChange}
          promptTextCreator={label => `${label} `}
        />
      </Wrapper>
    );
  }
}

export default SelectField;

