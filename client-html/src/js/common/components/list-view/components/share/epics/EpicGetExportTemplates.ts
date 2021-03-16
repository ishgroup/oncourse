import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import { GET_EXPORT_TEMPLATES, GET_EXPORT_TEMPLATES_FULFILLED } from "../actions";
import ExportService from "../services/ExportService";
import { ExportTemplate } from "@api/model";
import FetchErrorHandler from "../../../../../api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: GET_EXPORT_TEMPLATES,
  getData: payload => ExportService.getAllTemplates(payload),
  processData: (exportTemplates: ExportTemplate[]) => {
    return [
      {
        type: GET_EXPORT_TEMPLATES_FULFILLED,
        payload: { exportTemplates }
      }
    ];
  },
  processError: response => {
    if (response && response.status === 403) {
      return [];
    }

    return FetchErrorHandler(response);
  }
};

export const EpicGetExportTemplates: Epic<any, any> = EpicUtils.Create(request);
