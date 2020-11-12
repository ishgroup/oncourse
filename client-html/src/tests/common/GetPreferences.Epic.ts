import { store, mockedAPI } from "../TestEntry";
import { ActionsObservable } from "redux-observable";
import { PreferenceEnum } from "@api/model";
import {
  FETCH_FINISH,
  FETCH_START,
  GET_USER_PREFERENCES,
  GET_USER_PREFERENCES_FULFILLED
} from "../../js/common/actions";
import { EpicGetUserPreferences } from "../../js/common/epics/EpicGetUserPreferences";
import { filter, toArray } from "rxjs/operators";

export const GetPreferences = (keys: PreferenceEnum[]) => {
  // Expected response
  const preferences = mockedAPI.db.getUserPreferences(keys);

  // Redux action to trigger epic
  const action$ = ActionsObservable.of({ type: GET_USER_PREFERENCES, payload: keys });

  // Initializing epic instance
  const epic$ = EpicGetUserPreferences(action$, store, {});

  // Testing epic to be resolved with expected array of actions
  return expect(
    epic$
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
    }
  ]);
};
