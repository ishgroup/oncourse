/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ActionsObservable, Epic, StateObservable } from "redux-observable";
import { mergeMap } from "rxjs/operators";
import { Observable } from "rxjs";
import { State } from "../../reducers/state";
import { clearActionsQueue, EXECUTE_ACTIONS_QUEUE } from "../actions";

export const EpicExecuteActionsQueue: Epic<any, State> = (
  action$: ActionsObservable<any>,
  state$: StateObservable<State>
): Observable<any> => action$.ofType(EXECUTE_ACTIONS_QUEUE).pipe(
    mergeMap(() => {
      const syncActions = state$.value.actionsQueue.queuedActions.filter(a => a.entity !== "Note");

      return state$.value.actionsQueue.queuedActions.length
        ? [...syncActions.map(a => a.actionBody), clearActionsQueue()]
        : [];
    })
  );
