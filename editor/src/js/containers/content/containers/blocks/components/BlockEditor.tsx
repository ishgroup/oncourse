/*
* Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
* No copying or use of this code is allowed without permission in writing from ish.
*/

import React from 'react';
import clsx from 'clsx';
import { Paper } from '@material-ui/core';
import MarkdownEditor from '../../../../../common/components/editor/MarkdownEditor';
import Editor from '../../../../../common/components/editor/HtmlEditor';
import { ContentMode } from '../../../../../model';
import ContentModeSwitch from '../../../../../common/components/ContentModeSwitch';
import CustomButton from '../../../../../common/components/CustomButton';

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

const BlockEditor: React.FC<Props> = (props) => {
  const {
    mode, content, setContent, moduleId, setContentMode, handleSave, handleCancel, position, enabledFullscreen, fullscreen, onFullscreen
  } = props;

  const editor = () => {
    switch (mode) {
      case 'md': {
        return (
          <MarkdownEditor
            height={position ? position.height - 67 - 45 : window.innerHeight - 30 - 48 - 45 - 51}
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
            height={position ? `${position.height - 67 - 44}px` : null}
            value={content}
            onChange={setContent}
            mode={mode}
          />
        );
      }
    }
  };

  return (
    <Paper className="p-1 h-100">
      <div className={
        clsx('editor-wrapper', (mode === 'html' || mode === 'textile') && 'ace-wrapper')
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
        {editor()}
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
