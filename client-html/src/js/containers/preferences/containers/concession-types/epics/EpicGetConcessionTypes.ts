/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ConcessionType } from "@api/model";
import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_CONCESSION_TYPES_FULFILLED, GET_CONCESSION_TYPES_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request = {
  type: GET_CONCESSION_TYPES_REQUEST,
  getData: () => PreferencesService.getConcessionTypes(),
  processData: (concessionTypes: ConcessionType[]) => {
    return [
      {
        type: GET_CONCESSION_TYPES_FULFILLED,
        payload: { concessionTypes }
      }
    ];
  }
};

export const EpicGetConcessionTypes: Epic<any, any> = EpicUtils.Create(request);
