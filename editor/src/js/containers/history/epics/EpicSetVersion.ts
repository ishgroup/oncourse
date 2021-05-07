import {Epic} from "redux-observable";
import {success} from 'react-notification-system-redux';
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {getHistory, SET_VERSION_FULFILLED, SET_VERSION_REQUEST} from "../actions";
import PublishService from "../../../services/PublishService";
import {notificationParams} from "../../../common/utils/NotificationSettings";

const request: EpicUtils.Request<any, any> = {
  type: SET_VERSION_REQUEST,
  getData: (payload) => PublishService.setVersion(payload.id, payload.status),
  processData: (response: any, state: any, payload) => {
    return [
      success({...notificationParams,title: `Draft reverted to version #${payload.id}`}),
      {
        type: SET_VERSION_FULFILLED,
        payload: response,
      },
      getHistory(),
    ];
  },
};

export const EpicSetVersion: Epic<any, any> = EpicUtils.Create(request);
