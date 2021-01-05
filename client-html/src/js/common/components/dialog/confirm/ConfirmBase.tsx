/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Common confirm dialog
 * */

import React from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import { ConfirmState } from "../../../../model/common/Confirm";

const ConfirmBase: React.FunctionComponent<ConfirmState> = props => {
  const {
    open,
    onCancel,
    title,
    confirmMessage,
    onConfirm,
    cancelButtonText,
    confirmButtonText,
    onCancelCustom,
    confirmCustomComponent
  } = props;

  return (
    <Dialog open={open} onClose={onCancel}>
      {title && <DialogTitle id="alert-dialog-title" className="heading">{title}</DialogTitle>}
      {confirmMessage && (
        <DialogContent>
          <DialogContentText id="alert-dialog-description">{confirmMessage}</DialogContentText>
        </DialogContent>
      )}

      <DialogActions>
        <Button onClick={onCancelCustom ? onCancelCustom : onCancel} color="primary">
          {cancelButtonText}
        </Button>
        {onConfirm && !confirmCustomComponent && (
          <Button onClick={() => onConfirm()} variant="contained" color="primary">
            {confirmButtonText}
          </Button>
        )}
        {confirmCustomComponent && confirmCustomComponent}
      </DialogActions>
    </Dialog>
  );
};

export default ConfirmBase;
