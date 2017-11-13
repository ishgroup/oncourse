import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_LAYOUTS_REQUEST, GET_LAYOUTS_FULFILLED} from "../actions";
import {Layout} from "../../../../../model";
import ThemeService from "../../../../../services/ThemeService";

const request: EpicUtils.Request<any, any> = {
  type: GET_LAYOUTS_REQUEST,
  getData: (payload, state) => ThemeService.getLayouts(),
  processData: (layouts: Layout[], state: any) => {
    return [
      {
        type: GET_LAYOUTS_FULFILLED,
        payload: layouts,
      },
    ];
  },
};

export const EpicGetLayouts: Epic<any, any> = EpicUtils.Create(request);
