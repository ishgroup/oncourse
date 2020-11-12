/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ActionsObservable } from "redux-observable";
import { filter, toArray } from "rxjs/operators";
import { store } from "../TestEntry";
import { FETCH_FINISH, FETCH_START } from "../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  GET_RECORDS_FULFILLED
} from "../../js/common/components/list-view/actions";
import { EpicGetEntities } from "../../js/common/components/list-view/epics/EpicGetEntities";

export const GetEntities = (entity: string, records: []) => {
  // Redux action to trigger epic
  const action$ = ActionsObservable.of({
    type: GET_RECORDS_REQUEST,
    payload: { entity, viewAll: false }
  });

  // Initializing epic instance
  const epic$ = EpicGetEntities(action$, store, {});

  // Testing epic to be resolved with expected array of actions
  return expect(
    epic$
      .pipe(
        // Filtering common actions
        filter(a => ![FETCH_START, FETCH_FINISH].includes(a.type)),
        toArray()
      )
      .toPromise()
  ).resolves.toEqual([
    {
      type: GET_RECORDS_FULFILLED,
      payload: {
        records,
        payload: { entity, viewAll: false },
        searchQuery: {
          filter: "",
          offset: 0,
          pageSize: 50,
          search: "",
          tagGroups: []
        }
      }
    }
  ]);
};
