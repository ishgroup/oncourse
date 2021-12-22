import React, {useState} from 'react';
import clsx from "clsx";
import {ButtonBase, Menu, MenuItem, Tooltip} from "@material-ui/core";
import {withStyles, createStyles} from "@material-ui/core/styles";
import FullscreenIcon from '@material-ui/icons/Fullscreen';
import FullscreenExitIcon from '@material-ui/icons/FullscreenExit';
import {CONTENT_MODES} from "../../containers/content/constants";
import {ContentMode} from "../../model";
import {getEditorModeLabel} from "../../containers/content/utils";
import {AppTheme} from "../../styles/themeInterface";

const styles = (theme: AppTheme) => createStyles({
  contentModeWrapper: {
    position: "absolute",
    right: 10,
    top: 11,
    zIndex: 1000,
  },
  contentMode: {
    maxWidth: "85px",
    border: 0,
    boxShadow: "none",
    backgroundColor: "black",
    color: "white",
    padding: "2px",
    fontSize: "9px",
    "&:hover": {
      backgroundColor: theme.palette.primary.main,
      color: theme.palette.primary.contrastText,
    },
  },
  fullscreenButton: {
    "&:hover": {
      color: theme.palette.primary.main,
    },
  },
});

interface Props {
  classes: any;
  contentModeId?: ContentMode;
  moduleId?: number;
  setContentMode?: (moduleId, modeId) => void;
  enabledFullscreen?: boolean;
  fullscreen?: boolean;
  onFullscreen?: () => void;
}

const ContentModeSwitch = (props: Props) => {
  const {classes, contentModeId, moduleId, setContentMode, enabledFullscreen, fullscreen, onFullscreen} = props;
  const [modeMenu, setModeMenu] = useState(null);

  const modeMenuOpen = e => {
    setModeMenu(e.currentTarget);
  };

  const modeMenuClose = () => {
    setModeMenu(null);
  };

  const onClickMenuItem = (moduleId, modeId) => {
    setContentMode(moduleId, modeId);
    modeMenuClose();
  };

  return (
    <div className={clsx("d-flex align-items-center", classes.contentModeWrapper)}>
      <Tooltip title="Change content mode" disableFocusListener>
        <ButtonBase
          onClick={modeMenuOpen}
          aria-owns={modeMenu ? "mode-menu" : null}
          className={classes.contentMode}
        >
          {getEditorModeLabel(contentModeId)}
        </ButtonBase>
      </Tooltip>
      {enabledFullscreen && (
        <ButtonBase
          onClick={e => onFullscreen()}
          className={clsx("ml-1", classes.fullscreenButton)}
        >
          {fullscreen ? <FullscreenExitIcon /> : <FullscreenIcon />}
        </ButtonBase>
      )}
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