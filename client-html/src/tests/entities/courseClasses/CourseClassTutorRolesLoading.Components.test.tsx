/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { fireEvent, render, screen } from '@testing-library/react';
import * as React from 'react';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../js/common/components/list-view/constants';
import ListEditView from '../../../js/common/components/list-view/components/edit-view/EditView';
import CourseClassEditView from '../../../js/containers/entities/courseClasses/components/CourseClassEditView';
import { COURSE_CLASS_TUTOR_ROLES_COLUMNS } from '../../../js/containers/entities/courseClasses/constants';
import { GET_TUTOR_ROLES_REQUEST, getTutorRoles } from '../../../js/containers/preferences/actions';
import TestStore from '../../../js/constants/Store';
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

const loadClassTutorRoles = getTutorRoles(null, null, COURSE_CLASS_TUTOR_ROLES_COLUMNS);

const classTutorRoles = (load: object) => ({ tutorRolesByColumns: { [COURSE_CLASS_TUTOR_ROLES_COLUMNS]: load } });

describe("CourseClassEditView tutor roles loading", () => {
  let dispatch: jest.SpyInstance;

  beforeAll(() => {
    window.performance.getEntriesByName = jest.fn(() => []);
  });

  beforeEach(() => {
    const storeDispatch = TestStore.dispatch;

    // Tutor roles request is only recorded, other actions (form initialization) are dispatched as usual
    dispatch = jest.spyOn(TestStore, "dispatch").mockImplementation(
      (action: any) => (action.type === GET_TUTOR_ROLES_REQUEST ? action : storeDispatch(action))
    );
  });

  afterEach(() => {
    dispatch.mockRestore();
  });

  it("shows loading while tutor roles with pay rates are loading", () => {
    renderClass({});

    expect(screen.queryByTestId("expand-button-2")).toBeNull();
    expect(screen.getByRole("progressbar")).toBeTruthy();
    expect(dispatch).not.toHaveBeenCalledWith(loadClassTutorRoles);
  });

  it("does not use tutor roles loaded without pay rates, for example by Preferences", () => {
    renderClass({ tutorRoles });

    expect(screen.queryByTestId("expand-button-2")).toBeNull();
    expect(screen.getByRole("progressbar")).toBeTruthy();
    expect(dispatch).not.toHaveBeenCalledWith(loadClassTutorRoles);
  });

  it("is rendered once tutor roles with pay rates are loaded", () => {
    renderClass(classTutorRoles({ roles: tutorRoles }));

    expect(screen.getByTestId("expand-button-2")).toBeTruthy();
    expect(dispatch).not.toHaveBeenCalledWith(loadClassTutorRoles);
  });

  it("is rendered when a reload fails but tutor roles with pay rates are already loaded", () => {
    renderClass(classTutorRoles({ roles: tutorRoles, error: true }));

    expect(screen.getByTestId("expand-button-2")).toBeTruthy();
    expect(screen.queryByText("Try again")).toBeNull();
  });

  it("shows an error with a retry when tutor roles with pay rates fail to load", () => {
    renderClass(classTutorRoles({ error: true }));

    expect(screen.queryByTestId("expand-button-2")).toBeNull();
    expect(dispatch).not.toHaveBeenCalledWith(loadClassTutorRoles);

    fireEvent.click(screen.getByText("Try again"));

    expect(dispatch).toHaveBeenCalledWith(loadClassTutorRoles);
  });
});
