import React, {useEffect, useState} from 'react';
import {Button, FormGroup, Input} from 'reactstrap';
import classnames from 'classnames';
import Editor from "../../../../../common/components/editor/HtmlEditor";
import {BlockState} from "../reducers/State";
import {CONTENT_MODES, DEFAULT_CONTENT_MODE_ID} from "../../../constants";
import {addContentMarker} from "../../../utils";
import MarkdownEditor from "../../../../../common/components/editor/MarkdownEditor";
import marked from "marked";
import {ContentMode} from "../../../../../model";

interface Props {
  block: BlockState;
  onSave: (blockId, html) => void;
  setContentMode?: (id: number, contentMode: ContentMode) => any;
}

// custom event to reinitialize site plugins on editing content
const pluginInitEvent = new Event("plugins:init");

const Block: React.FC<Props> = ({block, onSave, setContentMode}) => {
  const [editMode, setEditMode] = useState(false);
  const [draftContent, setDraftContent] = useState("");

  useEffect(() => {
    document.dispatchEvent(pluginInitEvent);

    setDraftContent(block.content || "");
    if (block.contentMode === "md") {
      setDraftContent(marked(block.content || ""));
    }
  },        []);

  const onClickArea = e => {
    e.preventDefault();
    setEditMode(true);
    setDraftContent(block.content || "");
  };

  const onChangeArea = val => {
    setDraftContent(val);
  };

  const handleSave = () => {
    setEditMode(false);
    onSave(block.id, addContentMarker(draftContent, block.contentMode));
  };

  const handleCancel = () => {
    setEditMode(false);
    setDraftContent(block.content || "");
  };

  useEffect(() => {
    if (!editMode && block.content) {
      document.dispatchEvent(pluginInitEvent);
    }
  },[editMode, block, block && block.content]);

  const onContentModeChange = e => {
    setContentMode(block.id,e.target.value);
  };

  const renderEditor = () => {
    switch (block.contentMode) {
      case "md": {
        return (
          <MarkdownEditor
            value={draftContent}
            onChange={setDraftContent}
          />
        );
      }
      case "textile":
      case "html":
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
      {editMode && <>
        <div className={
          classnames({"editor-wrapper" : true, "ace-wrapper": block.contentMode === "html" || block.contentMode === "textile"})
        }>
          <div className="content-mode-wrapper">
            <Input
                type="select"
                name="contentMode"
                id="contentMode"
                className="content-mode"
                placeholder="Content mode"
                value={block.contentMode || DEFAULT_CONTENT_MODE_ID}
                onChange={e => onContentModeChange(e)}
            >
              {CONTENT_MODES.map(mode => (
                <option key={mode.id} value={mode.id}>{mode.title}</option>
              ))}
            </Input>
          </div>
          {renderEditor()}
        </div>
        <div className="mt-4">
            <FormGroup>
                <Button onClick={handleCancel} color="link">Cancel</Button>
                <Button onClick={handleSave} color="primary">Save</Button>
            </FormGroup>
        </div>
      </>}

      {!editMode &&
        <div onClick={onClickArea}>
          <div className={classnames("editor-area", {'editor-area--empty': !block.content})}>
            {block.content}
          </div>
        </div>
      }
    </div>
  );
};

export default Block;
