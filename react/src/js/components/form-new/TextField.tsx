import * as React from "react";
import Wrapper from "./Wrapper";
import {WrappedFieldInputProps, WrappedFieldProps} from "redux-form";
import {inputFrom} from "./FieldsUtils";

interface Props{
  name: string,
  label: string,
  type: string,
  required: boolean,
}

class TextField extends React.Component<any, any> {

  render() {
    const {type} = this.props;
    const input:WrappedFieldInputProps = inputFrom(this.props);
    return (
      <Wrapper {...this.props}>
        <input className="input-fixed contact-field" type={type} {...input} onFocus={input.onFocus} onBlur={input.onBlur} onChange={input.onChange}/>
      </Wrapper>
    )
  }
}

export default TextField;



