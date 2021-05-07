import {Epic} from "redux-observable";
import "rxjs";
import {success, error} from 'react-notification-system-redux';
import * as EpicUtils from "../../../epics/EpicUtils";
import {PUBLISH_REQUEST, PUBLISH_FULFILLED} from "../actions";
import PublishService from "../../../services/PublishService";
import {notificationParams} from "../../../common/utils/NotificationSettings";
import {REJECTED} from "../../../common/actions/ActionUtils";

const request: EpicUtils.Request<any, any> = {
  type: PUBLISH_REQUEST,
  getData: (payload) => PublishService.setVersion(payload.id, payload.status, payload.description),
  processData: (response: any, state: any) => {
    return [
      success({
        ...notificationParams,
        title: 'Your site is being published now. It will take a few minutes before the world sees your changes.',
      }),
      {
        type: PUBLISH_FULFILLED,
        payload: response,
      },
    ];
  },
  processError: response => {
    if (response.status && 423 === response.status) {
      return [
        {
          type: REJECTED(PUBLISH_REQUEST),
        },
        error({
          ...notificationParams,
          title: 'You cannot publish your site now. Please wait a few minutes before trying again.',
        }),
      ];
    }

    return [
      EpicUtils.errorMessage(response),
    ];
  },
};

export const EpicPublish: Epic<any, any> = EpicUtils.Create(request);
