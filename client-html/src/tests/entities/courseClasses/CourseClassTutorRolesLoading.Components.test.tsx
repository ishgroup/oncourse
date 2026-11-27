/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { mockedEditView } from "../../common/MockedEditView.Components";
import CourseClassEditView from "../../../js/containers/entities/courseClasses/components/CourseClassEditView";

// Tutor roles load in parallel with the class, the class budget must not be calculated before they arrive
describe("CourseClassEditView while tutor roles are not loaded yet", () => {
  mockedEditView({
    entity: "CourseClass",
    EditView: CourseClassEditView,
    record: mockedApi => ({
      ...mockedApi.db.getCourseClass(1),
      sessions: mockedApi.db.getCourseClassTimetable(),
      tutors: mockedApi.db.getCourseClassTutors(1),
      budget: mockedApi.db.getCourseClassBudget("1"),
      notes: [],
      assessments: []
    }),
    state: ({ mockedApi }) => ({
      taxes: { items: mockedApi.db.getPlainTaxesFormatted() },
      location: mockedApi.db.location,
      preferences: { tutorRoles: undefined }
    }),
    render: ({ screen }) => {
      expect(screen.queryByTestId("expand-button-2")).toBeNull();
    }
  });
});
