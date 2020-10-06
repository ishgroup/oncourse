import React, {useEffect, useRef, useState} from "react";
import CKEditor from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';
import EssentialsPlugin from '@ckeditor/ckeditor5-essentials/src/essentials';
import AutoformatPlugin from '@ckeditor/ckeditor5-autoformat/src/autoformat';
import BoldPlugin from '@ckeditor/ckeditor5-basic-styles/src/bold';
import ItalicPlugin from '@ckeditor/ckeditor5-basic-styles/src/italic';
import HeadingPlugin from '@ckeditor/ckeditor5-heading/src/heading';
import LinkPlugin from '@ckeditor/ckeditor5-link/src/link';
import ListPlugin from '@ckeditor/ckeditor5-list/src/list';
import ParagraphPlugin from '@ckeditor/ckeditor5-paragraph/src/paragraph';
import Markdown from '@ckeditor/ckeditor5-markdown-gfm/src/markdown';

interface Props {
  value?: string;
  onChange?: (val: any) => void;
}

const config = {
  plugins: [
    Markdown,
    EssentialsPlugin,
    AutoformatPlugin,
    BoldPlugin,
    ItalicPlugin,
    HeadingPlugin,
    LinkPlugin,
    ListPlugin,
    ParagraphPlugin
  ],
  toolbar: [
    'heading',
    'bold',
    'italic',
    'link',
    'bulletedList',
    'numberedList',
  ],
};

const WysiwygEditor: React.FC<Props> = ({value, onChange}) => {
  const editorRef = useRef(null);

  const [previewHeight, setPreviewHeight] = useState(200);

  function storeDimensions(element) {
    element.srcElement.textHeight = element.srcElement.clientHeight;
  }

  function onResize(element) {
    if (element.srcElement.textHeight > 20 && element.srcElement.textHeight !== element.srcElement.clientHeight) {
      setPreviewHeight(element.srcElement.clientHeight);
    }
  }

  useEffect(() => {
    const contentNode = document.querySelector(".ck-content") as HTMLElement;

    if (contentNode) {
      contentNode.onmousedown = storeDimensions;
      contentNode.onmouseup = onResize;
    }
  }, []);

  useEffect(() => {
    if (editorRef.current) {
      editorRef.current.editing.view.change(writer => {
        const root = editorRef.current.editing.view.document.getRoot();

        writer.setStyle(
          "height",
          `${previewHeight + 1}px`,
          root,
        );
      });
    }
  },[previewHeight, editorRef.current]);

  return <CKEditor
    editor={ClassicEditor}
    config={config}
    data={value}
    onChange={(e,editor) => onChange(editor.getData())}
    onInit={editor => {
      editorRef.current = editor;
    }}
  />;
};


export default WysiwygEditor;
