/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import Popper from "@material-ui/core/Popper";
import Grow from "@material-ui/core/Grow";
import Paper from "@material-ui/core/Paper";
import DialogContent from "@material-ui/core/DialogContent";
import { getFormValues, reduxForm } from "redux-form";
import DialogActions from "@material-ui/core/DialogActions";
import Button from "@material-ui/core/Button";
import { createStyles, withStyles } from "@material-ui/core/styles";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import Typography from "@material-ui/core/Typography";
import { connect } from "react-redux";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { normalizeNumber } from "../../../../../common/utils/numbers/numbersNormalizing";
import { SessionRepeatTypes } from "../../../../../model/entities/CourseClass";
import { mapSelectItems } from "../../../../../common/utils/common";

const styles = createStyles(theme => ({
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
 popupAnchorEl, onCancel, reset, onSave, values, classes
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
                  <div className="heading pb-2">Repeat</div>
                  <Typography variant="body2" color="inherit" component="div" className="pb-1">
                    Repeat this session
                    <FormField
                      type="number"
                      name="repeatTimes"
                      min="1"
                      max="99"
                      step="1"
                      normalize={normalizeNumber}
                      props={{
                        formatting: "inline"
                      }}
                    />
                    times
                  </Typography>

                  <Typography variant="body2" color="inherit" component="div" className="pb-2">
                    Repeat every
                    <FormField
                      type="select"
                      name="repeatType"
                      props={{
                        formatting: "inline",
                        disabledTab: true,
                        onInnerValueChange: onRepeatTypeChange
                      }}
                      items={repeatTypeItems}
                    />
                  </Typography>
                </DialogContent>

                <DialogActions>
                  <Button color="primary" onClick={onCancel}>
                    Cancel
                  </Button>
                  <Button color="primary" onClick={onSaveBase} variant="contained">
                    Create Sessions
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
  form: "CopySessionForm"
})(
  connect(
    state => ({
      values: getFormValues("CopySessionForm")(state)
    }),
    null
  )(withStyles(styles)(CopySessionDialogBase))
);
