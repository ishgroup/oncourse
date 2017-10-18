import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_WEBSITE_SETTINGS_FULFILLED, GET_WEBSITE_SETTINGS_REQUEST} from "../actions";
import SettingsService from "../../../../../services/SettingsService";

const request: EpicUtils.Request<any, any> = {
  type: GET_WEBSITE_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.getWebsiteSettings(),
  processData: (websiteSettings: any, state: any) => {
    return [
      {
        type: GET_WEBSITE_SETTINGS_FULFILLED,
        payload: websiteSettings,
      },
    ];
  },
};

export const EpicGetWebsiteSettings: Epic<any, any> = EpicUtils.Create(request);
