/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GET_TUTOR_ROLES_FAILED, GET_TUTOR_ROLES_FULFILLED, getTutorRoles } from "../../../js/containers/preferences/actions";
import { preferencesReducer } from "../../../js/containers/preferences/reducers";

const columns = "name,currentPayrate.rate";

const payRateRoles: any[] = [{ id: 1, name: "Tutor", "currentPayrate.rate": "50" }];

const plainRoles: any[] = [{ id: 1, name: "Tutor" }];

const loadedState = { tutorRoles: payRateRoles, tutorRolesByColumns: { [columns]: { roles: payRateRoles } } };

describe("Preferences reducer tutor roles tests", () => {
  it("stores tutor roles loaded with custom columns under their columns", () => {
    const state = preferencesReducer({}, { type: GET_TUTOR_ROLES_FULFILLED, payload: { tutorRoles: payRateRoles, columns } });

    expect(state.tutorRoles).toBe(payRateRoles);
    expect(state.tutorRolesByColumns[columns]).toEqual({ roles: payRateRoles, error: false });
  });

  it("keeps tutor roles loaded with custom columns when tutor roles are loaded with default columns", () => {
    const requested = preferencesReducer(loadedState, getTutorRoles());
    const state = preferencesReducer(requested, { type: GET_TUTOR_ROLES_FULFILLED, payload: { tutorRoles: plainRoles } });

    expect(state.tutorRoles).toBe(plainRoles);
    expect(state.tutorRolesByColumns[columns].roles).toBe(payRateRoles);
  });

  it("marks the error for the columns and keeps their tutor roles when they fail to load", () => {
    const state = preferencesReducer(loadedState, { type: GET_TUTOR_ROLES_FAILED, payload: { columns } });

    expect(state.tutorRolesByColumns[columns]).toEqual({ roles: payRateRoles, error: true });
  });

  it("does not mark custom columns as failed when tutor roles with default columns fail to load", () => {
    const state = preferencesReducer(loadedState, { type: GET_TUTOR_ROLES_FAILED, payload: {} });

    expect(state).toBe(loadedState);
  });

  it("clears the error for the columns when tutor roles are requested again", () => {
    const failed = { tutorRolesByColumns: { [columns]: { error: true } } };

    expect(preferencesReducer(failed, getTutorRoles(null, null, columns)).tutorRolesByColumns[columns].error).toBe(false);
  });
});
