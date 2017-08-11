import * as React from "react";
import classnames from "classnames";

import Select from "react-select";
import "react-select/dist/react-select.css";

import {MouseHover} from "../form/MouseHover";
import {showError, ValidateText} from "../form/ValidateText";
import {FieldLabel} from "../form/FieldLabel";


import {Item} from "../../model";
import {inputFrom, metaFrom} from "./FieldsUtils";
import {WrappedFieldInputProps, WrappedFieldMetaProps} from "redux-form";


interface Props {
  input: WrappedFieldInputProps;
  placeholder?: string;
  returnType?: string;
  meta: WrappedFieldMetaProps<any>;
  labelKey: string;
  valueKey: string;
  newOptionEnable: boolean;
  required: boolean;
  label: string;
  searchable?: boolean;
  onBlurSelect?: (field) => void;
}

/**
 *
 */

class SelectField extends React.Component<any, any> {

  private loadOptions = (input: string, showValuesOnInit: boolean): Promise<any> => {
    const {loadOptions} = this.props;
    if (loadOptions && (input.length > 0 || showValuesOnInit)) {
      return loadOptions(input).then((data: Item[]) => {
        return {options: data.map(item => ({key: item.key, value: item.value}))};
      });
    } else {
      return Promise.resolve([]);
    }
  }

  private onChange = res => {
    const input: WrappedFieldInputProps = inputFrom(this.props);
    input.onChange(this.props.returnType === 'object' ? res : res.value);
  }

  private onBlur = e => {
    const val = e.target.value;
    const props: Props = this.toProps();
    props.onBlurSelect && props.onBlurSelect(props.input.name);

    if (val && props.newOptionEnable) {
      this.onChange({key: val, value: val});
    }
  }

  toProps = (): Props => {
    const input: WrappedFieldInputProps = inputFrom(this.props);
    const meta = metaFrom(this.props);
    return {
      input,
      meta,
      placeholder: this.props.placeholder || "Please Select...",
      required: this.props.required,
      label: this.props.label,
      searchable: this.props.searchable !== false,
      labelKey: this.props.labelKey ? this.props.labelKey : "value",
      valueKey: this.props.valueKey ? this.props.valueKey : "key",
      newOptionEnable: this.props.newOptionEnable || false,
      onBlurSelect: this.props.onBlurSelect ? this.props.onBlurSelect : undefined,
    };
  }

  render() {
    const props: any = this.toProps();
    const RenderSelectWrapper = props.newOptionEnable ? Select.AsyncCreatable : Select.Async;
    const isShowError = showError(props);
    const showValuesOnInit: boolean = !props.searchable;

    const inner = props => (
      <div>
        {props.label &&
          <FieldLabel
            name={props.input.name}
            label={props.label}
            required={props.required}
          />
        }
        <span className={classnames({
          valid: !isShowError,
          validate: isShowError,
          'has-error': isShowError,
        })}>
          <RenderSelectWrapper
            inputProps={{autoComplete: 'off', autoCorrect: 'off', spellCheck: 'off'}}
            className={classnames({'t-error': isShowError})}
            placeholder={props.placeholder}
            name={name}
            labelKey={props.labelKey}
            valueKey={props.valueKey}
            searchable={props.searchable}
            clearable={false}
            value={this.props.returnType === 'object' ? props.input.value.key : props.input.value && props.input}
            loadOptions={input => this.loadOptions(input, showValuesOnInit)}
            onBlur={this.onBlur}
            onChange={this.onChange}
            promptTextCreator={label => `${label} `}
          />
          <ValidateText {...props}/>
        </span>
      </div>
    );

    return (
      <MouseHover component={inner} componentProps={props}/>
    );
  }
}

export default SelectField;

