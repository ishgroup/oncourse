import React, {useEffect, useRef, useState} from "react";
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
import CKEditor from '@ckeditor/ckeditor5-react';

interface Props {
  value?: string;
  onChange?: (val: any) => void;
  setParentHeight?: (val: any) => void;
  defaultHeight?: number;
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
  heading: {
    options: [
      {
        model: 'paragraph', title: 'Paragraph', class: 'ck-heading_paragraph',
      },
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

const WysiwygEditor: React.FC<Props> = ({
  value,
  onChange,
  defaultHeight,
  setParentHeight
}) => {
  const editorRef = useRef(null);
  const [previewHeight, setPreviewHeight] = useState(defaultHeight);

  function storeDimensions(element) {
    element.srcElement.textHeight = element.srcElement.clientHeight;
  }

  function onResize(element) {
    if (element.srcElement.textHeight > 20 && element.srcElement.textHeight !== element.srcElement.clientHeight) {
      setPreviewHeight(element.srcElement.clientHeight);
      setParentHeight(element.srcElement.clientHeight);
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
