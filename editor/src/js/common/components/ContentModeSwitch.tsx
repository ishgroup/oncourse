import React, {useState} from 'react';
import {getEditorModeLabel} from "../../containers/content/utils";
import {CONTENT_MODES} from "../../containers/content/constants";
import {Dropdown, DropdownToggle, DropdownMenu, DropdownItem} from 'reactstrap';
import {ContentMode} from "../../model";

interface Props {
  contentModeId?: ContentMode;
  moduleId?: number;
  setContentMode?: (moduleId, modeId) => void;
}

export const ContentModeSwitch = (props: Props) => {
  const {contentModeId, moduleId, setContentMode} = props;

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const toggleDropdown = () => setDropdownOpen(prevState => !prevState);

  return (
    <div className="content-mode-wrapper">
      <Dropdown isOpen={dropdownOpen} toggle={toggleDropdown}>
        <DropdownToggle className="content-toggle">
          {getEditorModeLabel(contentModeId)}
        </DropdownToggle>
        <DropdownMenu>
          {CONTENT_MODES.map(mode => (
            <DropdownItem
              key={mode.id}
              id={mode.id}
              onClick={() => {
                setContentMode(moduleId, mode.id);
              }}
            >
              {mode.title}
            </DropdownItem>
          ))}
        </DropdownMenu>
      </Dropdown>
    </div>
  );
};
