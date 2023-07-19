/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useState } from "react";
import SpeedDial from "@mui/material/SpeedDial";
import SpeedDialAction from "@mui/material/SpeedDialAction";
import SpeedDialIcon from "@mui/material/SpeedDialIcon";
import SettingsIcon from "@mui/icons-material/Settings";
import { connect } from "react-redux";
import { showConfirm } from "../../actions";
import { ShowConfirmCaller } from "../../../model/common/Confirm";
import { AppBarAction } from "../../../model/common/AppBar";
import { makeAppStyles } from "../../styles/makeStyles";
import clsx from "clsx";


const useStyles = makeAppStyles(theme => ({
  root: {
    zIndex: theme.zIndex.drawer
  },
  mainButton: {
    boxShadow: "none",
    color: theme.palette.primary.main,
    backgroundColor: "transparent",
    "&:hover": {
      backgroundColor: "transparent",
    }
  },
  actions: {},
  directionLeft: {
    "& $actions": {
      paddingRight: theme.spacing(4.5)
    }
  }
}));

interface Props {
  actions: AppBarAction[];
  showConfirm?: ShowConfirmCaller;
}

const AppBarActions = React.memo<Props>(({ actions, showConfirm }) => {
  const [opened, setOpened] = useState<boolean>(false);
  
  const classes = useStyles();

  const onClickAction = useCallback((onConfirm, confirmMessage, confirmButtonText) => {
    if (confirmMessage) {
      showConfirm({
        onConfirm,
        confirmMessage,
        confirmButtonText,
      });
      setOpened(false);
      return;
    }
    onConfirm();
    setOpened(false);
  }, []);

  const handleAddFieldClick = useCallback(() => {
    setOpened(true);
  }, []);

  const handleAddFieldClose = useCallback(() => {
    setOpened(false);
  }, []);

  return (
    <>
      <SpeedDial
        direction="left"
        ariaLabel="SpeedDial actions"
        open={opened}
        icon={<SpeedDialIcon icon={<SettingsIcon color="inherit" />} />}
        onClick={handleAddFieldClick}
        onClose={handleAddFieldClose}
        onMouseEnter={handleAddFieldClick}
        onMouseLeave={handleAddFieldClose}
        FabProps={{
          size: "medium",
          classes: {
            root: classes.mainButton
          }
        }}
        classes={{
          root: clsx("align-items-center", classes.root),
          actions: classes.actions,
          directionLeft: classes.directionLeft
        }}
      >
        {actions.map(item => (
          <SpeedDialAction
            tooltipPlacement="bottom"
            key={item.tooltip}
            icon={item.icon}
            tooltipTitle={item.tooltip}
            FabProps={{
              disabled: item.disabled
            }}
            onClick={() => onClickAction(item.action, item.confirmText, item.confirmButtonText)}
          />
        ))}
      </SpeedDial>
    </>
  );
});

const mapDispatchToProps = dispatch => ({
  showConfirm: props => dispatch(showConfirm(props))
});

export default connect<any, any, any>(null, mapDispatchToProps)(AppBarActions);