/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import IconButton from "@material-ui/core/IconButton";
import { ExpandMore } from "@material-ui/icons";
import GradeButton from "./GradeButton";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";
import { stubFunction } from "../../../../../common/utils/common";

const GradeContent = (
  {
    handleGradeMenuOpen,
    onToggleGrade,
    onChangeGrade,
    currentGrade,
    gradeErrors,
    setGradeVal,
    gradeType,
    gradeVal,
    classes,
    index,
    elem
  }
) => (
  <div className="d-flex relative">
    {gradeType?.entryType === "choice list"
      ? (
        <GradeButton
          onClick={() => onToggleGrade(elem, currentGrade)}
          grade={currentGrade?.name}
        />
      ) : (
        <span className="text-nowrap">
          <EditInPlaceField
            type="number"
            meta={gradeErrors}
            input={{
              onChange: e => setGradeVal(e.target.value),
              onFocus: stubFunction,
              onBlur: () => onChangeGrade(gradeVal, elem),
              value: gradeVal
            }}
            formatting="inline"
            hideArrows
          />
          {Boolean(currentGrade?.name) && `  ( ${currentGrade.name} )`}
        </span>
      )}
      {gradeType?.entryType === "choice list" && (
        <IconButton
          size="small"
          className={classes.hiddenIcon}
          onClick={handleGradeMenuOpen}
          id={`grade${index}`}
        >
          <ExpandMore color="disabled" fontSize="small" />
        </IconButton>
        )}
  </div>
);

export default GradeContent;
