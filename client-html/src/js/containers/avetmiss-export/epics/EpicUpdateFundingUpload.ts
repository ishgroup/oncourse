/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { FundingStatus } from "@api/model";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { UPDATE_FUNDING_UPLOAD_FULFILLED, UPDATE_FUNDING_UPLOAD_REQUEST } from "../actions";
import FundingUploadService from "../services/FundingUploadService";

const request: EpicUtils.Request<any, { id: number; status: FundingStatus }> = {
  type: UPDATE_FUNDING_UPLOAD_REQUEST,
  getData: ({ id, status }) => FundingUploadService.update(id, status),
  processData: (value, state, payload) => {
    const id = payload.id;
    const status = payload.status;
    return [
      {
        type: UPDATE_FUNDING_UPLOAD_FULFILLED,
        payload: { id, status }
      }
    ];
  }
};

export const EpicUpdateFundingUpload: Epic<any, any> = EpicUtils.Create(request);
