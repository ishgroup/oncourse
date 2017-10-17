import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SET_CHECKOUT_SETTINGS_REQUEST, SET_CHECKOUT_SETTINGS_FULFILLED} from "../actions";
import SettingsService from "../../../../../services/SettingsService";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";

const request: EpicUtils.Request<any, any> = {
  type: SET_CHECKOUT_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.setCheckoutSettings(payload),
  processData: (checkoutSettings: any, state: any) => {
    return [
      success(notificationParams),
      {
        type: SET_CHECKOUT_SETTINGS_FULFILLED,
        payload: checkoutSettings,
      },
    ];
  },
};

export const EpicSetCheckoutSettings: Epic<any, any> = EpicUtils.Create(request);
