import {Epic} from "redux-observable";
import "rxjs";
import {success} from 'react-notification-system-redux';

import * as EpicUtils from "../../../../../epics/EpicUtils";
import {ADD_THEME_REQUEST, ADD_THEME_FULFILLED} from "../actions";
import {Theme} from "../../../../../model";
import ThemeService from "../../../../../services/ThemeService";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";
import {URL} from "../../../../../routes";
import {getHistoryInstance} from "../../../../../history";

const request: EpicUtils.Request<any, any> = {
  type: ADD_THEME_REQUEST,
  getData: (props, state) => ThemeService.addTheme(),
  processData: (theme: Theme, state: any) => {

    getHistoryInstance().push(`${URL.THEMES}/${theme.id}`);

    return [
      success({...notificationParams, title: 'New Theme added'}),
      {
        payload: theme,
        type: ADD_THEME_FULFILLED,
      },
    ];
  },
};

export const EpicAddTheme: Epic<any, any> = EpicUtils.Create(request);
