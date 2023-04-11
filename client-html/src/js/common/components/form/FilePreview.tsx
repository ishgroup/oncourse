/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import { createStyles, withStyles } from "@mui/styles";
import { darken } from "@mui/material/styles";
import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import Avatar from "@mui/material/Avatar";
import clsx from "clsx";
import { AppTheme } from "../../../model/common/Theme";

const styles = (theme: AppTheme) => createStyles({
  root: {
    height: "fit-content",
    width: "fit-content",
    position: "relative",
    fontSize: 0,
    "&:hover $backdrop$active": {
      opacity: 1
    },
    color: "#fff",
  },
  backdrop: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: theme.palette.action.active,
    position: "absolute",
    height: "100%",
    width: "100%",
    opacity: 0,
    transition: theme.transitions.create("opacity", {
      duration: theme.transitions.duration.complex,
      easing: theme.transitions.easing.easeInOut
    }),
    zIndex: 1,
  },
  rounded: {
    borderRadius: "100%",
  },
  actionButton: {
    background: theme.palette.background.paper,
    "&:hover": {
      background: darken(theme.palette.background.paper, 0.2)
    },
    marginTop: theme.spacing(1)
  },
  noValue: {
    color: theme.palette.divider,
    marginTop: theme.spacing(-1)
  },
  active: {},
  avatarRoot: {
    maxWidth: "100%",
    transition: theme.transitions.create("all", {
      duration: theme.transitions.duration.complex,
      easing: theme.transitions.easing.easeInOut
    }),
  },
});

interface Props {
  data: string;
  label?: string;
  actions?: {
    actionLabel?: string;
    onAction?: any;
    disabled?: boolean,
    icon?: any;
  }[];
  classes?: any;
  iconPlacementRow?: boolean;
  disabled?: boolean;
  avatarSize?: number;
}

const FilePreview: React.FC<Props> = ({
 label, actions, data, classes, iconPlacementRow, disabled, avatarSize
}) => {
  const size = avatarSize || 90;
  return (
    <>
      {label && (
        <Typography variant="caption" color="textSecondary" component="div" className="pb-1">
          {label}
        </Typography>
      )}

      {data ? (
        <div className={classes.root}>
          {!disabled && (
            <div className={clsx(classes.backdrop, avatarSize && classes.rounded, actions && classes.active)}>
              <div className={iconPlacementRow ? "centeredFlex" : "flex-column"}>
                {actions
                    && actions.map((a, i) => (a.icon ? (
                      <Tooltip key={i} title={a.actionLabel} placement={iconPlacementRow ? "top" : "right"}>
                        <IconButton color="inherit" size="medium" onClick={a.onAction} disabled={a.disabled}>
                          {a.icon}
                        </IconButton>
                      </Tooltip>
                    ) : (
                      <Button
                        key={i}
                        size="small"
                        variant="outlined"
                        color="secondary"
                        classes={{
                          root: classes.actionButton
                        }}
                        onClick={a.onAction}
                        disabled={a.disabled}
                      >
                        {a.actionLabel}
                      </Button>
                    )))}
              </div>
            </div>
          )}
          {avatarSize
          ? (
            <Avatar
              alt="FilePreview"
              src={`data:image/png;base64, ${data}`}
              sx={{ width: size, height: size }}
              classes={{ root: classes.avatarRoot }}
            />
            ) : (
              <img
                alt="FilePreview"
                src={`data:image/png;base64, ${data}`}
                className={classes.avatarRoot}
              />
            )}
        </div>
      ) : (
        <Typography variant="body1" className={classes.noValue}>
          No Value
        </Typography>
      )}
    </>
  );
};

export default withStyles(styles)(FilePreview);
