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

import {
  Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle
} from "@material-ui/core";
import React from "react";
import { createStyles, withStyles } from "@material-ui/core/styles";
import Button from "../../../common/components/buttons/Button";

const styles = () => createStyles({
  scrollWrapper: {
    height: "100%",
    width: "100%",
    overflow: "hidden",
  },

  secondScrollWrapper: {
    height: "100%",
    width: "100%",
  }
});

const EulaDialog = props => {
  const [open, setOpen] = React.useState(true);
  const [canScroll, setCanScroll] = React.useState("no");
  const [redirectCount, setRedirectCount] = React.useState(0);
  const [bottom, setBottom] = React.useState(false);

  const handleClose = () => {
    setOpen(false);
    setBottom(false);
    props.onCancel();
  };

  const handleAccept = () => {
    setOpen(false);
    props.onAccept();
  };

  const handleScroll = e => {
    if (!bottom && canScroll !== "no") {
      const isScrollToBottom = e.target.scrollHeight - e.target.scrollTop === e.target.clientHeight;
      setBottom(isScrollToBottom);
    }
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
        aria-labelledby="scroll-dialog-title"
        aria-describedby="scroll-dialog-description"
        fullScreen={true}
      >
        <DialogTitle id="scroll-dialog-title">EULA Agreement</DialogTitle>
        <DialogContent
          id="EulaContent"
          dividers={true}
          style={{ overflow: "hidden" }}
        >
          <div className={props.classes.scrollWrapper}>
            <div
              className={props.classes.secondScrollWrapper}
              style={{ overflow: canScroll }}
              onScroll={handleScroll}
            >
              <DialogContentText
                id="scroll-dialog-description"
                tabIndex={-1}
              >
                <iframe
                  id="license_eula"
                  src={props.eulaUrl}
                  title="EULA Agreement"
                  width="100%"
                  height={window.innerHeight}
                  frameBorder="0"
                  scrolling={canScroll}
                  onLoad={() => {
                    setRedirectCount(redirectCount + 1);
                    if (redirectCount >= 1) {
                      setCanScroll("auto");
                    }
                  }}
                />
              </DialogContentText>
            </div>
          </div>
        </DialogContent>
        <DialogActions>
          <form>
            <Button onClick={handleClose}>Cancel</Button>
            <Button
              type="submit"
              variant="contained"
              disabled={!bottom}
              onClick={handleAccept}
            >
              I accept these conditions
            </Button>
          </form>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default withStyles(styles)(EulaDialog);