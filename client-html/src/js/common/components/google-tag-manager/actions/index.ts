/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType, FULFILLED } from "../../../actions/ActionUtils";
import { GAEventTypes } from "../services/GoogleAnalyticsService";

export const GET_TAG_MANAGER_PARAMETERS = _toRequestType("get/google/tagManager/parameter");
export const GET_GOOGLE_TAG_MANAGER_PARAMETERS_FULFILLED = FULFILLED(GET_TAG_MANAGER_PARAMETERS);

export const INIT_GOOGLE_TAG_MANAGER_EVENT = "initGAEvent";

export const getGoogleTagManagerParameters = () => ({
  type: GET_TAG_MANAGER_PARAMETERS
});

export const pushGTMEvent = (event: GAEventTypes, screen: string, time?: number) => ({
  type: INIT_GOOGLE_TAG_MANAGER_EVENT,
  payload: { event, screen, time }
});
