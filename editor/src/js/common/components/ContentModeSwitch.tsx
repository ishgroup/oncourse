import React, {useState} from 'react';
import {ButtonBase, Menu, MenuItem, Tooltip} from "@material-ui/core";
import {withStyles} from "@material-ui/core/styles";
import clsx from "clsx";
import {CONTENT_MODES} from "../../containers/content/constants";
import {ContentMode} from "../../model";
import {getEditorModeLabel} from "../../containers/content/utils";

const styles: any = theme => ({
  contentModeWrapper: {
    position: "absolute",
    right: "16px",
    top: "16px",
    zIndex: 1000,
  },
  contentMode: {
  maxWidth: "85px",
  border: 0,
  boxShadow: "none",
  backgroundColor: "black",
  color: "white",
  padding: "2px",
  fontSize: "9px"
}
})

interface Props {
  classes: any;
  contentModeId?: ContentMode;
  moduleId?: number;
  setContentMode?: (moduleId, modeId) => void;
}

const ContentModeSwitch = (props: Props) => {
  const {classes, contentModeId, moduleId, setContentMode} = props;
  const [modeMenu, setModeMenu] = useState(null);

  const modeMenuOpen = e => {
    setModeMenu(e.currentTarget);
  };

  const modeMenuClose = () => {
    setModeMenu(null);
  };

  const onClickMenuItem = (moduleId, modeId) => {
    setContentMode(moduleId, modeId);
    modeMenuClose()

  };

  return (
    <div className={clsx(classes.contentModeWrapper)}>
      <Tooltip title="Change content mode" disableFocusListener>
        <ButtonBase
          onClick={modeMenuOpen}
          aria-owns={modeMenu ? "mode-menu" : null}
          className={classes.contentMode}
        >
          {getEditorModeLabel(contentModeId)}
        </ButtonBase>
      </Tooltip>
      <Menu
        id="theme-menu"
        anchorEl={modeMenu}
        open={Boolean(modeMenu)}
        onClose={modeMenuClose}
      >
        {CONTENT_MODES.map(mode => (
          <MenuItem
            key={mode.id}
            value={mode.id}
            onClick={() => {
              onClickMenuItem(moduleId, mode.id);
            }}
          >
            {mode.title}
          </MenuItem>
        ))}
      </Menu>
    </div>
  );
};

export default (withStyles(styles)(ContentModeSwitch));