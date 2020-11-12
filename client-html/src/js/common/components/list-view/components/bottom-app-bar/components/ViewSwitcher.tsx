/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import IconButton from "@material-ui/core/IconButton";
import { VerticalSplit, ViewHeadline } from "@material-ui/icons";
import Tooltip from "@material-ui/core/Tooltip";
import clsx from "clsx";

export default ({
 threeColumn, switchLayout, classes, disabled
}) => (
  <Tooltip title="Change columns mode">
    <span>
      <IconButton
        color="inherit"
        classes={{
              root: clsx(classes.actionsBarButton, classes.customIconButton),
              disabled: classes.buttonDisabledOpacity
            }}
        onClick={switchLayout}
        className={classes.customIconButton}
        disabled={disabled}
      >
        <VerticalSplit
          className={threeColumn ? classes.switcherActive : classes.switcherDisabled}
          style={{ color: threeColumn ? classes.switcherActive : classes.switcherDisabled }}
        />
        <ViewHeadline
          className={threeColumn ? classes.switcherDisabled : classes.switcherActive}
          style={{ marginLeft: "-0.2em", color: threeColumn ? classes.switcherDisabled : classes.switcherActive }}
        />
      </IconButton>
    </span>
  </Tooltip>
    )
