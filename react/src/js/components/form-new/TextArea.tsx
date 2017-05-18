import * as React from "react";
import Wrapper from "./Wrapper";
import {inputFrom} from "./FieldsUtils";
import {WrappedFieldInputProps} from "redux-form";

class TextArea extends React.Component<any, any> {

  render() {
    const input:WrappedFieldInputProps = inputFrom(this.props);

    return (
      <Wrapper {...this.props}>
        <textarea className="form-control" onChange={input.onChange} onFocus={input.onFocus} onBlur={input.onBlur}/>
      </Wrapper>
    )
  }
}

export default TextArea;



