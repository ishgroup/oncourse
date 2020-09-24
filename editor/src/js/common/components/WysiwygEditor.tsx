import React, {FunctionComponent, useEffect, useState} from "react";
import CKEditor from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';

const CKEditorConfig = {
  removePlugins:
  [
    "EasyImage",
    "Image",
    "ImageCaption",
    "ImageStyle",
    "ImageToolbar",
    "ImageUpload",
    "MediaEmbed",
    "TableToolbar",
    "Table",
  ],
};

interface Props {
  value?: string;
  onChange?: (event: any, editor: any) => void;
}

const WysiwygEditor = props => {

  const {value, onChange} = props;

  return (
    <CKEditor
      editor={ClassicEditor}
      config={CKEditorConfig}
      data={value}
      onInit={ editor => {
      // You can store the "editor" and use when it is needed.
        console.log( 'Editor is ready to use!', editor );
      } }
      onChange={onChange}
    />
  );
};

export default WysiwygEditor as FunctionComponent<Props>;
