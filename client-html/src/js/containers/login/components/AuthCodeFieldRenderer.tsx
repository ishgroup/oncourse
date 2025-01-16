import * as React from "react";
import { change, Field as FormField } from "redux-form";
import { AuthDigitField } from "./AuthCodeDigitField";

const Field: any = FormField;

const AuthCodeFieldRenderer = ({ fields, dispatch, submitRef }) => {
  const inputNodes = [];
  
  const getInputNode = node => {
    inputNodes.push(node);
  };

  // auto focus on next empty field
  const focusNextNode = value => {
    if (value || value === 0) {
      const firstEmpty = inputNodes.find(node => !node.value);
      if (firstEmpty) {
        firstEmpty.focus();
      } else {
        setTimeout(() => {
          submitRef.click();
        }, 200);
      }
    }
  };

  // switch focus on previous field on backspace press
  const focusPrevNode = (code, value, index) => {
    if (index !== 0 && code === 8 && value !== 0 && !value) {
      inputNodes[index - 1].focus();
    }
  };

  const onCodePaste = e => {
    e.preventDefault();
    if (e.clipboardData) {
      const code = e.clipboardData.getData("text");
      if (code && code.length) {
        const codeDigits = code.split("");
        if (codeDigits.length === 6 && !Number.isNaN(Number(code))) {
          dispatch(change("LoginForm", "authCodeDigits", codeDigits));
          setTimeout(() => {
            submitRef.click();
          }, 200);
        }
      }
    }
  };

  return (
    <div className="d-flex">
      {fields.map((item, index) => (
        <Field
          name={item}
          key={index}
          onChange={(e, v) => focusNextNode(v)}
          onKeyUp={e => focusPrevNode(e.keyCode, fields.get(index), index)}
          inputRef={getInputNode}
          component={AuthDigitField}
          autoFocus={index === 0}
          normalize={value => (value && !isNaN(Number(value)) ? +value : null)}
          autoComplete="off"
          onPaste={onCodePaste}
        />
      ))}
    </div>
  );
};

export default AuthCodeFieldRenderer;