/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import SettingsIcon from "@mui/icons-material/Settings";
import SpeedDial from "@mui/material/SpeedDial";
import SpeedDialAction from "@mui/material/SpeedDialAction";
import SpeedDialIcon from "@mui/material/SpeedDialIcon";
import clsx from "clsx";
import { AppBarAction, makeAppStyles, ShowConfirmCaller } from "ish-ui";
import React, { useCallback, useState } from "react";
import { connect } from "react-redux";
import { showConfirm } from "../../actions";

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

const AppBarActions = React.memo<Props>(({actions, showConfirm}) => {
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
        icon={<SpeedDialIcon icon={<SettingsIcon color="inherit"/>}/>}
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

export default connect(null, mapDispatchToProps)(AppBarActions);