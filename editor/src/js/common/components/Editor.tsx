import React from 'react';
import AceEditor from 'react-ace';

import 'brace/mode/html';
import 'brace/snippets/html';
import 'brace/theme/tomorrow';
import 'brace/ext/language_tools';

const snippetManager = window['ace'].acequire("snippetManager", () => console.log('asdadsad'))

interface Props {
  value?: string;
  onChange?: (html) => void;
}

export class Editor extends React.Component<Props, any> {
  render() {
    const {value, onChange} = this.props;

    console.log(snippetManager);

    return (
      <div>
        <AceEditor
          mode="html"
          theme="tomorrow"
          fontSize={14}
          showPrintMargin={true}
          showGutter={true}
          highlightActiveLine={true}
          width="100%"
          setOptions={{
            enableBasicAutocompletion: true,
            enableLiveAutocompletion: true,
            enableSnippets: true,
            showLineNumbers: true,
            tabSize: 2,
          }}
          editorProps={{$blockScrolling: true}}
          value={value}
          onChange={val => onChange(val)}
        />
      </div>
    );
  }
}

