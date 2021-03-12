
/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import Dialog from "@material-ui/core/Dialog";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import MuiButton from "@material-ui/core/Button";
import { Grid } from "@material-ui/core";
import FormField from "../../../../../common/components/form/form-fields/FormField";

const AssessmentSubmissionModal = (
  {
    modalProps,
    item,
    tutors,
    onClose
  }
) => {
  const type = modalProps[0];
  const name = `${item}.submissions[${modalProps[1]}]`;

  const opened = Boolean(modalProps.length);

  return (
    <Dialog
      open={opened}
      onClose={onClose}
      classes={{
      paper: "overflow-visible"
    }}
      fullWidth
      disableAutoFocus
      disableEnforceFocus
      disableRestoreFocus
    >
      <DialogContent>
        {opened && (
        <Grid container>
          <Grid item xs={6}>
            <FormField
              type="date"
              name={`${name}.${type === "Marked" ? "markedOn" : "submittedOn"}`}
              label={`${type} date`}
              parse={value => {
                if (!value) {
                  const today = new Date();
                  today.setHours(0, 0, 0, 0);
                  return today.toISOString();
                }
                return value;
              }}
              required
            />
          </Grid>
          <Grid item xs={6}>
            {type === "Marked" && (
            <FormField
              type="select"
              selectValueMark="contactId"
              selectLabelMark="tutorName"
              name={`${name}.submittedById`}
              label="Assessor"
              items={tutors}
              allowEmpty
            />
            )}
          </Grid>
        </Grid>
        )}
      </DialogContent>

      <DialogActions>
        <MuiButton
          color="primary"
          onClick={onClose}
        >
          Close
        </MuiButton>
      </DialogActions>
    </Dialog>
);
};

export default AssessmentSubmissionModal;
