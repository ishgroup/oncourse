import * as React from "react";
import classnames from "classnames";
import debounce from "debounce-promise";

import Select from "react-select";
import "react-select/dist/react-select.css";

import {MouseHover} from "../form/MouseHover";
import {showError, ValidateText} from "../form/ValidateText";
import {HintText} from "../form/HintText";
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
  showOnFocus?: boolean;
  required: boolean;
  label: string;
  hint?: string;
  allowEditSelected?: boolean;
  autocomplete?: string;
  searchable?: boolean;
  fullWidth?: boolean;
  onBlurSelect?: (field) => void;
  optionRenderer?: (option) => void;
  valueRenderer?: (option) => void;
}

/**
 *
 */

class SelectField extends React.Component<any, any> {
  private selectComponent: any;

  private loadOptions = (input: string): Promise<any> => {
    const {loadOptions} = this.props;
    const props: any = this.toProps();
    const showValuesOnInit: boolean = !props.searchable || props.showOnFocus;

    if (loadOptions && showValuesOnInit) {
      return loadOptions(input).then((data: Item[]) => {
        return {options: data.map(item => (item))};
      });
    } else {
      return Promise.resolve([]);
    }
  }

  private loadOptionsDebounce = debounce((input: string): Promise<any> => {
    const {loadOptions} = this.props;
    if (loadOptions) {
      return loadOptions(input).then((data: Item[]) => {
        return {options: data.map(item => (item))};
      });
    } else {
      return Promise.resolve([]);
    }
  },                                     600);

  private onChange = res => {
    const input: WrappedFieldInputProps = inputFrom(this.props);
    const result = (typeof res === "object" && res.length === 0) ? null : res;

    const props: Props = this.toProps();
    props.onBlurSelect && props.onBlurSelect(props.input.name);

    input.onChange(this.props.returnType === 'object' ? result : result && result.value);
  }

  private onBlur = e => {
    const val = e.target.value;
    const props: Props = this.toProps();
    props.onBlurSelect && props.onBlurSelect(props.input.name);

    if (val && props.newOptionEnable) {
      this.onChange({key: val, value: val});
    }
  }

  private onOpen = valueObj => {
    if (valueObj) {
      this.selectComponent.select.handleInputChange({target: {value: typeof valueObj === "object" ? valueObj.value : valueObj}});
    }
  }

  private normalizeObject = value => {
    if (value && typeof value === "string") {
      return {value};
    }
    return value;
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
      hint: this.props.hint,
      fullWidth: this.props.fullWidth,
      searchable: this.props.searchable !== false,
      showOnFocus: this.props.showOnFocus,
      labelKey: this.props.labelKey ? this.props.labelKey : "value",
      valueKey: this.props.valueKey ? this.props.valueKey : "key",
      newOptionEnable: this.props.newOptionEnable || false,
      onBlurSelect: this.props.onBlurSelect ? this.props.onBlurSelect : undefined,
      allowEditSelected: this.props.allowEditSelected,
      autocomplete: this.props.autocomplete,
    };
  }

  render() {
    const props: any = this.toProps();
    const RenderSelectWrapper = props.newOptionEnable ? Select.AsyncCreatable : Select.Async;
    const isShowError = showError(props);
    const showValuesOnInit: boolean = !props.searchable || props.showOnFocus;

    const inner = props => (
      <div>
        {props.label &&
        <FieldLabel
          name={props.input.name}
          label={props.label}
          required={props.required}
          className={classnames({["with-hint"]: Boolean(props.hint)})}
        >
            <HintText {...props}/>
        </FieldLabel>
        }
        <span className={classnames({
          valid: !isShowError,
          validate: isShowError,
          'has-error': isShowError,
        })}>
          <RenderSelectWrapper
            inputProps={{autoComplete: props.autocomplete || 'off', autoCorrect: 'off', spellCheck: 'off'}}
            className={classnames({'t-error': isShowError, 'always-full-width': props.fullWidth})}
            placeholder={props.placeholder}
            name={name}
            labelKey={props.labelKey}
            valueKey={props.valueKey}
            searchable={props.searchable}
            optionRenderer={this.props.optionRenderer}
            valueRenderer={this.props.valueRenderer}
            clearable={false}
            value={this.props.returnType === 'object' ? this.normalizeObject(props.input.value) : props.input.value && props.input}
            loadOptions={input => input.length > 0 && !showValuesOnInit ? this.loadOptionsDebounce(input) : this.loadOptions(input)}
            options={this.props.options}
            onBlur={this.onBlur}
            onChange={this.onChange}
            onOpen={props.allowEditSelected ? () => this.onOpen(props.input.value) : undefined}
            promptTextCreator={label => `${label} `}
            ref={c => {
              this.selectComponent = c;
            }}

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

