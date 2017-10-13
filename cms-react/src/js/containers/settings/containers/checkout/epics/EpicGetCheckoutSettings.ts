import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_CHECKOUT_SETTINGS_REQUEST, GET_CHECKOUT_SETTINGS_FULFILLED} from "../actions";
import SettingsService from "../../../../../services/SettingsService";

const request: EpicUtils.Request<any, any> = {
  type: GET_CHECKOUT_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.getCheckoutSettings(),
  processData: (checkoutSettings: any, state: any) => {
    return [
      {
        type: GET_CHECKOUT_SETTINGS_FULFILLED,
        payload: checkoutSettings,
      },
    ];
  },
};

export const EpicGetCheckoutSettings: Epic<any, any> = EpicUtils.Create(request);
