/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from 'react';
import {
  Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle
} from '@mui/material';
import { ConfirmState } from '../../../models/Confirm';

const ConfirmBase = (
  {
    open,
    onCancel,
    title,
    confirmMessage,
    onConfirm,
    cancelButtonText,
    confirmButtonText,
    onCancelCustom,
    confirmCustomComponent
  }: ConfirmState
) => (
  <Dialog open={open} onClose={onCancel}>
    {title && <DialogTitle>{title}</DialogTitle>}
    {confirmMessage && (
    <DialogContent>
      <DialogContentText>{confirmMessage}</DialogContentText>
    </DialogContent>
    )}

    <DialogActions>
      <Button onClick={onCancelCustom || onCancel} color="primary">
        {cancelButtonText}
      </Button>
      {onConfirm && !confirmCustomComponent && (
      <Button onClick={onConfirm} variant="contained" color="primary">
        {confirmButtonText}
      </Button>
      )}
      {confirmCustomComponent && confirmCustomComponent}
    </DialogActions>
  </Dialog>
);

export default ConfirmBase;
