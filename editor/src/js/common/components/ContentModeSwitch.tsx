import React, {useState} from 'react';
import {MenuItem, Select} from "@material-ui/core";
import {CONTENT_MODES} from "../../containers/content/constants";
import {ContentMode} from "../../model";
import {withStyles} from "@material-ui/core/styles";

const styles: any = theme => ({
  contentModeWrapper: {
    position: "absolute",
    right: "10px",
    top: "5px",
    zIndex: 1000,
  },
})

interface Props {
  classes: any;
  contentModeId?: ContentMode;
  moduleId?: number;
  setContentMode?: (moduleId, modeId) => void;
}

const ContentModeSwitch = (props: Props) => {
  const {classes, contentModeId, moduleId, setContentMode} = props;

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const toggleDropdown = () => setDropdownOpen(prevState => !prevState);

  return (
    <div className={classes.contentModeWrapper}>
      <Select
        defaultValue={contentModeId || null}
      >
        {CONTENT_MODES.map(mode => (
          <MenuItem
            key={mode.id}
            value={mode.id}
            onClick={() => {
              setContentMode(moduleId, mode.id);
            }}
          >
            {mode.title}
          </MenuItem>
        ))}
      </Select>

      {/*<Dropdown isOpen={dropdownOpen} toggle={toggleDropdown}>*/}
      {/*  <DropdownToggle className="content-toggle">*/}
      {/*    {getEditorModeLabel(contentModeId)}*/}
      {/*  </DropdownToggle>*/}
      {/*  <DropdownMenu>*/}
      {/*    {CONTENT_MODES.map(mode => (*/}
      {/*      <DropdownItem*/}
      {/*        key={mode.id}*/}
      {/*        id={mode.id}*/}
      {/*        onClick={() => {*/}
      {/*          setContentMode(moduleId, mode.id);*/}
      {/*        }}*/}
      {/*      >*/}
      {/*        {mode.title}*/}
      {/*      </DropdownItem>*/}
      {/*    ))}*/}
      {/*  </DropdownMenu>*/}
      {/*</Dropdown>*/}
    </div>
  );
};

export default (withStyles(styles)(ContentModeSwitch));