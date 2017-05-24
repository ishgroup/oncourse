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
    const {type, children} = this.props;
    const input:WrappedFieldInputProps = inputFrom(this.props);
    return (
      <Wrapper {...this.props}>
        <input className="input-fixed contact-field" type={type} {...input} id={input.name} onFocus={input.onFocus} onBlur={input.onBlur} onChange={input.onChange}/>
        {children}
      </Wrapper>
    )
  }
}

export default TextField;



