import React, {FunctionComponent} from "react";
import ReactMde from "react-mde";
import {ReactMdeProps} from "react-mde/lib/definitions/components/ReactMde";

const ReactMarkdown = require("react-markdown");

interface Props {
  value?: string;
  onChange?: (html) => void;
}

const MarkdownEditor = props => {

  const {value, onChange} = props;

  const [selectedTab, setSelectedTab] = React.useState("write" as ReactMdeProps["selectedTab"]);

  return (
    <ReactMde
      value={value}
      onChange={val => onChange(val)}
      selectedTab={selectedTab}
      onTabChange={setSelectedTab}
      generateMarkdownPreview={ markdown =>
        Promise.resolve(<ReactMarkdown source={markdown} />)
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
