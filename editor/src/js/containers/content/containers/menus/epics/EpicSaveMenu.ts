import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_MENU_ITEMS_REQUEST, SAVE_MENU_TREE_FULFILLED, SAVE_MENU_TREE_REQUEST} from "../actions";
import {MenuItem} from "../../../../../model";
import MenuService from "../../../../../services/MenuService";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_MENU_TREE_REQUEST,
  getData: (payload: MenuItem[], state) => MenuService.saveMenuItems(payload),
  processData: (items: MenuItem[], state: any) => {
    return [
      {
        type: SHOW_MESSAGE,
        payload: {message: "Save success", success: true},
      },
      {
        type: SAVE_MENU_TREE_FULFILLED,
        payload: items,
      },
      {
        type: GET_MENU_ITEMS_REQUEST
      },
    ];
  },
  processError: response => {
    if (response.status && 400 === response.status) {
      return [
        {
          type: SAVE_MENU_TREE_FULFILLED,
          payload: response.data,
        },
        {
          type: GET_MENU_ITEMS_REQUEST
        },
        EpicUtils.errorMessage(response),
      ];
    }

    return [
      EpicUtils.errorMessage(response),
    ];
  },
};

export const EpicSaveMenu: Epic<any, any> = EpicUtils.Create(request);
