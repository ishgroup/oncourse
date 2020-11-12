import * as React from "react";
import { change, Field as FormField } from "redux-form";
import { AuthDigitField } from "./AuthCodeDigitField";

const Field: any = FormField;

class AuthCodeFieldRenderer extends React.Component<any, any> {
  private inputNodes = [];

  getInputNode = node => {
    this.inputNodes.push(node);
  };

  // auto focus on next empty field
  focusNextNode = value => {
    if (value || value === 0) {
      const firstEmpty = this.inputNodes.find(node => !node.value);
      if (firstEmpty) firstEmpty.focus();
    }
  };

  // switch focus on previous field on backspace press
  focusPrevNode = (code, value, index) => {
    if (index !== 0 && code === 8 && value !== 0 && !value) {
      this.inputNodes[index - 1].focus();
    }
  };

  onCodePaste = e => {
    e.preventDefault();
    if (e.clipboardData) {
      const { dispatch } = this.props;
      const code = e.clipboardData.getData("text");
      if (code && code.length) {
        const codeDigits = code.split("");
        if (codeDigits.length === 6 && !Number.isNaN(Number(code))) {
          dispatch(change("LoginForm", "authCodeDigits", codeDigits));
        }
      }
    }
  };

  render() {
    const { fields } = this.props;

    return (
      <div>
        {fields.map((item, index) => (
          <Field
            name={item}
            key={index}
            onChange={(e, v) => this.focusNextNode(v)}
            onKeyUp={e => this.focusPrevNode(e.keyCode, fields.get(index), index)}
            inputRef={this.getInputNode}
            component={AuthDigitField}
            autoFocus={index === 0}
            normalize={value => (value && !isNaN(Number(value)) ? +value : null)}
            autoComplete="off"
            onPaste={this.onCodePaste}
          />
        ))}
      </div>
    );
  }
}

export default AuthCodeFieldRenderer;
