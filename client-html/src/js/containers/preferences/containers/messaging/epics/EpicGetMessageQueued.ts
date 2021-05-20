/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { GET_MESSAGE_QUEUED_FULFILLED, GET_MESSAGE_QUEUED_REQUEST } from "../../../../../common/actions";

const request: EpicUtils.Request = {
  type: GET_MESSAGE_QUEUED_REQUEST,
  getData: payload => PreferencesService.messageQueued(payload.type),
  processData: (count: any, state: any, payload) => {
    return [
      {
        type: GET_MESSAGE_QUEUED_FULFILLED,
        payload: { count, type: payload.type }
      }
    ];
  }
};

export const EpicGetMessageQueued: Epic<any, any> = EpicUtils.Create(request);
