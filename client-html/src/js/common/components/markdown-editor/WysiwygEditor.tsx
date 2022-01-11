import React, { useRef } from "react";
import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';
import EssentialsPlugin from '@ckeditor/ckeditor5-essentials/src/essentials';
import AutoformatPlugin from '@ckeditor/ckeditor5-autoformat/src/autoformat';
import SourceEditing from '@ckeditor/ckeditor5-source-editing/src/sourceediting';
import BoldPlugin from '@ckeditor/ckeditor5-basic-styles/src/bold';
import ItalicPlugin from '@ckeditor/ckeditor5-basic-styles/src/italic';
import HeadingPlugin from '@ckeditor/ckeditor5-heading/src/heading';
import LinkPlugin from '@ckeditor/ckeditor5-link/src/link';
import ListPlugin from '@ckeditor/ckeditor5-list/src/list';
import ParagraphPlugin from '@ckeditor/ckeditor5-paragraph/src/paragraph';
import Markdown from '@ckeditor/ckeditor5-markdown-gfm/src/markdown';
import Table from '@ckeditor/ckeditor5-table/src/table';
import TableToolbar from '@ckeditor/ckeditor5-table/src/tabletoolbar';
import { CKEditor } from '@ckeditor/ckeditor5-react';
import ReactDOM from "react-dom";
import CodeIcon from '@mui/icons-material/Code';
import CodeOffIcon from '@mui/icons-material/CodeOff';
import { removeContentMarker } from "./utils";

const SourceEditingSwitch = ({ on }: { on?: boolean }) => (
  <div className="ck_source_edit_custom">
    {on ? <CodeOffIcon className="ck_code_icon_custom" /> : <CodeIcon className="ck_code_icon_custom" />}
    <span className="ck ck-tooltip ck-tooltip_s"><span className="ck ck-tooltip__text">Edit markdown source</span></span>
  </div>
);

function customizeSourceEditing( editor ) {
  editor.plugins.get("SourceEditing").on('change:isSourceEditingMode', (e, n, on ) => {
    const sourceEdit = document.querySelector('.ck-source-editing-button');
    ReactDOM.render(
      <SourceEditingSwitch on={on} />,
      sourceEdit
    );
  });
}

const config = {
  plugins: [
    Markdown,
    SourceEditing,
    EssentialsPlugin,
    AutoformatPlugin,
    BoldPlugin,
    ItalicPlugin,
    HeadingPlugin,
    LinkPlugin,
    ListPlugin,
    ParagraphPlugin,
    Table,
    TableToolbar,
  ],
  extraPlugins: [customizeSourceEditing],
  toolbar: [
    'heading',
    'bold',
    'italic',
    'link',
    'bulletedList',
    'numberedList',
    'insertTable',
    'sourceEditing',
  ],
  table: {
    defaultHeadings: { rows: 1 },
    contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells']
  },
  heading: {
    options: [
      { model: 'paragraph', title: 'Paragraph', class: 'ck-heading_paragraph' },
      {
       model: 'heading1', view: 'h1', title: 'Heading 1', class: 'ck-heading_heading1',
      },
      {
       model: 'heading2', view: 'h2', title: 'Heading 2', class: 'ck-heading_heading2',
      },
      {
        model: 'heading3', view: 'h3', title: 'Heading 3', class: 'ck-heading_heading3',
      },
      {
        model: 'heading4', view: 'h4', title: 'Heading 4', class: 'ck-heading_heading4',
      },
    ],
  },
};

interface Props {
  value?: string;
  onChange?: (val: any) => void;
}

const WysiwygEditor: React.FC<Props> = ({
  value,
  onChange
}) => {
  const editorRef = useRef(null);
  
  const onReady = editor => {
    editorRef.current = editor;

    // customize source edit button switch
    const sourceEdit = document.querySelector('.ck-source-editing-button');
    ReactDOM.render(
      <SourceEditingSwitch />,
      sourceEdit,
      () => {
        sourceEdit.classList.remove('invisible');
      }
    );
  };

  return (
    <CKEditor
      editor={ClassicEditor}
      config={config}
      data={removeContentMarker(value)}
      onChange={(e, editor) => onChange(editor.getData())}
      onReady={onReady}
    />
  );
};

export default WysiwygEditor;
