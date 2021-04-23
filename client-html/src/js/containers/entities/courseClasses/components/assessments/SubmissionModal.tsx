/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useState } from "react";
import {
  Button,
  Grid,
  Dialog,
  DialogContent,
  DialogActions,
  DialogTitle
} from "@material-ui/core";
import { stubFunction } from "../../../../../common/utils/common";
import EditInPlaceDateTimeField from "../../../../../common/components/form/form-fields/EditInPlaceDateTimeField";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";

const SubmissionModal = (
  {
    modalProps,
    tutors,
    onClose,
    onSave,
    title,
    selectDefault,
    dateDefault
  }
) => {
  const type = modalProps[0];
  const opened = Boolean(modalProps.length);

  const [dateVal, setDateVal] = useState<string>(null);
  const [selectVal, setSelectVal] = useState<string>(null);

  useEffect(() => {
    setSelectVal(selectDefault);
  }, [selectDefault]);

  useEffect(() => {
    setDateVal(dateDefault);
  }, [dateDefault]);

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
        <DialogTitle className="p-0 mb-2">{title}</DialogTitle>
        {opened && (
        <Grid container>
          <Grid item xs={6}>
            <EditInPlaceDateTimeField
              type="date"
              label={`${type} date`}
              input={{
                onChange: setDateVal,
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: dateVal
              }}
              meta={{}}
            />
          </Grid>
          <Grid item xs={6}>
            {type === "Marked" && modalProps[2] !== "all" && (
              (
                <EditInPlaceField
                  label="Assessor"
                  selectValueMark="contactId"
                  selectLabelMark="tutorName"
                  input={{
                    onChange: setSelectVal,
                    onFocus: stubFunction,
                    onBlur: stubFunction,
                    value: selectVal
                  }}
                  meta={{}}
                  items={tutors}
                  allowEmpty
                  select
                />
              )
            )}
          </Grid>
        </Grid>
        )}
      </DialogContent>

      <DialogActions>
        <Button
          color="primary"
          onClick={onClose}
        >
          Close
        </Button>
        <Button
          variant="contained"
          color="primary"
          onClick={() => onSave(dateVal, selectVal)}
        >
          Save
        </Button>
      </DialogActions>
    </Dialog>
);
};

export default SubmissionModal;
