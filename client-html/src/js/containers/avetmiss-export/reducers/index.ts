import { IAction } from "../../../common/actions/IshAction";
import {
  CLEAR_AVETMISS_EXPORT_OUTCOMES,
  GET_AVETMISS_EXPORT_OUTCOMES_FULFILLED,
  CLEAR_AVETMISS8_EXPORT_ID,
  GET_AVETMISS8_EXPORT_ID_FULFILLED,
  GET_AVETMISS8_EXPORT_RESULTS_FULFILLED,
  GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID_FULFILLED,
  GET_FUNDING_UPLOADS_FULFILLED,
  UPDATE_FUNDING_UPLOAD_FULFILLED,
  GET_ACTIVE_FUNDING_CONTRACTS_FULFILLED,
  CLEAR_AVETMISS_EXPORT_UPLOADS
} from "../actions";
import { AvetmissExportState } from "./state";
import { format as formatDate } from "date-fns";

import { YYYYMMDD_KKMMSS_MINUSED } from  "ish-ui";

export const AvetmissExportReducer = (state: AvetmissExportState = {}, action: IAction<any>): any => {
  switch (action.type) {
    case GET_AVETMISS_EXPORT_OUTCOMES_FULFILLED:
    case GET_AVETMISS8_EXPORT_ID_FULFILLED:
    case GET_AVETMISS_EXPORT_OUTCOMES_PROCESS_ID_FULFILLED:
    case GET_FUNDING_UPLOADS_FULFILLED:
    case GET_ACTIVE_FUNDING_CONTRACTS_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_AVETMISS_EXPORT_OUTCOMES: {
      return {
        ...state,
        ...{ outcomes: null }
      };
    }

    case CLEAR_AVETMISS_EXPORT_UPLOADS: {
      return {
        ...state,
        ...{ uploads: undefined }
      };
    }

    case CLEAR_AVETMISS8_EXPORT_ID: {
      return {
        ...state,
        ...{ exportID: null, outcomesID: null }
      };
    }

    case GET_AVETMISS8_EXPORT_RESULTS_FULFILLED: {
      const url = window.URL.createObjectURL(new Blob([action.payload.exported]));
      const link = document.createElement("a");
      const fileName = formatDate(new Date(), YYYYMMDD_KKMMSS_MINUSED);

      link.href = url;
      link.setAttribute("download", fileName + ".zip");
      link.setAttribute("type", "application/zip");

      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      return { contracts: state.contracts };
    }

    case UPDATE_FUNDING_UPLOAD_FULFILLED: {
      const uploads = state.uploads.map(value => {
        if (value.id === action.payload.id) {
          value.status = action.payload.status;
        }
        return value;
      });

      return {
        ...state,
        ...{ uploads }
      };
    }

    default:
      return state;
  }
};
