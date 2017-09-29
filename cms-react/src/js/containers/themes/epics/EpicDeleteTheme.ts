import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../epics/EpicUtils";
import {DELETE_THEME_FULFILLED, DELETE_THEME_REQUEST} from "../actions";
import {getHistoryInstance} from "../../../history";
import {URL} from "../../../routes";
import {notificationParams} from "../../../common/utils/NotificationSettings";
import ThemeService from "../../../services/ThemeService";

const request: EpicUtils.Request<any, any> = {
  type: DELETE_THEME_REQUEST,
  getData: (payload, state) => ThemeService.deleteTheme(payload),
  processData: (theme: any, state: any, payload) => {

    getHistoryInstance().push(URL.THEMES);

    return [
      {
        payload,
        type: DELETE_THEME_FULFILLED,
      },
      success({...notificationParams, title: "Theme deleted"}),
    ];
  },
};

export const EpicDeleteTheme: Epic<any, any> = EpicUtils.Create(request);
