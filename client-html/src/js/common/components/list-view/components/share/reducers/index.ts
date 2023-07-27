/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../../../actions/IshAction";
import {
  GET_EXPORT_TEMPLATES,
  GET_EXPORT_TEMPLATES_FULFILLED,
  GET_OVERLAY_ITEMS_FULFILLED,
  GET_PDF_REPORTS,
  GET_PDF_REPORTS_FULFILLED,
  SET_PRINT_VALIDATING_STATUS
} from "../actions";
import { ShareState } from "../../../../../../model/common/Share";

export class ShareStateInitial implements ShareState {
  exportTemplates = [];

  exportTemplatesFetching = false;

  pdfReports = [];

  pdfReportsFetching = false;

  overlays = [];

  validating = false;
}

export const shareReducer = (state: ShareState = new ShareStateInitial(), action: IAction<any>): ShareState => {
  switch (action.type) {
    case GET_PDF_REPORTS: {
      return {
        ...state,
        pdfReportsFetching: true
      };
    }

    case GET_EXPORT_TEMPLATES: {
      return {
        ...state,
        exportTemplatesFetching: true
      };
    }

    case GET_EXPORT_TEMPLATES_FULFILLED: {
      return {
        ...state,
        ...action.payload,
        exportTemplatesFetching: false
      };
    }

    case GET_PDF_REPORTS_FULFILLED: {
      return {
        ...state,
        ...action.payload,
        pdfReportsFetching: false
      };
    }

    case SET_PRINT_VALIDATING_STATUS:
    case GET_OVERLAY_ITEMS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
