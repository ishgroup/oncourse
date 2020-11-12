/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ActionsObservable, Epic } from "redux-observable";
import { State } from "../../../../reducers/state";
import { initGAEvent } from "../services/GoogleAnalyticsService";
import { INIT_GOOGLE_TAG_MANAGER_EVENT } from "../actions";
import { mergeMap } from "rxjs/operators";

export const EpicSendGoogleAnalytics: Epic<any, State> = (action$: ActionsObservable<any>): any => {
  return action$.ofType(INIT_GOOGLE_TAG_MANAGER_EVENT).pipe(
    mergeMap(action => {
      const { event, screen, time } = action.payload;

      initGAEvent(event, screen, time);
      return [];
    })
  );
};
