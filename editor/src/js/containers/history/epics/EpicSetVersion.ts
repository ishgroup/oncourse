import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {getHistory, SET_VERSION_FULFILLED, SET_VERSION_REQUEST} from "../actions";
import PublishService from "../../../services/PublishService";
import {SHOW_MESSAGE} from "../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: SET_VERSION_REQUEST,
  getData: (payload) => PublishService.setVersion(payload.id, payload.status),
  processData: (response: any, state: any, payload) => {
    return [
      {
        type: SHOW_MESSAGE,
        payload: {message: `Draft reverted to version #${payload.id}`, success: true},
      },
      {
        type: SET_VERSION_FULFILLED,
        payload: response,
      },
      getHistory(),
    ];
  },
};

export const EpicSetVersion: Epic<any, any> = EpicUtils.Create(request);
