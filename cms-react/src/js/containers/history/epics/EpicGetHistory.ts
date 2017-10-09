import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {GET_VERSIONS_FULFILLED, GET_VERSIONS_REQUEST} from "../actions";
import HistoryService from "../../../services/HistoryService";

const request: EpicUtils.Request<any, any> = {
  type: GET_VERSIONS_REQUEST,
  getData: (payload, state) => HistoryService.getVersions(),
  processData: (history: History, state: any) => {
    return [
      {
        type: GET_VERSIONS_FULFILLED,
        payload: history,
      },
    ];
  },
};

export const EpicGetHistory: Epic<any, any> = EpicUtils.Create(request);
