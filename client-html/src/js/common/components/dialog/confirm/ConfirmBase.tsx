/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Common confirm dialog
 * */

import React from "react";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
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
      {title && <DialogTitle id="alert-dialog-title">{title}</DialogTitle>}
      {confirmMessage && (
        <DialogContent>
          <DialogContentText id="alert-dialog-description">{confirmMessage}</DialogContentText>
        </DialogContent>
      )}

      <DialogActions>
        <Button onClick={onCancelCustom || onCancel} color="primary">
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
