import React, {FunctionComponent} from 'react';
import AceEditor from 'react-ace';
import "ace-builds/src-noconflict/mode-html";
import "ace-builds/src-noconflict/mode-textile";
import "ace-builds/src-noconflict/theme-tomorrow";
import "ace-builds/src-noconflict/ext-language_tools";
import "ace-builds/src-noconflict/snippets/html";
import "ace-builds/src-noconflict/snippets/textile";
const ace = require('ace-builds/src-noconflict/ace');
ace.config.set("basePath", "https://cdn.jsdelivr.net/npm/ace-builds@1.4.12/src-noconflict/");
ace.config.setModuleUrl('ace/mode/javascript_worker', "https://cdn.jsdelivr.net/npm/ace-builds@1.4.3/src-noconflict/worker-javascript.js");

interface Props {
  height?: string;
  value?: string;
  mode?: string;
  onChange?: (html) => void;
}

const HtmlEditor = props => {

  const {height = "500px", value, onChange, mode = "html"} = props;

  return (
    <AceEditor
      mode={mode}
      height={height}
      theme="tomorrow"
      fontSize={14}
      showPrintMargin={true}
      showGutter={false}
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

export default HtmlEditor as FunctionComponent<Props>;
