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
} from "@mui/material";
import { stubFunction } from "../../../../../common/utils/common";
import EditInPlaceDateTimeField from "../../../../../../ish-ui/formFields/EditInPlaceDateTimeField";
import EditInPlaceSearchSelect from "../../../../../../ish-ui/formFields/EditInPlaceSearchSelect";

const SubmissionModal = (
  {
    modalProps,
    tutors,
    onClose,
    onSave,
    title,
    selectDefault,
    dateDefault,
    dispatch
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
            disableAutoFocus
      disableEnforceFocus
      disableRestoreFocus
    >
      <DialogContent>
        <DialogTitle className="p-0 mb-2">{title}</DialogTitle>
        {opened && (
        <Grid container rowSpacing={2} columnSpacing={3}>
          <Grid item xs={12}>
            <EditInPlaceDateTimeField
              type="datetime"
              label={`${type} date`}
              input={{
                onChange: setDateVal as any,
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: dateVal
              }}
              meta={{
                dispatch
              }}
            />
          </Grid>
          <Grid item xs={12}>
            {type === "Marked" && (
              (
                <EditInPlaceSearchSelect
                  label="Assessor"
                  selectValueMark="contactId"
                  selectLabelMark="tutorName"
                  input={{
                    onChange: setSelectVal as any,
                    onFocus: stubFunction,
                    onBlur: stubFunction,
                    value: selectVal
                  }}
                  meta={{}}
                  items={tutors}
                  allowEmpty
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