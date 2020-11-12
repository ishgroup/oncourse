/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AvetmissExportRequest, AvetmissExportSettings, FundingStatus } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";

export const GET_AVETMISS_EXPORT_OUTCOMES = _toRequestType("get/export/avetmiss8/outcomes");
export const GET_AVETMISS_EXPORT_OUTCOMES_FULFILLED = FULFILLED(GET_AVETMISS_EXPORT_OUTCOMES);

export const GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID = _toRequestType("put/export/avetmiss8/outcomes");
export const GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID_FULFILLED = FULFILLED(GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID);

export const GET_AVETMISS8_EXPORT_ID = _toRequestType("put/export/avetmiss8");
export const GET_AVETMISS8_EXPORT_ID_FULFILLED = FULFILLED(GET_AVETMISS8_EXPORT_ID);

export const GET_AVETMISS8_EXPORT_STATUS = _toRequestType("get/export/avetmiss8/status");

export const GET_AVETMISS8_OUTCOMES_STATUS = _toRequestType("get/export/avetmiss8/outcomes/status");

export const GET_AVETMISS8_EXPORT_RESULTS = _toRequestType("get/export/avetmiss8");
export const GET_AVETMISS8_EXPORT_RESULTS_FULFILLED = FULFILLED(GET_AVETMISS8_EXPORT_RESULTS);

export const CLEAR_AVETMISS8_EXPORT_ID = "clear/export/avetmiss8/exportID";

export const CLEAR_AVETMISS_EXPORT_OUTCOMES = "clear/export/avetmiss8/outcomes";
export const CLEAR_AVETMISS_EXPORT_UPLOADS = "clear/export/avetmiss8/uploads";

export const GET_FUNDING_UPLOADS_REQUEST = _toRequestType("get/funding/uploads");
export const GET_FUNDING_UPLOADS_FULFILLED = FULFILLED(GET_FUNDING_UPLOADS_REQUEST);

export const UPDATE_FUNDING_UPLOAD_REQUEST = _toRequestType("update/funding/upload");
export const UPDATE_FUNDING_UPLOAD_FULFILLED = FULFILLED(UPDATE_FUNDING_UPLOAD_REQUEST);

export const GET_ACTIVE_FUNDING_CONTRACTS_REQUEST = _toRequestType("get/active/funding/contracts");
export const GET_ACTIVE_FUNDING_CONTRACTS_FULFILLED = FULFILLED(GET_ACTIVE_FUNDING_CONTRACTS_REQUEST);

export const getAvetmiss8OutcomesStatus = (outcomesID: string) => ({
  type: GET_AVETMISS8_OUTCOMES_STATUS,
  payload: outcomesID
});

export const getAvetmiss8ExportStatus = (exportID: string) => ({
  type: GET_AVETMISS8_EXPORT_STATUS,
  payload: exportID
});

export const getAvetmiss8ExportID = (requestParameters: AvetmissExportRequest) => ({
  type: GET_AVETMISS8_EXPORT_ID,
  payload: requestParameters
});

export const getAvetmiss8ExportOutcomesProcessID = (settings: AvetmissExportSettings) => ({
  type: GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID,
  payload: settings
});

export const getAvetmiss8ExportResults = (processId: string) => ({
  type: GET_AVETMISS8_EXPORT_RESULTS,
  payload: processId
});

export const clearExportOutcomes = () => ({
  type: CLEAR_AVETMISS_EXPORT_OUTCOMES
});

export const clearAvetmiss8ExportID = () => ({
  type: CLEAR_AVETMISS8_EXPORT_ID
});

export const clearExportUploads = () => ({
  type: CLEAR_AVETMISS_EXPORT_UPLOADS
});

export const getFundingUploads = (search?: string) => ({
  type: GET_FUNDING_UPLOADS_REQUEST,
  payload: search
});

export const updateFundingUpload = (id: number, status: FundingStatus) => ({
  type: UPDATE_FUNDING_UPLOAD_REQUEST,
  payload: { id, status }
});

export const getActiveFundingContracts = (addNotFunded?: boolean) => ({
  type: GET_ACTIVE_FUNDING_CONTRACTS_REQUEST,
  payload: addNotFunded
});
