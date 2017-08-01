import * as React from "react";
import Wrapper from "./Wrapper";
import {WrappedFieldInputProps} from "redux-form";
import {inputFrom} from "./FieldsUtils";
import MaskedTextInput from "react-text-mask";

interface Props {
  name: string;
  label: string;
  type: string;
  required: boolean;
  mask: string;
}

export class MaskedTextField extends React.Component<any, any> {

  render() {
    const {type, children, autoComplete, placeholderChar} = this.props;
    const input: WrappedFieldInputProps = inputFrom(this.props);

    return (
      <Wrapper {...this.props}>
        <MaskedTextInput
          className="input-fixed contact-field"
          autoComplete={autoComplete}
          type={type} {...input}
          id={input.name}
          onFocus={input.onFocus}
          onBlur={input.onBlur}
          onChange={input.onChange}
          placeholderChar={placeholderChar || '_'}
          mask={this.props.mask}
        />
        {children}
      </Wrapper>
    );
  }
}

