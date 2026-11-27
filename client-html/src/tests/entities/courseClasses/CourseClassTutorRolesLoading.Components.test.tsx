/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { render, screen } from '@testing-library/react';
import * as React from 'react';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../js/common/components/list-view/constants';
import ListEditView from '../../../js/common/components/list-view/components/edit-view/EditView';
import CourseClassEditView from '../../../js/containers/entities/courseClasses/components/CourseClassEditView';
import { COURSE_CLASS_TUTOR_ROLES_COLUMNS } from '../../../js/containers/entities/courseClasses/constants';
import { mockedAPI, TestEntry } from '../../TestEntry';

const tutorRoles = mockedAPI.db.getPlainTutorRoles().rows.map(r => ({ id: r.id, name: r.values[0] }));

// Tutor roles load in parallel with the class, the class budget and tutor pay must wait for roles with pay rates
const renderClass = (preferences: object) => {
  const record = {
    ...mockedAPI.db.getCourseClass(1),
    trainingPlan: mockedAPI.db.getCourseClassTrainingPlan(),
    sessions: mockedAPI.db.getCourseClassTimetable(),
    tutors: mockedAPI.db.getCourseClassTutors(1),
    budget: mockedAPI.db.getCourseClassBudget("1"),
    studentAttendance: mockedAPI.db.getCourseClassAttendanceStudents(),
    notes: [],
    assessments: mockedAPI.db.getCourseClassAssessment(1)
  };

  render(
    <TestEntry
      state={{
        taxes: { items: mockedAPI.db.getPlainTaxesFormatted() },
        location: mockedAPI.db.location,
        preferences
      }}
    >
      <ListEditView
        initialValues={record}
        values={record}
        EditViewContent={pr => <CourseClassEditView {...pr} />}
        rootEntity="CourseClass"
        form={LIST_EDIT_VIEW_FORM_NAME}
        hasSelected
        creatingNew={false}
        toogleFullScreenEditView={jest.fn()}
      />
    </TestEntry>
  );
};

describe("CourseClassEditView tutor roles loading", () => {
  beforeAll(() => {
    window.performance.getEntriesByName = jest.fn(() => []);
  });

  it("is not rendered while tutor roles are not loaded yet", () => {
    renderClass({});

    expect(screen.queryByTestId("expand-button-2")).toBeNull();
  });

  it("is not rendered while only tutor roles without pay rates are loaded", () => {
    renderClass({ tutorRoles });

    expect(screen.queryByTestId("expand-button-2")).toBeNull();
  });

  it("is rendered once tutor roles with pay rates are loaded", () => {
    renderClass({ tutorRoles, tutorRolesColumns: COURSE_CLASS_TUTOR_ROLES_COLUMNS });

    expect(screen.getByTestId("expand-button-2")).toBeTruthy();
  });

  it("shows an error with a retry instead of the class when tutor roles fail to load", () => {
    renderClass({ tutorRolesError: true });

    expect(screen.queryByTestId("expand-button-2")).toBeNull();
    expect(screen.getByText("Try again")).toBeTruthy();
  });
});
