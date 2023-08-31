/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { SelectItemDefault } from "../../../model/entities/common";
import { GET_TIMEZONES_FULFILLED, GET_TIMEZONES_REQUEST } from "../actions";
import PreferencesService from "../services/PreferencesService";

const request: EpicUtils.Request = {
  type: GET_TIMEZONES_REQUEST,
  getData: () => PreferencesService.getTimezones(),
  processData: (data: string[]) => {
    const timezones: SelectItemDefault[] = data.map(t => ({ value: t, label: t }));
    return [
      {
        type: GET_TIMEZONES_FULFILLED,
        payload: { timezones }
      }
    ];
  }
};

export const EpicGetTimezones: Epic<any, any> = EpicUtils.Create(request);
