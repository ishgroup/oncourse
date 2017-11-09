import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';
import * as EpicUtils from "../../../epics/EpicUtils";
import {PUBLISH_REQUEST, PUBLISH_FULFILLED} from "../actions";
import PublishService from "../../../services/PublishService";
import {notificationParams} from "../../../common/utils/NotificationSettings";

const request: EpicUtils.Request<any, any> = {
  type: PUBLISH_REQUEST,
  getData: (payload, state) => PublishService.publish(),
  processData: (response: any, state: any) => {
    return [
      success({...notificationParams, title: 'Publish success'}),
      {
        type: PUBLISH_FULFILLED,
        payload: response,
      },
    ];
  },
};

export const EpicPublish: Epic<any, any> = EpicUtils.Create(request);
