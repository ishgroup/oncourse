import * as React from "react";

export function FieldLabel({name, label, required, children, className}: FieldLabelProps) {
  const requiredSign = required ? '<em title="This field is required">*</em>' : '';

  return (
    <label htmlFor={name} className={className}>
      <span dangerouslySetInnerHTML={{__html: label + requiredSign}}/>
      {children}
    </label>
  );
}

interface FieldLabelProps {
  name: string;
  label: string;
  required: boolean;
  className?: string;
  children?: any;
}
