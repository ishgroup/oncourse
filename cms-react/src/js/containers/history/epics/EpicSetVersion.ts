import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {SET_VERSION_FULFILLED, SET_VERSION_REQUEST} from "../actions";
import PublishService from "../../../services/PublishService";

const request: EpicUtils.Request<any, any> = {
  type: SET_VERSION_REQUEST,
  getData: (payload, state) => PublishService.setVersion(payload),
  processData: (response: any, state: any) => {
    return [
      {
        type: SET_VERSION_FULFILLED,
        payload: response,
      },
    ];
  },
};

export const EpicSetVersion: Epic<any, any> = EpicUtils.Create(request);
