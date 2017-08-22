import * as React from "react";

export function FieldLabel({name, label, required}: FieldLabelProps) {
  return (
    <label htmlFor={name}>
      <span dangerouslySetInnerHTML={{__html: label}}/>
      {requiredText(required)}
    </label>
  );
}

function requiredText(required) {
  return (
    <span>
      {required && <em title="This field is required">*</em>}
    </span>
  );
}

interface FieldLabelProps {
  name: string;
  label: string;
  required: boolean;
}
