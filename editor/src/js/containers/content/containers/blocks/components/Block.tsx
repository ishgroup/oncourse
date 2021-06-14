import React, {useEffect, useState} from 'react';
import clsx from "clsx";
import marked from "marked";
import {withStyles} from "@material-ui/core/styles";
import Editor from "../../../../../common/components/editor/HtmlEditor";
import {BlockState} from "../reducers/State";
import {addContentMarker} from "../../../utils";
import MarkdownEditor from "../../../../../common/components/editor/MarkdownEditor";
import {ContentMode} from "../../../../../model";
import ContentModeSwitch from "../../../../../common/components/ContentModeSwitch";
import CustomButton from "../../../../../common/components/CustomButton";

const styles = theme => ({
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
const pluginInitEvent = new Event("plugins:init");

const Block: React.FC<Props> = ({block, classes, onSave, setContentMode}) => {
  const [editMode, setEditMode] = useState(true);
  const [draftContent, setDraftContent] = useState("");

  useEffect(() => {
    document.dispatchEvent(pluginInitEvent);

    setDraftContent(block.content || "");
    if (block.contentMode === "md") {
      setDraftContent(marked(block.content || ""));
    }
  }, [block]);

  const onClickArea = e => {
    e.preventDefault();
    setEditMode(true);
    setDraftContent(block.content || "");
  };

  const onChangeArea = val => {
    setDraftContent(val);
  };

  const handleSave = () => {
    // setEditMode(false);
    onSave(block.id, addContentMarker(draftContent, block.contentMode));
  };

  const handleCancel = () => {
    // setEditMode(false);
    setDraftContent(block.content || "");
  };

  // useEffect(() => {
  //   if (!editMode && block.content) {
  //     document.dispatchEvent(pluginInitEvent);
  //   }
  // }, [editMode, block, block && block.content]);

  const onContentModeChange = e => {
    setContentMode(block.id,e.target.value);
  };

  const renderEditor = () => {
    switch (block.contentMode) {
      case "md": {
        return (
          <MarkdownEditor
            height={window.innerHeight - 30 - 48 - 45 - 51}
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
          clsx("editor-wrapper", (block.contentMode === "html" || block.contentMode === "textile") && "ace-wrapper",
          )
        }>
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
      </>}

      {/*{!editMode &&*/}
      {/*  <div onClick={onClickArea}>*/}
      {/*    <div className={clsx("editor-area", !block.content && 'editor-area--empty')}>*/}
      {/*      {block.content}*/}
      {/*    </div>*/}
      {/*  </div>*/}
      {/*}*/}
    </div>
  );
};

export default (withStyles(styles)(Block));
