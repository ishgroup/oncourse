import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SET_REDIRECT_SETTINGS_REQUEST, SET_REDIRECT_SETTINGS_FULFILLED} from "../actions";
import SettingsService from "../../../../../services/SettingsService";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";

const request: EpicUtils.Request<any, any> = {
  type: SET_REDIRECT_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.setRedirectSettings(payload),
  processData: (redirectSettings: History, state: any) => {
    return [
      success(notificationParams),
      {
        type: SET_REDIRECT_SETTINGS_FULFILLED,
        payload: redirectSettings,
      },
    ];
  },
};

export const EpicSetRedirectSettings: Epic<any, any> = EpicUtils.Create(request);
