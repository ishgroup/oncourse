import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SET_CHECKOUT_SETTINGS_REQUEST, SET_CHECKOUT_SETTINGS_FULFILLED} from "../actions";
import SettingsService from "../../../../../services/SettingsService";
import {CheckoutSettings} from "../../../../../model";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: SET_CHECKOUT_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.setCheckoutSettings(payload),
  processData: (checkoutSettings: CheckoutSettings, state: any) => {
    return [
      {
        type: SHOW_MESSAGE,
        payload: {message: "Save success", success: true},
      },
      {
        type: SET_CHECKOUT_SETTINGS_FULFILLED,
        payload: checkoutSettings,
      },
    ];
  },
};

export const EpicSetCheckoutSettings: Epic<any, any> = EpicUtils.Create(request);
