/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { from, filter, toArray } from "rxjs";
import { store, mockedAPI } from "../TestEntry";
import { GET_USER_PREFERENCES_FULFILLED, FETCH_FINISH, FETCH_START } from "../../js/common/actions";
import {
  GOOGLE_ANALYTICS_CLIENT_ID_KEY,
  GOOGLE_ANALYTICS_COMPAIN_ID_KEY,
  GOOGLE_ANALYTICS_COMPAIN_NAME_KEY,
  GOOGLE_ANALYTICS_USER_ID_KEY
} from "../../js/constants/Config";
import { EpicGetGoogleAnalyticsParameters } from "../../js/common/components/google-tag-manager/epics/EpicGetGoogleAnalyticsParameters";
import {
  GET_TAG_MANAGER_PARAMETERS,
  GET_GOOGLE_TAG_MANAGER_PARAMETERS_FULFILLED
} from "../../js/common/components/google-tag-manager/actions";

describe("Get google analytics parameters epics tests", () => {
  it("EpicGetUserPreferences should return correct actions", () => {
    // Expected response
    const preferences = mockedAPI.db.getUserPreferences([
      GOOGLE_ANALYTICS_CLIENT_ID_KEY,
      GOOGLE_ANALYTICS_COMPAIN_ID_KEY,
      GOOGLE_ANALYTICS_COMPAIN_NAME_KEY,
      GOOGLE_ANALYTICS_USER_ID_KEY
    ]);

    // Redux action to trigger epic
    const action$ = from([{ type: GET_TAG_MANAGER_PARAMETERS }]);

    // Initializing epic instance
    const epic$ = EpicGetGoogleAnalyticsParameters(action$, store, {});

    // Testing epic to be resolved with expected array of actions
    return expect(
      epic$
        // Filtering common actions
        .pipe(
          // Filtering common actions
          filter(a => [FETCH_START, FETCH_FINISH].includes(a.type) === false),
          toArray()
        )
        .toPromise()
    ).resolves.toEqual([
      {
        type: GET_USER_PREFERENCES_FULFILLED,
        payload: { preferences }
      },
      {
        type: GET_GOOGLE_TAG_MANAGER_PARAMETERS_FULFILLED,
        payload: preferences
      }
    ]);
  });
});
