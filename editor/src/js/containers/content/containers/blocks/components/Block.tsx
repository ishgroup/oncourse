import React, { useEffect, useState } from 'react';
import clsx from 'clsx';
import marked from 'marked';
import { withStyles } from '@material-ui/core/styles';
import Editor from '../../../../../common/components/editor/HtmlEditor';
import { BlockState } from '../reducers/State';
import { addContentMarker } from '../../../utils';
import MarkdownEditor from '../../../../../common/components/editor/MarkdownEditor';
import { ContentMode } from '../../../../../model';
import ContentModeSwitch from '../../../../../common/components/ContentModeSwitch';
import CustomButton from '../../../../../common/components/CustomButton';

const styles = (theme) => ({
  cancelButton: {
    marginRight: theme.spacing(2),
  },
});

interface Props {
  block: BlockState;
  classes: any;
  onSave: (blockId, html) => void;
  setContentMode?: (id: number, contentMode: ContentMode) => any;
}

// custom event to reinitialize site plugins on editing content
const pluginInitEvent = new Event('plugins:init');

const Block: React.FC<Props> = ({
  block, classes, onSave, setContentMode
}) => {
  const [draftContent, setDraftContent] = useState('');

  useEffect(() => {
    document.dispatchEvent(pluginInitEvent);
    setDraftContent(block.contentMode === 'html' ? marked(block.content || '') : (block.content || ''));
  }, [block]);

  const onChangeArea = (val) => {
    setDraftContent(val);
  };

  const handleSave = () => {
    onSave(block.id, addContentMarker(draftContent, block.contentMode));
  };

  const handleCancel = () => {
    setDraftContent(block.content || '');
  };

  const renderEditor = () => {
    switch (block.contentMode) {
      case 'md': {
        return (
          <MarkdownEditor
            height={window.innerHeight - 30 - 48 - 45 - 51}
            value={draftContent}
            onChange={setDraftContent}
          />
        );
      }
      case 'textile':
      case 'html':
      default: {
        return (
          <Editor
            value={draftContent}
            onChange={onChangeArea}
            mode={block.contentMode}
          />
        );
      }
    }
  };

  return (
    <div>
      <div className={
        clsx('editor-wrapper', (block.contentMode === 'html' || block.contentMode === 'textile') && 'ace-wrapper')
      }
      >
        <ContentModeSwitch
          contentModeId={block.contentMode}
          moduleId={block.id}
          setContentMode={setContentMode}
        />
        {renderEditor()}
      </div>
      <div className="mt-3">
        <CustomButton
          styleType="cancel"
          styles={classes.cancelButton}
          onClick={handleCancel}
        >
          Cancel
        </CustomButton>
        <CustomButton
          styleType="submit"
          onClick={handleSave}
        >
          Save
        </CustomButton>
      </div>
    </div>
  );
};

export default (withStyles(styles)(Block));
