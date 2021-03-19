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
import DialogTitle from "@material-ui/core/DialogTitle";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { stubFunction } from "../../../../../common/utils/common";
import EditInPlaceDateTimeField from "../../../../../common/components/form/form-fields/EditInPlaceDateTimeField";

const AssessmentSubmissionModal = (
  {
    modalProps,
    name,
    tutors,
    onClose,
    triggerAsyncChange,
    title,
    allSubmissionsDate,
    setAllSubmissionsDate,
  }
) => {
  const type = modalProps[0];
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
        <DialogTitle className="normalHeading p-0 mb-2">{title}</DialogTitle>
        {opened && (
        <Grid container>
          <Grid item xs={6}>
            {modalProps[2] === "all" ? (
              <EditInPlaceDateTimeField
                type="date"
                label={`${type} date`}
                input={{
                  onChange: setAllSubmissionsDate,
                  onFocus: stubFunction,
                  onBlur: stubFunction,
                  value: allSubmissionsDate
                }}
                meta={{}}
              />
            ) : (
              <FormField
                type="date"
                name={`${name}.${type === "Marked" ? "markedOn" : "submittedOn"}`}
                label={`${type} date`}
                onChange={triggerAsyncChange}
              />
            )}

          </Grid>
          <Grid item xs={6}>
            {type === "Marked" && modalProps[2] !== "all" && (
              (
                <FormField
                  type="select"
                  selectValueMark="contactId"
                  selectLabelMark="tutorName"
                  name={`${name}.markedById`}
                  label="Assessor"
                  items={tutors}
                  onChange={triggerAsyncChange}
                  allowEmpty
                />
              )
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
