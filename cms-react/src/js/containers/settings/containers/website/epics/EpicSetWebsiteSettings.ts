import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SET_WEBSITE_SETTINGS_FULFILLED, SET_WEBSITE_SETTINGS_REQUEST} from "../actions";
import SettingsService from "../../../../../services/SettingsService";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";

const request: EpicUtils.Request<any, any> = {
  type: SET_WEBSITE_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.setWebsiteSettings(payload),
  processData: (websiteSettings: any, state: any) => {
    return [
      success(notificationParams),
      {
        type: SET_WEBSITE_SETTINGS_FULFILLED,
        payload: websiteSettings,
      },
    ];
  },
};

export const EpicSetWebsiteSettings: Epic<any, any> = EpicUtils.Create(request);
