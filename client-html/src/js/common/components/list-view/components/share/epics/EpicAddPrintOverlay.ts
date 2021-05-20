/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import { ADD_PRINT_OVERLAY, ADD_PRINT_OVERLAY_FULFILLED, GET_OVERLAY_ITEMS } from "../actions";
import FetchErrorHandler from "../../../../../api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../../actions";
import ReportOverlayService
  from "../../../../../../containers/automation/containers/pdf-backgrounds/services/ReportOverlayService";

const request: EpicUtils.Request<any, { fileName: string; overlay: File }> = {
  type: ADD_PRINT_OVERLAY,
  getData: ({ fileName, overlay }) => ReportOverlayService.addOverlay(fileName, overlay),
  processData: (value, state, payload) => ([
    {
      type: ADD_PRINT_OVERLAY_FULFILLED
    },
    {
      type: GET_OVERLAY_ITEMS,
      payload: { overlayToSelect: payload.fileName }
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "New Background was added" }
    }
  ]),
  processError: response => FetchErrorHandler(response, "New Background was not added")
};

export const EpicAddPrintOverlay: Epic<any, any> = EpicUtils.Create(request);
