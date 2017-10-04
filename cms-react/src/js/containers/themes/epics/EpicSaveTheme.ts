import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../epics/EpicUtils";
import {SAVE_THEME_REQUEST, SAVE_THEME_FULFILLED} from "../actions";
import {Theme} from "../../../model";
import ThemeService from "../../../services/ThemeService";
import {notificationParams} from "../../../common/utils/NotificationSettings";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_THEME_REQUEST,
  getData: (props, state) => ThemeService.saveTheme(props, state),
  processData: (theme: Theme[], state: any) => {
    console.log(state);
    return [
      success(notificationParams),
      {
        payload: theme,
        type: SAVE_THEME_FULFILLED,
      },
    ];
  },
};

export const EpicSaveTheme: Epic<any, any> = EpicUtils.Create(request);
