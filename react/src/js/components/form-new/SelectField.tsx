import * as React from "react";
import Select from "react-select";

import {Item} from "../../model/common/Item";
import Wrapper from "./Wrapper";
import {inputFrom} from "./FieldsUtils";
import {WrappedFieldInputProps} from "redux-form";

interface Props {
  input: WrappedFieldInputProps
  labelKey: string
  valueKey: string
  filterOption: (option: any, filter: string) => boolean
  optionComponent: React.Component<any, any>
  valueComponent: React.Component<any, any>
  newOptionCreator: (newOption: { label: string, labelKey: string, valueKey: string }) => any,
  handleChange: (value: any) => void;
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
    const input: WrappedFieldInputProps = inputFrom(this.props);
    const {onBlur} = input;
    if (this.select && onBlur) {
      onBlur(this.select.value)
    }
  };

  private onFocus = (e) => {
    const input: WrappedFieldInputProps = inputFrom(this.props);
    const {onFocus} = input;
    if (onFocus) {
      onFocus(e)
    }
  };

  private onChange = (value) => {
    const input: WrappedFieldInputProps = inputFrom(this.props);
    const {onChange} = input;
    if (onChange) {
      onChange(value.key)
    }
    if (this.props.handleChange) {
      this.props.handleChange(value)
    }
  };


  toProps = (): Props => {
    const input: WrappedFieldInputProps = inputFrom(this.props);
    const labelKey = this.props.labelKey ? this.props.labelKey : "value";
    const valueKey = this.props.valueKey ? this.props.valueKey : "key";
    const {filterOption, optionComponent, valueComponent, newOptionCreator, handleChange} = this.props;
    return {
      input: input,
      labelKey: labelKey,
      valueKey: valueKey,
      filterOption: filterOption,
      optionComponent: optionComponent,
      valueComponent: valueComponent,
      newOptionCreator: newOptionCreator,
      handleChange: handleChange
    };
  };

  render() {
    const props: Props = this.toProps();
    const renderSelect = props.newOptionCreator ? this.renderAsyncCreatable : this.renderAsync;
    return (
      <Wrapper {...this.props}>
        {renderSelect(props)}
      </Wrapper>
    );
  }

  renderAsync = (props: Props) => {
    return (
      <Select.Async
        name={name}
        labelKey={props.labelKey}
        valueKey={props.valueKey}
        searchable={true}
        clearable={false}
        value={props.input.value}
        loadOptions={this.loadOptions}
        onBlur={this.onBlur}
        onFocus={this.onFocus}
        onChange={this.onChange}
        optionComponent={props.optionComponent}
        valueComponent={props.valueComponent}
        filterOption={props.filterOption}
        ref={(input) => {
          this.select = input;
        }}
      />
    );
  };

  renderAsyncCreatable = (props: Props) => {
    return (
      <Select.AsyncCreatable
        name={name}
        labelKey={props.labelKey}
        valueKey={props.valueKey}
        searchable={true}
        clearable={false}
        value={props.input.value}
        loadOptions={this.loadOptions}
        onBlur={this.onBlur}
        onFocus={this.onFocus}
        onChange={this.onChange}
        optionComponent={props.optionComponent}
        valueComponent={props.valueComponent}
        newOptionCreator={props.newOptionCreator}
        filterOption={props.filterOption}
        promptTextCreator={(label) => {return label}}
        ref={(input) => {
          this.select = input;
        }}
      />
    );
  };
}

export default SelectField;

