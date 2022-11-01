/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ofType, Epic } from "redux-observable";
import { mergeMap } from "rxjs/operators";
import { Observable } from "rxjs";
import { State } from "../../../../reducers/state";
import { initGAEvent } from "../services/GoogleAnalyticsService";
import { INIT_GOOGLE_TAG_MANAGER_EVENT } from "../actions";

export const EpicSendGoogleAnalytics: Epic<any, State> = (action$: Observable<any>): any => action$.pipe(
    ofType(INIT_GOOGLE_TAG_MANAGER_EVENT),
    mergeMap(action => {
      const { event, screen, time } = action.payload;

      initGAEvent(event, screen, time);
      return [];
    })
  );
