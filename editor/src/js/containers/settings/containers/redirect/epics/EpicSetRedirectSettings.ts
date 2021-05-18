import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SET_REDIRECT_SETTINGS_REQUEST, SET_REDIRECT_SETTINGS_FULFILLED} from "../actions";
import SettingsService from "../../../../../services/SettingsService";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: SET_REDIRECT_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.setRedirectSettings(payload),
  processData: (redirectSettings: History, state: any) => {
    return [
      {
        type: SHOW_MESSAGE,
        payload: {message: "Save success", success: true},
      },
      {
        type: SET_REDIRECT_SETTINGS_FULFILLED,
        payload: redirectSettings,
      },
    ];
  },
  processError: response => {
    if (response.status && 400 === response.status) {
      return [
        {
          type: SET_REDIRECT_SETTINGS_FULFILLED,
          payload: response.data,
        },
        EpicUtils.errorMessage(response),
      ];
    }

    return [
      EpicUtils.errorMessage(response),
    ];
  },
};

export const EpicSetRedirectSettings: Epic<any, any> = EpicUtils.Create(request);
