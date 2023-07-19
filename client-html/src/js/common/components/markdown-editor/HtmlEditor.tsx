import React, { FunctionComponent } from 'react';
import AceEditor from 'react-ace';
import "ace-builds/webpack-resolver";
import "ace-builds/src-noconflict/mode-html";
import "ace-builds/src-noconflict/mode-textile";
import "ace-builds/src-noconflict/theme-tomorrow";
import "ace-builds/src-noconflict/ext-language_tools";
import "ace-builds/src-noconflict/snippets/html";
import "ace-builds/src-noconflict/snippets/textile";

interface Props {
  value?: string;
  mode?: string;
  onChange?: (html) => void;
}

const HtmlEditor = props => {
  const { value, onChange, mode = "html" } = props;

  return (
    <AceEditor
      mode={mode}
      theme="tomorrow"
      fontSize={14}
      height="200px"
      showGutter={false}
      width="100%"
      wrapEnabled
      highlightActiveLine
      enableLiveAutocompletion={mode === "html"}
      enableBasicAutocompletion={mode === "html"}
      setOptions={{
        enableSnippets: true,
        showLineNumbers: true,
        tabSize: 2,
      }}
      editorProps={{ $blockScrolling: Infinity }}
      defaultValue={value}
      onChange={onChange}
    />
  );
};

export default HtmlEditor as FunctionComponent<Props>;
