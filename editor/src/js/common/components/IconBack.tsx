import React from 'react';

interface Props {
  text?: string;
}

export const IconBack = (props: Props) => {
  const {text} = props;

  return (
    <span>
      <span className="icon-keyboard_arrow_left"/>
      {text &&
        <span>{` ${text}`}</span>
      }
    </span>
  );
};
