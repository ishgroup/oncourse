/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic, ofType } from "redux-observable";
import { flatMap } from "rxjs/operators";
import { State } from "../../../../reducers/state";
import { GET_GOOGLE_TAG_MANAGER_PARAMETERS_FULFILLED } from "../actions";
import {
  GOOGLE_ANALYTICS_CLIENT_ID_KEY,
  GOOGLE_ANALYTICS_COMPAIN_ID_KEY,
  GOOGLE_ANALYTICS_COMPAIN_NAME_KEY,
  GOOGLE_ANALYTICS_USER_ID_KEY,
  GOOGLE_TAG_MANAGER,
  GOOGLE_TAG_MANAGER_DEFAULT_APP_NAME
} from "../../../../constants/Config";
import { Observable } from "rxjs";

// Google Analytics Init
export const EpicInitGoogleAnalytics: Epic<any, State> = (action$: Observable<any>): any => action$.pipe(
    ofType(GET_GOOGLE_TAG_MANAGER_PARAMETERS_FULFILLED),
    flatMap(action => {
      const script = document.createElement("script");

      const dataLayerScript = document.createTextNode(`dataLayer = [{
       'campaignId': "${action.payload[GOOGLE_ANALYTICS_COMPAIN_ID_KEY]}",
       'campaignName': "${action.payload[GOOGLE_ANALYTICS_COMPAIN_NAME_KEY]}",
       'appName': "${GOOGLE_TAG_MANAGER_DEFAULT_APP_NAME}",
       'appVersion': "${process.env.RELEASE_VERSION}",
       'clientId': "${action.payload[GOOGLE_ANALYTICS_CLIENT_ID_KEY]}",
       'userId': "${action.payload[GOOGLE_ANALYTICS_USER_ID_KEY]}"
       }];
       
       (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
        new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
        j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
        'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
        })(window,document,'script','dataLayer','${GOOGLE_TAG_MANAGER}');
       `);

      script.appendChild(dataLayerScript);
      document.head.appendChild(script);

      const noscript = document.createElement("noscript");
      const iframe = document.createElement("iframe");
      iframe.src = `https://www.googletagmanager.com/ns.html?id=${GOOGLE_TAG_MANAGER}`;
      iframe.height = "0";
      iframe.width = "0";
      // @ts-ignore
      iframe.style = "display:none;visibility:hidden";
      noscript.appendChild(iframe);
      document.body.prepend(noscript);

      return [];
    })
  );
