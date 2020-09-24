import React, {FunctionComponent} from 'react';
import AceEditor from 'react-ace';
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

const Editor = props => {

  const {value, onChange, mode = "html"} = props;

  return (
    <AceEditor
      mode={mode}
      theme="tomorrow"
      fontSize={14}
      showPrintMargin={true}
      showGutter={true}
      highlightActiveLine={true}
      width="100%"
      enableBasicAutocompletion={true}
      wrapEnabled={true}
      enableLiveAutocompletion={true}
      setOptions={{
        enableSnippets: true,
        showLineNumbers: true,
        tabSize: 2,
      }}
      editorProps={{$blockScrolling: Infinity}}
      value={value}
      onChange={val => onChange(val)}
    />
  );
};

export default Editor as FunctionComponent<Props>;
