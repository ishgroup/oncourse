/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import { createStyles, darken, withStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import { AppTheme } from "../../../model/common/Theme";

const styles = (theme: AppTheme) => createStyles({
    root: {
      height: "fit-content",
      width: "fit-content",
      position: "relative",
      "&:hover $backdrop$active": {
        opacity: 1
      }
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
        duration: theme.transitions.duration.standard,
        easing: theme.transitions.easing.easeInOut
      })
    },
    image: {
      height: "100%",
      width: "auto",
      maxHeight: theme.spacing(30),
      maxWidth: theme.spacing(30)
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
    active: {}
  });

interface Props {
  data: string;
  label?: string;
  actions?: {
    actionLabel?: string;
    onAction?: any;
    disabled?: boolean
  }[];
  classes?: any;
}

const FilePreview: React.FC<Props> = ({
 label, actions, data, classes
}) => (
  <>
    {label && (
    <Typography variant="caption" color="textSecondary" component="div" className="pb-1">
      {label}
    </Typography>
      )}

    {data ? (
      <div className={classes.root}>
        <div className={clsx(classes.backdrop, actions && classes.active)}>
          <div className="flex-column">
            {actions
                && actions.map((a, i) => (
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
                ))}
          </div>
        </div>
        <img className={classes.image} src={`data:image/png;base64, ${data}`} alt="FilePreview" />
      </div>
      ) : (
        <Typography variant="body1" className={classes.noValue}>
          No Value
        </Typography>
      )}
  </>
  );

export default withStyles(styles)(FilePreview);
