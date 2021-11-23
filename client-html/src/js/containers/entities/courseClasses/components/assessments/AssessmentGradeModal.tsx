/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useState } from "react";
import {
  Grid,
  Menu,
  MenuItem,
  DialogContent,
  DialogTitle,
  DialogActions,
  Button,
  Dialog
} from "@mui/material";
import { stubFunction } from "../../../../../common/utils/common";
import EditInPlaceField from "../../../../../common/components/form/formFields/EditInPlaceField";
import { useGradeErrors } from "./utils/hooks";
import { normalizeNumber } from "../../../../../common/utils/numbers/numbersNormalizing";

const AssessmentGradeModal = (
  {
    gradeMenuAnchorEl,
    handleGradeMenuClose,
    onChangeAllGrades,
    studentsForRender,
    onChangeGrade,
    gradeItems,
    gradeType
  }
) => {
  const [gradeVal, setGradeVal] = useState<number>(null);
  const gradeErrors = useGradeErrors(gradeVal, gradeType);

  const onSetGrade = grade => {
    const id = gradeMenuAnchorEl?.attributes?.id?.value;
    if (id === "allStudents") {
      onChangeAllGrades(grade);
      handleGradeMenuClose();
      return;
    }
    const student = studentsForRender[id?.replace("stud", "")];
    onChangeGrade(grade, student);
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
        <span className="placeholderContent">No grade</span>
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
      fullWidth
      disableAutoFocus
      disableEnforceFocus
      disableRestoreFocus
    >
      <DialogContent>
        <DialogTitle className="p-0 mb-2">Set all students grade as</DialogTitle>
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
              hideArrows
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button
          color="primary"
          onClick={handleGradeMenuClose}
        >
          Close
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
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AssessmentGradeModal;
