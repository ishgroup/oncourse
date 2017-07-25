import * as React from "react";
import Wrapper from "./Wrapper";
import {inputFrom} from "./FieldsUtils";

class Checkbox extends React.Component<any, any> {

  render() {
    const input = inputFrom(this.props);
    console.log(input);
    return (
      <Wrapper {...this.props}>
        <input type="checkbox" {...input}/>
      </Wrapper>
    );
  }
}

export default Checkbox;

