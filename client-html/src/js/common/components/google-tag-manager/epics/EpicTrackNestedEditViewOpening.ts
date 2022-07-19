/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic, ofType } from "redux-observable";
import { mergeMap } from "rxjs/operators";
import { State } from "../../../../reducers/state";
import { GET_LIST_NESTED_EDIT_RECORD } from "../../list-view/actions";
import { pushGTMEvent } from "../actions";
import { Observable } from "rxjs";

export const EpicTrackNestedEditViewOpening: Epic<any, State> = (action$: Observable<any>): any => action$.pipe(
    ofType(GET_LIST_NESTED_EDIT_RECORD),
    mergeMap(action => {
      const { entity } = action.payload;

      window.performance.mark("NestedEditViewStart");

      return [pushGTMEvent("screenview", `${entity}EditView`)];
    })
  );
