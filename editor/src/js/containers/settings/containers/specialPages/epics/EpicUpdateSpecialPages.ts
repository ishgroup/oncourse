import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SET_SPECIAL_PAGE_SETTINGS_REQUEST, SET_SPECIAL_PAGE_SETTINGS_FULFILLED} from "../actions";
import SettingsService from "../../../../../services/SettingsService";
import {SpecialPages} from "../../../../../../../build/generated-sources";

const request: EpicUtils.Request<any, any> = {
  type: SET_SPECIAL_PAGE_SETTINGS_REQUEST,
  getData: (specialPages: SpecialPages) => SettingsService.updateSpecialPages(specialPages),
  processData: (specialPages: SpecialPages) => {
    return [
      {
        type: SET_SPECIAL_PAGE_SETTINGS_FULFILLED,
        payload: specialPages,
      },
    ];
  },
};

export const EpicUpdateSpecialPages: Epic<any, any> = EpicUtils.Create(request);
