import React, {FunctionComponent, useState} from "react";
import ReactMde from "react-mde";
import {ReactMdeProps} from "react-mde/lib/definitions/components/ReactMde";
import "react-mde/lib/styles/css/react-mde-all.css";
import WysiwygEditor from "./WysiwygEditor";
import {HeaderCommand} from "./components/MdeHeadersDropdown";
import iconGetter from "./components/iconGetter";
import {UnorderedListCommand} from "./components/ListCommand";

interface Props {
  value?: string;
  onChange?: (html) => void;
}

const MarkdownEditor = props => {
  const {value, onChange} = props;

  const [selectedTab, setSelectedTab] = useState<ReactMdeProps["selectedTab"]>("write");

  return (
    <ReactMde
      value={value}
      commands={{
        "custom-header": HeaderCommand,
        "bullet-list-custom": UnorderedListCommand
      }}
      getIcon={iconGetter}
      toolbarCommands={[
        ["custom-header","bold","italic","link","bullet-list-custom","ordered-list"]]
      }
      onChange={onChange}
      selectedTab={selectedTab}
      onTabChange={setSelectedTab}
      generateMarkdownPreview={markdown =>
        Promise.resolve(
          <WysiwygEditor
            value={markdown}
            onChange={onChange}
          />,
        )
      }
      childProps={{
        writeButton: {
          tabIndex: -1,
        },
      }}
    />
  );
};

export default MarkdownEditor as FunctionComponent<Props>;
