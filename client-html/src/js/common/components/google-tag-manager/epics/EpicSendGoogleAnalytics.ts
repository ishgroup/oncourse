/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic, ofType } from "redux-observable";
import { Observable } from "rxjs";
import { mergeMap } from "rxjs/operators";
import { State } from "../../../../reducers/state";
import { INIT_GOOGLE_TAG_MANAGER_EVENT } from "../actions";
import { initGAEvent } from "../services/GoogleAnalyticsService";

export const EpicSendGoogleAnalytics: Epic<any, State> = (action$: Observable<any>): any => action$.pipe(
    ofType(INIT_GOOGLE_TAG_MANAGER_EVENT),
    mergeMap(action => {
      const { event, screen, time } = action.payload;

      initGAEvent(event, screen, time);
      return [];
    })
  );
