/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GET_TUTOR_ROLES_FAILED } from "../../../js/containers/preferences/actions";
import { preferencesReducer } from "../../../js/containers/preferences/reducers";

describe("Preferences reducer tutor roles tests", () => {
  it("marks tutor roles as loaded with an empty list when they fail to load", () => {
    expect(preferencesReducer({}, { type: GET_TUTOR_ROLES_FAILED }).tutorRoles).toEqual([]);
  });

  it("keeps already loaded tutor roles when they fail to reload", () => {
    const tutorRoles: any[] = [{ id: 1, name: "Tutor" }];

    expect(preferencesReducer({ tutorRoles }, { type: GET_TUTOR_ROLES_FAILED }).tutorRoles).toBe(tutorRoles);
  });
});
