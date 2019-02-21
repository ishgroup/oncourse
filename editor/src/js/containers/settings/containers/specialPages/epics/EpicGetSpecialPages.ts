import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_SPECIAL_PAGE_SETTINGS_REQUEST, GET_SPECIAL_PAGE_SETTINGS_FULFILLED} from "../actions";
import SettingsService from "../../../../../services/SettingsService";
import {SpecialPages} from "../../../../../../../build/generated-sources";

const request: EpicUtils.Request<any, any> = {
  type: GET_SPECIAL_PAGE_SETTINGS_REQUEST,
  getData: () => SettingsService.getSpecialPages(),
  processData: (specialPages: SpecialPages) => {
    return [
      {
        type: GET_SPECIAL_PAGE_SETTINGS_FULFILLED,
        payload: specialPages,
      },
    ];
  },
};

export const EpicGetSpecialPages: Epic<any, any> = EpicUtils.Create(request);
