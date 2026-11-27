/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { getClassCostTypes } from '../../../../js/containers/entities/courseClasses/utils';

const tutor: any = { id: 10, roleId: 5 };

const wage: any = {
  flowType: "Wages",
  repetitionType: "Fixed",
  perUnitAmountExTax: 100,
  courseClassTutorId: 10
};

const role = { id: 5, "currentPayrate.oncostRate": "0.1" };

const getCosts = (tutorRoles: any[]) => getClassCostTypes([wage], 10, 5, 3, [], [tutor], tutorRoles);

describe("CourseClass getClassCostTypes utils tests", () => {
  test("Tutor wage is counted without on cost while tutor roles are not loaded yet", () => {
    expect(() => getCosts(undefined)).not.toThrow();
    expect(getCosts(undefined).cost).toMatchObject({ max: 100, projected: 100 });
  });

  test("Tutor wage is counted with its role on cost once tutor roles are loaded", () => {
    expect(getCosts([role]).cost).toMatchObject({ max: 110, projected: 110 });
  });
});
