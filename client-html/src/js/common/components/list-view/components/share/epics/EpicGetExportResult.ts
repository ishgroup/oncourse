/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { OutputType } from "@api/model";

import { stopSubmit } from "redux-form";
import { Epic } from "redux-observable";
import { State } from "../../../../../../reducers/state";
import { FETCH_FAIL, FETCH_SUCCESS } from "../../../../../actions";
import FetchErrorHandler from "../../../../../api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import { createAndDownloadFile } from "../../../../../utils/common";
import { GET_EXPORT_RESULT } from "../actions";
import ExportService from "../services/ExportService";

const request: EpicUtils.Request<any, {
  entityName: string;
  processId: string;
  outputType: OutputType,
  isClipboard?: boolean
}> = {
  type: GET_EXPORT_RESULT,
  hideLoadIndicator: true,
  getData: ({entityName, processId}) => ExportService.getExportResult(entityName, processId),
  processData: (data: any, state: State, {outputType, isClipboard}) => {
    let noContent = false;
    if (data) {
      if (isClipboard) {
        navigator.clipboard.writeText(data).catch(() => {
          console.error("Copy failed");
        });
      } else {
        createAndDownloadFile(data, outputType, state.form.ListShareForm.initial.name);
      }
    } else {
      noContent = true;
    }

    return [
      stopSubmit("ListShareForm"),
      noContent ? {
          type: FETCH_FAIL,
          payload: {message: "No export content was found for chosen template"}
        }
        : {
          type: FETCH_SUCCESS,
          payload: {message: isClipboard ? "Export content copied" : "Export completed"}
        }
    ];
  },
  processError: response => [stopSubmit("ListShareForm"), ...FetchErrorHandler(response)]
};

export const EpicGetExportResult: Epic<any, any> = EpicUtils.Create(request);
