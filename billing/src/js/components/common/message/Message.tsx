/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import { withStyles } from "@material-ui/core/styles";
import Snackbar from "@material-ui/core/Snackbar";
import IconButton from "@material-ui/core/IconButton";
import Alert from "@material-ui/lab/Alert";
import CloseIcon from "@material-ui/icons/Close";

const styles = theme => ({
  close: {
    color: theme.palette.primary.contrastText,
    width: theme.spacing(4),
    height: theme.spacing(4),
    padding: theme.spacing(0.5)
  },
  success: {
    maxWidth: "unset"
  },
  autoWidth: {
    [theme.breakpoints.down("md")]: {
      flexGrow: "unset",
      borderRadius: `${theme.shape.borderRadius}px`
    }
  },
  autoWidthRoot: {
    [theme.breakpoints.down("md")]: {
      left: "24px",
      right: "auto",
      bottom: "24px"
    }
  },
  fullScreenMessage: {
    maxHeight: "calc(100vh - 90px)",
    overflow: "auto",
    marginRight: theme.spacing(-2),
    paddingRight: theme.spacing(2)
  }
});

interface Props {
  opened: boolean;
  text: string;
  clearMessage: any;
  isSuccess?: boolean;
  persist?: boolean;
  classes?: any;
}

class Message extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      success: props.isSuccess,
      text: props.text,
      persist: props.persist
    };
  }

  componentDidUpdate(prevProps) {
    if (this.props.opened !== prevProps.opened) {
      this.setState({
        success: this.props.opened ? this.props.isSuccess : this.state.success,
        text: this.props.opened ? this.props.text : this.state.text,
        persist: this.props.opened ? this.props.persist : this.state.persist
      });
    }
  }

  handleClose = () => {
    if (!this.state.persist) {
      this.props.clearMessage();
    }
  };

  render() {
    const { classes, opened, clearMessage } = this.props;

    const { success, text, persist } = this.state;

    return (

      <Snackbar
        open={opened}
        anchorOrigin={{
            vertical: "bottom",
            horizontal: "left"
          }}
        classes={{
            root: classes.autoWidthRoot
          }}
        ContentProps={{
            classes: {
              root: clsx(classes.autoWidth, {
                [classes.success]: success
              }),
              message: persist ? classes.fullScreenMessage : undefined
            }
          }}
        ClickAwayListenerProps={{
            onClickAway: this.handleClose
          }}
        autoHideDuration={persist ? null : 6000}
        onClose={this.handleClose}
        disableWindowBlurListener={persist}
      >
        <Alert
          variant="filled"
          severity={success ? "success" : "error"}
          classes={{
            message: "text-pre-wrap"
          }}
          action={(
            <IconButton
              key="close"
              aria-label="Close"
              color="inherit"
              className={classes.close}
              onClick={clearMessage}
            >
              <CloseIcon />
            </IconButton>
)}
        >
          {text}
        </Alert>
      </Snackbar>
    );
  }
}

export default withStyles(styles as any)(Message);
