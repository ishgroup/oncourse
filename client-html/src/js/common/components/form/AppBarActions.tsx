/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useState } from "react";
import { withStyles } from "@material-ui/core/styles";
import SpeedDial from "@material-ui/lab/SpeedDial";
import SpeedDialAction from "@material-ui/lab/SpeedDialAction";
import SpeedDialIcon from "@material-ui/lab/SpeedDialIcon";
import SettingsIcon from "@material-ui/icons/Settings";
import { connect } from "react-redux";
import { showConfirm } from "../../actions";
import { ShowConfirmCaller } from "../../../model/common/Confirm";

const styles = theme =>
  ({
    mainButton: {
      boxShadow: "none",
      "&:hover": {
        backgroundColor: "inherit"
      }
    },
    actions: {},
    directionLeft: {
      "& $actions": {
        paddingRight: theme.spacing(4.5)
      }
    }
  } as any);

interface FormSettingsAction {
  action: any;
  icon: React.ReactNode;
  tooltip: string;
  confirmText?: string;
  confirmButtonText?: string;
  disabled?: boolean;
  tooltipError?: boolean;
}

interface Props {
  actions: FormSettingsAction[];
  classes?: any;
  showConfirm?: ShowConfirmCaller;
}

const AppBarActions = React.memo<Props>(({ actions, classes, showConfirm }) => {
  const [opened, setOpened] = useState<boolean>(false);

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
        icon={<SpeedDialIcon icon={<SettingsIcon />} />}
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
          root: "align-items-center",
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

export default connect<any, any, any>(null, mapDispatchToProps)(withStyles(styles)(AppBarActions));
