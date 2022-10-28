/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { InjectedFormProps, reduxForm } from "redux-form";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import FormField from "../../../common/components/form/formFields/FormField";
import { validateKeycode } from "../utils";

interface Props {
  opened: any;
  onClose: any;
  onSave: any;
  validateNameField: any;
}

const SaveAsNewAutomationModal = React.memo<Props & InjectedFormProps>(
  ({
     opened, handleSubmit, invalid, onClose, onSave, validateNameField
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

            <FormField
              type="text"
              label="Name"
              name="name"
              validate={validateNameField || undefined}
              className="mt-2"
              required
              fullWidth
            />

          </DialogContent>

          <DialogActions className="p-3">
            <Button color="primary" onClick={onClose}>
              Cancel
            </Button>

            <Button variant="contained" color="primary" type="submit" disabled={invalid}>
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