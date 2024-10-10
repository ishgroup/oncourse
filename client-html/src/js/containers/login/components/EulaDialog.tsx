/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General
 *  Public License for more details.
 */

import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import React from 'react';
import { withStyles } from 'tss-react/mui';

const styles = () => ({

  dialogContent: {
    overflow: "hidden",
    height: window.innerHeight
  },
  iframe: {
    border: 0,
    width: "100%",
    height: "100%",
  },
  acceptButton: {
    marginLeft: "16px"
  }
});

const EulaDialog = props => {
  const [open, setOpen] = React.useState(true);
  const [bottom, setBottom] = React.useState(false);

  const handleClose = () => {
    setOpen(false);
    setBottom(false);
    props.onCancel();
  };

  const handleEulaAccept = () => {
    setOpen(false);
    props.onAccept();
  };

  const getScrollToBottomMessage = () => {
    const eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
    const eventer = window[eventMethod];
    const messageEvent = eventMethod === "attachEvent" ? "onmessage" : "message";

    eventer(messageEvent, e => {
      if (e.data === "userScrollToButtom" || e.message === "userScrollToButtom") {
        setBottom(true);
      }
    });
  };

  const descriptionElementRef = React.useRef<HTMLElement>(null);
  React.useEffect(() => {
    if (open) {
      const { current: descriptionElement } = descriptionElementRef;
      if (descriptionElement !== null) {
        descriptionElement.focus();
      }
    }
  }, [open]);

  return (
    <div>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="eula-dialog-title"
        aria-describedby="eula-dialog-description"
        maxWidth="md"
        fullWidth={true}
      >
        <DialogTitle id="eula-dialog-title">EULA Agreement</DialogTitle>
        <DialogContent
          id="EulaContent"
          className={props.classes.dialogContent}
          dividers={true}
        >
          <iframe
            id="license_eula"
            className={props.classes.iframe}
            src={props.eulaUrl}
            title="EULA Agreement"
            onLoad={getScrollToBottomMessage}
          />
        </DialogContent>
        <DialogActions>
          <form>
            <div className={props.classes.buttonsContainer}>
              <Button
                color="primary"
                classes={{
                  root: props.classes.declineButton
                }}
                onClick={handleClose}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                className={props.classes.acceptButton}
                disabled={!bottom}
                classes={{
                  root: props.classes.loginButton,
                  disabled: props.classes.loginButtonDisabled
                }}
                onClick={handleEulaAccept}
              >
                I accept these conditions
              </Button>
            </div>
          </form>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default withStyles(EulaDialog, styles);