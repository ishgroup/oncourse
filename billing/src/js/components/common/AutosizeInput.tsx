import React, { useEffect, useRef } from 'react';
import ContentEditable from 'react-contenteditable';

const AutosizeInput = ({
  onChange, value, error = false, disabled = false
}) => {
  const ref = useRef<HTMLDivElement>();

  useEffect(() => {
    ref.current.setAttribute('aria-invalid', String(error));
  }, [error]);

  return (
    <ContentEditable
      className="input"
      innerRef={ref}
      html={value}
      onChange={onChange}
      disabled={disabled}
    />
  );
};

export default AutosizeInput;
