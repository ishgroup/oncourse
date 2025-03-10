/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Menu, MenuItem } from '@mui/material';
import $t from '@t';
import { EditInPlaceField, normalizeNumber, stubFunction } from 'ish-ui';
import React, { useState } from 'react';
import { useGradeErrors } from './utils/hooks';

const GradeModal = (
  {
    gradeMenuAnchorEl,
    handleGradeMenuClose,
    onChangeAllGrades,
    gradedItems,
    onChangeGrade,
    gradeItems,
    gradeType
  }
) => {
  const [gradeVal, setGradeVal] = useState<number>(null);
  const gradeErrors = useGradeErrors(gradeVal, gradeType);

  const onSetGrade = grade => {
    const id = gradeMenuAnchorEl?.attributes?.id?.value;
    if (id === "allGrades") {
      onChangeAllGrades(grade);
      handleGradeMenuClose();
      return;
    }
    const gradedItem = gradedItems[id?.replace("grade", "")];
    onChangeGrade(grade, gradedItem);
    handleGradeMenuClose();
  };

  return gradeType?.entryType === "choice list" ? (
    <Menu
      id="grades-menu"
      anchorEl={gradeMenuAnchorEl}
      keepMounted
      open={Boolean(gradeMenuAnchorEl)}
      onClose={handleGradeMenuClose}
    >
      <MenuItem onClick={() => onSetGrade(null)}>
        <span className="placeholderContent">{$t('no_grade')}</span>
      </MenuItem>
      {gradeItems?.map(g => (
        <MenuItem onClick={() => onSetGrade(g.lowerBound)}>
          {g.name}
        </MenuItem>
      ))}
    </Menu>
  ) : (
    <Dialog
      open={Boolean(gradeMenuAnchorEl)}
      onClose={handleGradeMenuClose}
      classes={{
        paper: "overflow-visible"
      }}
            disableAutoFocus
      disableEnforceFocus
      disableRestoreFocus
    >
      <DialogContent>
        <DialogTitle className="p-0 mb-2">{$t('set_all_students_grade_as')}</DialogTitle>
        <Grid container columnSpacing={3}>
          <Grid item xs={6}>
            <EditInPlaceField
              type="number"
              meta={gradeErrors}
              input={{
                onChange: e => setGradeVal(normalizeNumber(e.target.value)),
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: gradeVal
              }}
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button
          color="primary"
          onClick={handleGradeMenuClose}
        >
          {$t('close')}
        </Button>
        <Button
          variant="contained"
          color="primary"
          disabled={gradeErrors.invalid}
          onClick={() => {
            onChangeAllGrades(gradeVal);
            handleGradeMenuClose();
          }}
        >
          {$t('save2')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default GradeModal;
