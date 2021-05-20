/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { GET_TAX_TYPES_FULFILLED, GET_TAX_TYPES_REQUEST } from "../../../actions";
import { Tax } from "@api/model";

const request: EpicUtils.Request = {
  type: GET_TAX_TYPES_REQUEST,
  getData: () => PreferencesService.getTaxTypes(),
  processData: (taxTypes: Tax[]) => {
    return [
      {
        type: GET_TAX_TYPES_FULFILLED,
        payload: { taxTypes }
      }
    ];
  }
};

export const EpicGetTaxTypes: Epic<any, any> = EpicUtils.Create(request);
