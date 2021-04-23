/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */


import { GradingType } from "@api/model";

export const useGradeErrors = (grade: number, gradeType: GradingType) => {
  const errors: any = {};

  if (typeof grade !== "number") {
    return errors;
  }

  if (grade > gradeType.maxValue) {
    errors.invalid = true;
    errors.error = `Max value is ${gradeType.maxValue}`;
  }

  if (grade < gradeType.minValue) {
    errors.invalid = true;
    errors.error = `Min value is ${gradeType.minValue}`;
  }

  return errors;
};
