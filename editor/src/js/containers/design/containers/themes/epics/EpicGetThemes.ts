import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_THEMES_REQUEST, GET_THEMES_FULFILLED} from "../actions";
import {Theme} from "../../../../../model";
import ThemeService from "../../../../../services/ThemeService";

const request: EpicUtils.Request<any, any> = {
  type: GET_THEMES_REQUEST,
  getData: (payload, state) => ThemeService.getThemes(),
  processData: (themes: Theme[], state: any) => {
    return [
      {
        type: GET_THEMES_FULFILLED,
        payload: themes,
      },
    ];
  },
};

export const EpicGetThemes: Epic<any, any> = EpicUtils.Create(request);
