import * as React from "react";

export function FieldLabel({name, label, required}: FieldLabelProps) {
  const requiredSign = required ? '<em title="This field is required">*</em>' : '';

  return (
    <label htmlFor={name}>
      <span dangerouslySetInnerHTML={{__html: label + requiredSign}}/>
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
