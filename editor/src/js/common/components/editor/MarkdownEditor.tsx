import React, {FunctionComponent, useEffect, useState} from "react";
import ReactMde from "react-mde";
import {ReactMdeProps} from "react-mde/lib/definitions/components/ReactMde";
import "react-mde/lib/styles/css/react-mde-all.css";
import WysiwygEditor from "./WysiwygEditor";
import {HeaderCommand} from "./components/MdeHeadersDropdown";
import iconGetter from "./components/iconGetter";
import {UnorderedListCommand} from "./components/ListCommand";
import {ItalicCommandCustom} from "./components/ItalicCommand";
import {BoldCommandCustom} from "./components/BoldCommand";

interface Props {
  height?: number;
  value?: string;
  onChange?: (html) => void;
}

const MarkdownEditor = props => {
  const {value, onChange, height} = props;

  const [selectedTab, setSelectedTab] = useState<ReactMdeProps["selectedTab"]>("write");
  const [previewHeight, setPreviewHeight] = useState(100);

  function storeDimensions(element) {
    element.srcElement.textHeight = element.srcElement.clientHeight;
  }

  function onResize(element) {
    if (element.srcElement.textHeight > 20 && element.srcElement.textHeight !== element.srcElement.clientHeight) {
      setPreviewHeight(element.srcElement.clientHeight);
    }
  }

  useEffect(() => {
    const contentNode = document.querySelector("textarea.mde-text") as HTMLElement;

    if (contentNode) {
      contentNode.onmousedown = storeDimensions;
      contentNode.onmouseup = onResize;
    }
  }, []);

  return (
    <ReactMde
      value={value}
      maxEditorHeight={height}
      commands={{
        "header-custom": HeaderCommand,
        "bullet-list-custom": UnorderedListCommand,
        "italic-custom": ItalicCommandCustom,
        "bold-custom": BoldCommandCustom
      }}
      getIcon={iconGetter}
      toolbarCommands={[
        ["header-custom","bold-custom","italic-custom","link","bullet-list-custom","ordered-list"]]
      }
      onChange={onChange}
      selectedTab={selectedTab}
      onTabChange={setSelectedTab}
      generateMarkdownPreview={markdown =>
        Promise.resolve(
          <WysiwygEditor
            value={markdown}
            onChange={onChange}
            // defaultHeight={previewHeight}
            // defaultHeight={height}
            height={height}
            setParentHeight={setPreviewHeight}
          />,
        )
      }
      childProps={{
        writeButton: {
          tabIndex: -1,
        },
        textArea: {
          style: {
            height: height + "px",
          },
        },
      }}
    />
  );
};

export default MarkdownEditor as FunctionComponent<Props>;
