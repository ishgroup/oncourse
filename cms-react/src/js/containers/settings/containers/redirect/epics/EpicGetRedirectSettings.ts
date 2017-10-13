import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_REDIRECT_SETTINGS_FULFILLED, GET_REDIRECT_SETTINGS_REQUEST} from "../actions";
import SettingsService from "../../../../../services/SettingsService";

const request: EpicUtils.Request<any, any> = {
  type: GET_REDIRECT_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.getRedirectSettings(),
  processData: (redirectSettings: History, state: any) => {
    return [
      {
        type: GET_REDIRECT_SETTINGS_FULFILLED,
        payload: redirectSettings,
      },
    ];
  },
};

export const EpicGetRedirectSettings: Epic<any, any> = EpicUtils.Create(request);
