import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SET_WEBSITE_SETTINGS_FULFILLED, SET_WEBSITE_SETTINGS_REQUEST} from "../actions";
import SettingsService from "../../../../../services/SettingsService";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: SET_WEBSITE_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.setWebsiteSettings(payload),
  processData: (websiteSettings: any, state: any) => {
    return [
      {
        type: SHOW_MESSAGE,
        payload: {message: "Save success", success: true},
      },
      {
        type: SET_WEBSITE_SETTINGS_FULFILLED,
        payload: websiteSettings,
      },
    ];
  },
};

export const EpicSetWebsiteSettings: Epic<any, any> = EpicUtils.Create(request);
