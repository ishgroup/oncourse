/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Button from '@mui/material/Button';
import ClickAwayListener from '@mui/material/ClickAwayListener';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import Grow from '@mui/material/Grow';
import Paper from '@mui/material/Paper';
import Popper from '@mui/material/Popper';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { mapSelectItems, normalizeNumberToPositive } from 'ish-ui';
import React, { useCallback } from 'react';
import { connect } from 'react-redux';
import { getFormValues, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { SessionRepeatTypes } from '../../../../../model/entities/CourseClass';

const styles = (theme => ({
  popper: {
    zIndex: theme.zIndex.modal
  },
  paper: {
    minWidth: "340px",
    "&:after": {
      top: "50%",
      right: "0px",
      width: "10px",
      height: "22px",
      position: "absolute",
      transform: "translateY(-50%)",
      background: theme.palette.background.paper,
      content: "''",
      zIndex: 1
    }
  },
  corner: {
    right: "-6px",
    top: "50%",
    width: "12px",
    height: "12px",
    position: "absolute",
    transform: "translateY(-50%) rotate(45deg)",
    background: theme.palette.background.paper,
    boxShadow: "1px 0px 2px 0px rgba(0,0,0,0.2), 1px 0px 2px -1px rgba(0,0,0,0.12), 0px 0px 1px 0px rgba(0,0,0,0.12);"
  }
}));

const repeatTypeItems = Object.keys(SessionRepeatTypes).map(mapSelectItems);

const CopySessionDialogBase = React.memo<any>(props => {
  const {
   popupAnchorEl, onCancel, reset, onSave, values, classes, invalid
  } = props;
  const [closeDialog, setCloseDialog] = React.useState(true);

  const onSaveBase = useCallback(
    e => {
      onSave(values);
      onCancel(e);
      reset();
    },
    [values]
  );

  const handleOnClose = useCallback(() => {
    if (closeDialog) onCancel();
  }, [closeDialog]);

  const onRepeatTypeChange = useCallback(() => {
    setCloseDialog(false);
    setInterval(() => {
      setCloseDialog(true);
    }, 1000);
  }, []);

  return (
    <Popper
      open={Boolean(popupAnchorEl)}
      anchorEl={popupAnchorEl}
      className={classes.popper}
      placement="left"
      transition
    >
      {({ TransitionProps }) => (
        <ClickAwayListener onClickAway={handleOnClose}>
          <Grow {...TransitionProps} timeout={200}>
            <form>
              <Paper className={classes.paper} elevation={8}>
                <DialogContent className="overflow-hidden">
                  <div className="heading pb-2">{$t('repeat')}</div>
                  <Typography variant="body2" color="inherit" component="div" className="pb-1">
                    {$t('repeat_this_session')}
                    {" "}
                    <FormField
                      type="number"
                      name="repeatTimes"
                      min="1"
                      max="99"
                      step="1"
                      normalize={normalizeNumberToPositive}
                      placeholder="0"
                      required
                      inline
                    />
                    {" "}
                    {$t('times2')}
                  </Typography>

                  <Typography variant="body2" color="inherit" component="div" className="pb-2">
                    {$t('repeat_every')}
                    {" "}
                    <FormField
                      type="select"
                      name="repeatType"
                      items={repeatTypeItems}
                      onInnerValueChange={onRepeatTypeChange}
                      inline
                    />
                  </Typography>
                </DialogContent>

                <DialogActions>
                  <Button color="primary" onClick={onCancel}>
                    {$t('cancel')}
                  </Button>
                  <Button color="primary" onClick={onSaveBase} disabled={invalid} variant="contained">
                    {$t('create_sessions')}
                  </Button>
                </DialogActions>
                <div className={classes.corner} />
              </Paper>
            </form>
          </Grow>
        </ClickAwayListener>
      )}
    </Popper>
  );
});

export default reduxForm<any, any>({
  form: "CopySessionForm",
  initialValues: {
    repeatTimes: 1
  }
})(
  connect(
    state => ({
      values: getFormValues("CopySessionForm")(state)
    }),
    null
  )(withStyles(CopySessionDialogBase, styles))
);
