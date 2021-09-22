/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import Snackbar from '@mui/material/Snackbar';
import IconButton from '@mui/material/IconButton';
import Alert from '@mui/lab/Alert';
import CloseIcon from '@mui/icons-material/Close';
import { makeAppStyles } from '../../../styles/makeStyles';

const useStyles = makeAppStyles()((theme) => ({
  close: {
    color: theme.palette.primary.contrastText,
    width: theme.spacing(4),
    height: theme.spacing(4),
    padding: theme.spacing(0.5)
  },
  success: {
    maxWidth: 'unset'
  },
  autoWidth: {
    [theme.breakpoints.down('xl')]: {
      flexGrow: 'unset',
      borderRadius: `${theme.shape.borderRadius}px`
    }
  },
  autoWidthRoot: {
    [theme.breakpoints.down('xl')]: {
      left: '24px',
      right: 'auto',
      bottom: '24px'
    }
  },
  fullScreenMessage: {
    maxHeight: 'calc(100vh - 90px)',
    overflow: 'auto',
    marginRight: theme.spacing(-2),
    paddingRight: theme.spacing(2)
  }
}));

interface Props {
  opened: boolean;
  text: string;
  clearMessage: any;
  isSuccess?: boolean;
  persist?: boolean;
}

const Message = ({
  opened,
  text,
  clearMessage,
  isSuccess,
  persist
}: Props) => {
  const handleClose = () => {
    if (!persist) {
      clearMessage();
    }
  };

  const { classes, cx } = useStyles();

  return (
    <Snackbar
      open={opened}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'left'
      }}
      classes={{
        root: classes.autoWidthRoot
      }}
      ContentProps={{
        classes: {
          root: cx(classes.autoWidth, {
            [classes.success]: isSuccess
          }),
          message: persist ? classes.fullScreenMessage : undefined
        }
      }}
      ClickAwayListenerProps={{
        onClickAway: handleClose
      }}
      autoHideDuration={persist ? null : 6000}
      onClose={handleClose}
      disableWindowBlurListener={persist}
    >
      <Alert
        variant="filled"
        severity={isSuccess ? 'success' : 'error'}
        classes={{
          message: 'text-pre-wrap'
        }}
        action={(
          <IconButton
            key="close"
            aria-label="Close"
            color="inherit"
            className={classes.close}
            onClick={clearMessage}
            size="large"
          >
            <CloseIcon />
          </IconButton>
)}
      >
        {text}
      </Alert>
    </Snackbar>
  );
};

export default Message;
