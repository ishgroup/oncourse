import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_MENU_ITEMS_REQUEST, GET_MENU_ITEMS_FULFILLED} from "../actions";
import {MenuItem} from "../../../../../model";
import MenuService from "../../../../../services/MenuService";

const request: EpicUtils.Request<any, any> = {
  type: GET_MENU_ITEMS_REQUEST,
  getData: (payload, state) => MenuService.getMenuItems(),
  processData: (items: MenuItem[], state: any) => {
    return [
      {
        type: GET_MENU_ITEMS_FULFILLED,
        payload: items,
      },
    ];
  },
};

export const EpicGetMenu: Epic<any, any> = EpicUtils.Create(request);
