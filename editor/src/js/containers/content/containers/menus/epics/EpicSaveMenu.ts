import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';
import {notificationParams} from "../../../../../common/utils/NotificationSettings";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SAVE_MENU_TREE_FULFILLED, SAVE_MENU_TREE_REQUEST} from "../actions";
import {MenuItem} from "../../../../../model";
import MenuService from "../../../../../services/MenuService";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_MENU_TREE_REQUEST,
  getData: (payload: MenuItem[], state) => MenuService.saveMenuItems(payload),
  processData: (items: MenuItem[], state: any) => {
    return [
      success(notificationParams),
      {
        type: SAVE_MENU_TREE_FULFILLED,
        payload: items,
      },
    ];
  },
  processError: data => {
    if (data.status && 400 === data.status) {
      return [
        // {
        //   type: SAVE_MENU_TREE_FULFILLED,
        // },
        EpicUtils.errorMessage(data),
      ];
    }

    return [
      EpicUtils.errorMessage(data),
    ];
  },
};

export const EpicSaveMenu: Epic<any, any> = EpicUtils.Create(request);
