/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { getClassCostTypes } from '../../../../js/containers/entities/courseClasses/utils';

const tutors: any[] = [{ id: 10, roleId: 5 }, { id: 11, roleId: 6 }];

const tutorRoles: any[] = [
  { id: 5, "currentPayrate.oncostRate": "0.1" },
  { id: 6, "currentPayrate.oncostRate": "0.2" }
];

const getWage = (courseClassTutorId: number, onCostRate?: number): any => ({
  flowType: "Wages",
  repetitionType: "Fixed",
  perUnitAmountExTax: 100,
  courseClassTutorId,
  onCostRate: onCostRate ?? null
});

const getCosts = (budget: any[]) => getClassCostTypes(budget, 10, 5, 3, [], tutors, tutorRoles).cost;

describe("CourseClass getClassCostTypes utils tests", () => {
  test("Tutor wage is counted with the on cost of its role", () => {
    expect(getCosts([getWage(10)])).toMatchObject({ max: 110, projected: 110 });
  });

  test("Tutor wage on cost saved on the wage is used instead of its role on cost", () => {
    expect(getCosts([getWage(10, 0.5)])).toMatchObject({ max: 150, projected: 150 });
  });

  test("Each tutor wage is counted once with the on cost of its own role", () => {
    expect(getCosts([getWage(10), getWage(11)])).toMatchObject({ max: 230, projected: 230 });
  });
});
