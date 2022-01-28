/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import clsx from 'clsx';
import { Paper } from '@material-ui/core';
import Editor from '../../../../../common/components/editor/HtmlEditor';
import { ContentMode } from '../../../../../model';
import ContentModeSwitch from '../../../../../common/components/ContentModeSwitch';
import CustomButton from '../../../../../common/components/CustomButton';
import { useStyles } from '../../../../../common/components/editor/style';
import WysiwygEditor from '../../../../../common/components/editor/WysiwygEditor';

interface Props {
  mode: ContentMode;
  content: string;
  setContent: (content: string) => void;
  moduleId: number;
  setContentMode?: (id: number, contentMode: ContentMode) => any;
  handleSave: () => void;
  handleCancel: () => void;
  position?: any;
  enabledFullscreen?: boolean;
  fullscreen?: boolean;
  onFullscreen?: () => void;
}

const EditorResolver = ({
  mode, content, setContent, position, fullscreen
}) => {
  switch (mode) {
    case 'md': {
      return (
        <WysiwygEditor
          value={content}
          onChange={setContent}
        />
      );
    }
    case 'textile':
    case 'html':
    default: {
      return (
        <Editor
          height={position && !fullscreen ? `${position.height - 67 - 44}px` : '100%'}
          value={content}
          onChange={setContent}
          mode={mode}
        />
      );
    }
  }
};

const BlockEditor: React.FC<Props> = (props) => {
  const {
    mode, content, setContent, moduleId, setContentMode, handleSave, handleCancel, position, enabledFullscreen, fullscreen, onFullscreen
  } = props;

  const classes = useStyles();

  return (
    <Paper className="p-1 h-100 flex-column">
      <div
        id="editorRoot"
        className={
        clsx(classes.editorArea, (mode === 'html' || mode === 'textile') && 'ace-wrapper')
      }
      >
        <ContentModeSwitch
          contentModeId={mode}
          moduleId={moduleId}
          setContentMode={setContentMode}
          enabledFullscreen={enabledFullscreen}
          onFullscreen={onFullscreen}
          fullscreen={fullscreen}
        />
        <EditorResolver
          mode={mode}
          content={content}
          setContent={setContent}
          position={position}
          fullscreen={fullscreen}
        />
      </div>

      <div className="mt-3">
        <CustomButton styleType="cancel" styles="mr-2" onClick={handleCancel}>
          Cancel
        </CustomButton>
        <CustomButton styleType="submit" onClick={handleSave}>
          Save
        </CustomButton>
      </div>
    </Paper>
  );
};

export default BlockEditor;
