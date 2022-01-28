/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {useEffect, useState} from 'react';
import marked from "marked";
import {BlockState} from "../reducers/State";
import {addContentMarker} from "../../../utils";
import {ContentMode} from "../../../../../model";
import BlockEditor from "./BlockEditor";

interface Props {
  block: BlockState;
  onSave: (blockId, html) => void;
  setContentMode?: (id: number, contentMode: ContentMode) => any;
}

// custom event to reinitialize site plugins on editing content
const pluginInitEvent = new Event('plugins:init');

const Block: React.FC<Props> = ({
  block, onSave, setContentMode
}) => {
  const [draftContent, setDraftContent] = useState('');

  useEffect(() => {
    document.dispatchEvent(pluginInitEvent);
    setDraftContent(block.contentMode === 'html' ? marked(block.content || '') : (block.content || ''));
  }, [block]);

  const handleSave = () => {
    onSave(block.id, addContentMarker(draftContent, block.contentMode));
  };

  const handleCancel = () => {
    setDraftContent(block.content || '');
  };

  useEffect(() => {
    if (block.content) {
      document.dispatchEvent(pluginInitEvent);
    }
  }, [block, block && block.content]);

  return (
    <div className="block-wrapper h-100">
      <BlockEditor
        mode={block.contentMode}
        content={draftContent}
        setContent={setDraftContent}
        moduleId={block.id}
        setContentMode={setContentMode}
        handleSave={handleSave}
        handleCancel={handleCancel}
      />
    </div>
  );
};

export default Block;
