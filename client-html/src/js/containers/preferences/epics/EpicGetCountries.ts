/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import PreferencesService from "../services/PreferencesService";
import { GET_COUNTRIES_REQUEST, GET_COUNTRIES_REQUEST_FULFILLED } from "../actions";
import { Country } from "@api/model";

const request: EpicUtils.Request = {
  type: GET_COUNTRIES_REQUEST,
  getData: () => PreferencesService.getCountries(),
  processData: (data: Country[]) => {
    const countries = data.sort((a, b) => (a.name > b.name ? 1 : -1));

    return [
      {
        type: GET_COUNTRIES_REQUEST_FULFILLED,
        payload: { countries }
      }
    ];
  }
};

export const EpicGetCountries: Epic<any, any> = EpicUtils.Create(request);
