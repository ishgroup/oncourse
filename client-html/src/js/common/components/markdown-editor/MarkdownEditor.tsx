import React, { FunctionComponent } from "react";
import WysiwygEditor from "./WysiwygEditor";

interface Props {
  value?: string;
  onChange?: (html) => void;
}

const MarkdownEditor = props => {
  const { value, onChange } = props;

  return (
    <WysiwygEditor
      value={value}
      onChange={onChange}
    />
  );
};

export default MarkdownEditor as FunctionComponent<Props>;
