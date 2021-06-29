import * as React from "react";
import Wrapper from "./Wrapper";
import {inputFrom} from "./FieldsUtils";

const Checkbox = (props) => {
  const input = inputFrom(props);
  return (
    <Wrapper {...props}>
      <input type="checkbox" {...input}/>
    </Wrapper>
  );
}

export default Checkbox;

