/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import AvetmissExportService from "../services/AvetmissExportService";
import {
  CLEAR_AVETMISS8_EXPORT_ID,
  GET_AVETMISS_EXPORT_OUTCOMES,
  GET_AVETMISS_EXPORT_OUTCOMES_FULFILLED
} from "../actions";
import { AvetmissExportOutcome } from "@api/model";
import { CLEAR_PROCESS } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: GET_AVETMISS_EXPORT_OUTCOMES,
  getData: (outcomesID: string) => AvetmissExportService.getExportOutcomes(outcomesID),
  processData: (outcomes: AvetmissExportOutcome[]) => {
    return [
      {
        type: GET_AVETMISS_EXPORT_OUTCOMES_FULFILLED,
        payload: { outcomes }
      },
      {
        type: CLEAR_AVETMISS8_EXPORT_ID
      },
      {
        type: CLEAR_PROCESS
      }
    ];
  },
  processError: response => {
    return [
      {
        type: CLEAR_AVETMISS8_EXPORT_ID
      },
      {
        type: CLEAR_PROCESS
      },
      ...FetchErrorHandler(response)
    ];
  }
};

export const EpicGetAvetmiss8ExportOutcomes: Epic<any, any> = EpicUtils.Create(request);
