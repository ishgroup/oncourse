import React, {useCallback, useState} from 'react';
import {ButtonBase, Menu, MenuItem, Tooltip} from "@material-ui/core";
import {withStyles, createStyles} from "@material-ui/core/styles";
import FullscreenIcon from '@material-ui/icons/Fullscreen';
import FullscreenExitIcon from '@material-ui/icons/FullscreenExit';
import {CONTENT_MODES} from "../../containers/content/constants";
import {ContentMode} from "../../model";
import {getEditorModeLabel} from "../../containers/content/utils";

const styles = () => createStyles({
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
});

interface Props {
  classes: any;
  contentModeId?: ContentMode;
  moduleId?: number;
  setContentMode?: (moduleId, modeId) => void;
  enabledFullscreen?: boolean;
  onFullscreen?: (fullscreen: boolean) => void;
}

const ContentModeSwitch = (props: Props) => {
  const {classes, contentModeId, moduleId, setContentMode, enabledFullscreen, onFullscreen} = props;
  const [modeMenu, setModeMenu] = useState(null);
  const [fullscreen, setFullscreen] = useState<boolean>(false);

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

  const handleFullscreen = useCallback(() => {
    setFullscreen(!fullscreen);
    if (onFullscreen) onFullscreen(!fullscreen);
  }, [fullscreen]);

  return (
    <div className={classes.contentModeWrapper}>

      {enabledFullscreen && (
        <ButtonBase
          onClick={handleFullscreen}
          className="mr-1"
        >
          {fullscreen ? <FullscreenExitIcon /> : <FullscreenIcon />}
        </ButtonBase>
      )}

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