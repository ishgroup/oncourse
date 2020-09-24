import React, {useEffect, useState} from 'react';
import {Button, FormGroup, Label, Input} from 'reactstrap';
import classnames from 'classnames';
import Editor from "../../../../../common/components/Editor";
import {BlockState} from "../reducers/State";
import {CONTENT_MODES, DEFAULT_CONTENT_MODE_ID} from "../../../constants";
import {addContentMarker} from "../../../utils";
import "react-mde/lib/styles/css/react-mde-all.css";
import MarkdownEditor from "../../../../../common/components/MarkdownEditor";
import WysiwygEditor from "../../../../../common/components/WysiwygEditor";
import marked from "marked";
import TurndownService from "turndown";
const ReactMarkdown = require("react-markdown");

interface Props {
  block: BlockState;
  onSave: (blockId, html) => void;
}

// custom event to reinitialize site plugins on editing content
const pluginInitEvent = new Event("plugins:init");

const turnDownService = new TurndownService();

const Block: React.FC<Props> = props => {

  const {block, onSave} = props;
  const [editMode, setEditMode] = useState(false);
  const [draftContent, setDraftContent] = useState("");
  const [CKEditorData, setCKEditorData] = useState("");
  const [contentMode, setContentMode] = useState(DEFAULT_CONTENT_MODE_ID);

  useEffect(() => {
    document.dispatchEvent(pluginInitEvent);

    const contentMode = block.contentMode || DEFAULT_CONTENT_MODE_ID;

    setContentMode(contentMode);
    setDraftContent(block.content || "");
    if (contentMode === "md") {
      setCKEditorData(marked(block.content || ""));
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
    let newContent;

    if (contentMode === "wysiwyg") {
      const mdText = turnDownService.turndown(CKEditorData);
      newContent = addContentMarker(mdText, "md");
    } else {
      newContent = addContentMarker(draftContent, contentMode);
    }

    setEditMode(false);
    onSave(block.id, newContent);
  };

  const handleCancel = () => {
    setEditMode(false);
    setDraftContent(block.content || "");
  };

  useEffect(() => {
    if (!editMode && block.content) {
      document.dispatchEvent(pluginInitEvent);
    }
  },        [editMode, block, block && block.content]);

  const onCKEditorChange = ( event, editor ) => {
    const data = editor.getData();
    setCKEditorData(data);
  };

  const onContentModeChange = e => {
    const v = e.target.value;
    setContentMode(v);
  };

  const renderEditor = () => {
    switch (contentMode) {
      case "md": {
        return (
          <MarkdownEditor
            value={draftContent}
            onChange={setDraftContent}
          />
        );
      }
      case "wysiwyg": {
        return (
          <WysiwygEditor
            value={CKEditorData}
            onChange={onCKEditorChange}
          />
        );
      }
      case "textile":
      case "html":
      default: {
        return (
          <Editor
            value={draftContent}
            onChange={val => onChangeArea(val)}
            mode={contentMode}
          />
        );
      }
    }
  };

  return (
    <div>

      {editMode && <>
        <div className={
          classnames({"editor-wrapper" : true, "ace-wrapper": contentMode === "html" || contentMode === "textile"})
        }>
          <div className="content-mode-wrapper">
            <Input
                type="select"
                name="contentMode"
                id="contentMode"
                className="content-mode"
                placeholder="Content mode"
                value={contentMode}
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
                <Button onClick={() => handleCancel()} color="link">Cancel</Button>
                <Button onClick={() => handleSave()} color="primary">Save</Button>
            </FormGroup>
        </div>
      </>}

      {!editMode &&
      <div onClick={e => onClickArea(e)}>
        <div className={classnames("editor-area", {'editor-area--empty': !block.content})}>
          {
            contentMode === "md" || contentMode === "wysiwyg"
              ? <ReactMarkdown source={block.content} />
              : block.content
          }
        </div>
      </div>
      }

    </div>
  );
};

export default Block;
