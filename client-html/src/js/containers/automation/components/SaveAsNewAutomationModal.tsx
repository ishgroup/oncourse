/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import MuiButton from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Typography from "@material-ui/core/Typography/Typography";
import React from "react";
import { InjectedFormProps, reduxForm } from "redux-form";
import Button from "../../../common/components/buttons/Button";
import FormField from "../../../common/components/form/form-fields/FormField";
import { validateKeycode } from "../utils";

interface Props {
  opened: any;
  onClose: any;
  onSave: any;
  hasNameField?: boolean;
  validateNameField?: any;
}

const SaveAsNewAutomationModal = React.memo<Props & InjectedFormProps>(
  ({
     opened, handleSubmit, invalid, onClose, onSave, hasNameField, validateNameField
    }) => (
      <Dialog open={opened} onClose={onClose} maxWidth="md" scroll="body">
        <form autoComplete="off" onSubmit={handleSubmit(onSave)}>
          <DialogTitle>Your new custom Automation requires a unique key code</DialogTitle>

          <DialogContent>
            <Typography variant="body2" color="textSecondary" className="pb-2" noWrap>
              Your new custom automation requires you to create a unique identifier.
              {' '}
              <br />
              Please type a combination of numbers and full stops.
            </Typography>

            <FormField
              type="text"
              label="Keycode"
              name="keyCode"
              validate={validateKeycode}
              required
              fullWidth
            />

            {hasNameField
              && (
              <FormField
                type="text"
                label="Name"
                name="name"
                validate={validateNameField || undefined}
                required
                fullWidth
              />
          )}

          </DialogContent>

          <DialogActions className="p-3">
            <MuiButton color="primary" onClick={onClose}>
              Cancel
            </MuiButton>

            <Button color="primary" type="submit" disabled={invalid}>
              Save
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    )
);

export default reduxForm<any, Props>({
  form: "SaveAsNewAutomationForm"
})(SaveAsNewAutomationModal);
