import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {GET_HISTORY_FULFILLED, GET_HISTORY_REQUEST} from "../actions";
import HistoryService from "../../../services/HistoryService";

const request: EpicUtils.Request<any, any> = {
  type: GET_HISTORY_REQUEST,
  getData: (payload, state) => HistoryService.getHistory(),
  processData: (history: History, state: any) => {
    return [
      {
        type: GET_HISTORY_FULFILLED,
        payload: history,
      },
    ];
  },
};

export const EpicGetHistory: Epic<any, any> = EpicUtils.Create(request);
