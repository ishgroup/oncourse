import * as React from "react";
import classnames from "classnames";
import Select from "react-select";
import {MouseHover, WrappedMouseHoverProps} from "../form/MouseHover";
import {showError, ValidateText} from "../form/ValidateText";
import {FieldLabel} from "../form/FieldLabel";


import {Item} from "../../model";
import Wrapper from "./Wrapper";
import {inputFrom, metaFrom} from "./FieldsUtils";
import {WrappedFieldInputProps, WrappedFieldMetaProps} from "redux-form";

interface Props {
  input: WrappedFieldInputProps;
  meta: WrappedFieldMetaProps<any>;
  labelKey: string;
  valueKey: string;
  newOptionEnable: boolean;
  required: boolean;
  label: string;
  onBlurSelect?: (field) => void;
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
      required: this.props.required,
      label: this.props.label,
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
            className={classnames({'t-error': isShowError})}
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
          <ValidateText {...props}/>
        </span>
      </div>
    )

    return (
      <MouseHover component={inner} componentProps={props}/>
    );
  }
}

export default SelectField;

