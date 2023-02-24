/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
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
import Image from '@ckeditor/ckeditor5-image/src/image';
import LinkImage from '@ckeditor/ckeditor5-link/src/linkimage';
import ImageCaption from '@ckeditor/ckeditor5-image/src/imagecaption';
import ImageStyle from '@ckeditor/ckeditor5-image/src/imagestyle';
import { CKEditor } from '@ckeditor/ckeditor5-react';
import { createRoot } from 'react-dom/client';
import CodeIcon from '@mui/icons-material/Code';

const SourceEditingSwitch = () => (
  <div className="ck_source_edit_custom">
    <CodeIcon className="ck_code_icon_custom" />
  </div>
);

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
    ImageStyle,
    ImageCaption,
  ],
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
  wysiwygRef?: any;
}

const WysiwygEditor: React.FC<Props> = ({
  value,
  onChange,
  wysiwygRef
}) => {

  const onReady = editor => {
    wysiwygRef.current = editor;

    const sourceEdit = document.querySelector<any>('.ck-source-editing-button');
    sourceEdit.dataset.ckeTooltipText = 'Edit source';
    sourceEdit.dataset.ckeTooltipPosition = 's';

    const root = createRoot(sourceEdit);

    root.render(
      <SourceEditingSwitch />
    );

    editor.plugins.get("SourceEditing").on('change:isSourceEditingMode', () => {
      const sourceEdit = document.querySelector('.ck-source-editing-button');
      if (sourceEdit) {
        root.render(
          <SourceEditingSwitch/>,
        );
      }
    });
  };

  const onChangeHandler = (e, editor) => onChange(editor.getData());

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