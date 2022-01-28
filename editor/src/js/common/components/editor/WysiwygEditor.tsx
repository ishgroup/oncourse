/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from 'react';
import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';
import EssentialsPlugin from '@ckeditor/ckeditor5-essentials/src/essentials';
import SourceEditing from '@ckeditor/ckeditor5-source-editing/src/sourceediting';
import AutoformatPlugin from '@ckeditor/ckeditor5-autoformat/src/autoformat';
import BoldPlugin from '@ckeditor/ckeditor5-basic-styles/src/bold';
import ItalicPlugin from '@ckeditor/ckeditor5-basic-styles/src/italic';
import HeadingPlugin from '@ckeditor/ckeditor5-heading/src/heading';
import LinkPlugin from '@ckeditor/ckeditor5-link/src/link';
import ListPlugin from '@ckeditor/ckeditor5-list/src/list';
import ParagraphPlugin from '@ckeditor/ckeditor5-paragraph/src/paragraph';
import Markdown from '@ckeditor/ckeditor5-markdown-gfm/src/markdown';
import Table from '@ckeditor/ckeditor5-table/src/table';
import TableToolbar from '@ckeditor/ckeditor5-table/src/tabletoolbar';
import Image from '@ckeditor/ckeditor5-image/src/image';
import LinkImage from '@ckeditor/ckeditor5-link/src/linkimage';
import ImageCaption from '@ckeditor/ckeditor5-image/src/imagecaption';
import ImageStyle from '@ckeditor/ckeditor5-image/src/imagestyle';
import { CKEditor } from '@ckeditor/ckeditor5-react';
import ReactDOM from 'react-dom';
import CodeIcon from '@material-ui/icons/Code';
import { removeContentMarker } from './utils';

const SourceEditingSwitch = () => (
  <div className="ck_source_edit_custom">
    <CodeIcon className="ck_code_icon_custom" />
    <span className="ck ck-tooltip ck-tooltip_s"><span className="ck ck-tooltip__text">Edit source</span></span>
  </div>
);

function customizeSourceEditing(editor) {
  editor.plugins.get('SourceEditing').on('change:isSourceEditingMode', () => {
    const sourceEdit = document.querySelector('.ck-source-editing-button');

    ReactDOM.render(
      <SourceEditingSwitch />,
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
    Image,
    LinkImage,
    ImageCaption,
    ImageStyle
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
    contentToolbar: ['tableColumn', 'tableRow']
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
  const onReady = (editor) => {
    const sourceEdit = document.querySelector('.ck-source-editing-button');

    if (sourceEdit) {
      ReactDOM.render(
        <SourceEditingSwitch />,
        sourceEdit
      );
    }

    if (value) {
      editor.setData(value);
    }
  };

  const onChangeHandler = useCallback((e, editor) => {
    onChange(editor.getData())
  }, []);

  return (
    <CKEditor
      editor={ClassicEditor}
      config={config}
      data={value}
      onChange={onChangeHandler}
      onReady={onReady}
    />
  );
};

export default WysiwygEditor;
