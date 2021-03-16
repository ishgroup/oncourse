/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { FundingUpload } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_FUNDING_UPLOADS_FULFILLED, GET_FUNDING_UPLOADS_REQUEST } from "../actions";
import FundingUploadService from "../services/FundingUploadService";
import getAvetmissExportFormValues from "../utils/getAvetmissExportFormValues";

const request: EpicUtils.Request<any, string> = {
  type: GET_FUNDING_UPLOADS_REQUEST,
  getData: search => FundingUploadService.getFundingUploads(search),
  processData: (uploads: FundingUpload[]) => {
    const fundingUpload = uploads.find(value => value.lastSettings);

    const settings = getAvetmissExportFormValues(fundingUpload ? fundingUpload.settings : null);

    return [
      initialize("AvetmissExportForm", settings),
      {
        type: GET_FUNDING_UPLOADS_FULFILLED,
        payload: { uploads }
      }
    ];
  }
};

export const EpicGetFundingUploads: Epic<any, any> = EpicUtils.Create(request);
