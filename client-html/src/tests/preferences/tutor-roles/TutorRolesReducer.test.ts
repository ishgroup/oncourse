/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GET_TUTOR_ROLES_FAILED, GET_TUTOR_ROLES_FULFILLED, getTutorRoles } from "../../../js/containers/preferences/actions";
import { preferencesReducer } from "../../../js/containers/preferences/reducers";

const tutorRoles: any[] = [{ id: 1, name: "Tutor" }];

describe("Preferences reducer tutor roles tests", () => {
  it("keeps tutor roles and marks the error when tutor roles fail to load", () => {
    const state = preferencesReducer({ tutorRoles }, { type: GET_TUTOR_ROLES_FAILED });

    expect(state.tutorRoles).toBe(tutorRoles);
    expect(state.tutorRolesError).toBe(true);
  });

  it("does not set tutor roles when they fail to load before any were loaded", () => {
    expect(preferencesReducer({}, { type: GET_TUTOR_ROLES_FAILED }).tutorRoles).toBeUndefined();
  });

  it("clears the error when tutor roles are requested again", () => {
    expect(preferencesReducer({ tutorRolesError: true }, getTutorRoles()).tutorRolesError).toBe(false);
  });

  it("stores the columns tutor roles were loaded with", () => {
    const state = preferencesReducer({}, { type: GET_TUTOR_ROLES_FULFILLED, payload: { tutorRoles, columns: "name,currentPayrate.rate" } });

    expect(state.tutorRoles).toBe(tutorRoles);
    expect(state.tutorRolesColumns).toBe("name,currentPayrate.rate");
  });
});
