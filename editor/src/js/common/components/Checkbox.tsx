import React from 'react';

interface Props {
  label?: string;
  name?: string;
  checked?: boolean;
  onChange: (e) => void;
}

export const Checkbox = (props: Props) => {
  const {label, name, checked, onChange} = props;

  return (
    <div className="md-checkbox">
      <input
        id={name}
        type="checkbox"
        name={name}
        onChange={onChange}
        checked={checked}
      />
      {label &&
        <label htmlFor={name}>{label}</label>
      }
    </div>
  );
};
