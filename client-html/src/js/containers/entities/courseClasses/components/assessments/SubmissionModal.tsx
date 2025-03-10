/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid } from '@mui/material';
import $t from '@t';
import { EditInPlaceDateTimeField, EditInPlaceSearchSelect, stubFunction } from 'ish-ui';
import React, { useEffect, useState } from 'react';

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
                  label={$t('assessor')}
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
          {$t('close')}
        </Button>
        <Button
          variant="contained"
          color="primary"
          onClick={() => onSave(dateVal, selectVal)}
        >
          {$t('save2')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default SubmissionModal;