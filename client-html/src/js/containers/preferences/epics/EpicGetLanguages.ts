/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import PreferencesService from "../services/PreferencesService";
import { GET_LANGUAGES_REQUEST, GET_LANGUAGES_REQUEST_FULFILLED } from "../actions";
import { Language } from "@api/model";

const request: EpicUtils.Request<any, any, any> = {
  type: GET_LANGUAGES_REQUEST,
  getData: () => PreferencesService.getLanguages(),
  processData: (data: Language[]) => {
    const languages = data.sort((a, b) => (a.name > b.name ? 1 : -1));

    return [
      {
        type: GET_LANGUAGES_REQUEST_FULFILLED,
        payload: { languages }
      }
    ];
  }
};

export const EpicGetLanguages: Epic<any, any> = EpicUtils.Create(request);
