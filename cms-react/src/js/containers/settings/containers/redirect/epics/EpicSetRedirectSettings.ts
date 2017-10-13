import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SET_REDIRECT_SETTINGS_REQUEST, SET_REDIRECT_SETTINGS_FULFILLED} from "../actions";
import SettingsService from "../../../../../services/SettingsService";

const request: EpicUtils.Request<any, any> = {
  type: SET_REDIRECT_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.setRedirectSettings(payload),
  processData: (redirectSettings: History, state: any) => {
    return [
      {
        type: SET_REDIRECT_SETTINGS_FULFILLED,
        payload: redirectSettings,
      },
    ];
  },
};

export const EpicSetRedirectSettings: Epic<any, any> = EpicUtils.Create(request);
