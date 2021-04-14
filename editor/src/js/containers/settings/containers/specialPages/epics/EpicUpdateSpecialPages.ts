import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SET_SPECIAL_PAGE_SETTINGS_REQUEST, SET_SPECIAL_PAGE_SETTINGS_FULFILLED} from "../actions";
import SettingsService from "../../../../../services/SettingsService";
import {SpecialPages} from "../../../../../../../build/generated-sources";
import {ProcessError} from "../../../../../epics/EpicUtils";

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
  processError: response => {
    return response?.data?.rules ? [
      {
        type: SET_SPECIAL_PAGE_SETTINGS_FULFILLED,
        payload: response.data,
      },
    ] : ProcessError(response);
  },
};

export const EpicUpdateSpecialPages: Epic<any, any> = EpicUtils.Create(request);
