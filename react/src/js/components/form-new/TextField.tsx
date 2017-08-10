import * as React from "react";
import classnames from "classnames";
import Wrapper from "./Wrapper";
import {WrappedFieldInputProps} from "redux-form";
import {inputFrom} from "./FieldsUtils";

interface Props {
  name: string;
  label: string;
  type: string;
  required: boolean;
}

export class TextField extends React.Component<any, any> {

  render() {
    const {type, children, autoComplete, meta} = this.props;
    const input:WrappedFieldInputProps = inputFrom(this.props);

    const showError = meta.invalid && meta.touched;

    return (
      <Wrapper {...this.props}>
        <input
          className={classnames("input-fixed contact-field", {'t-error': showError})}
          autoComplete={autoComplete}
          type={type}
          {...input}
          id={input.name}
          onFocus={input.onFocus}
          onBlur={input.onBlur}
          onChange={input.onChange}
        />
        {children}
      </Wrapper>
    );
  }
}

