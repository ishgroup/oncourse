import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {DELETE_THEME_FULFILLED, DELETE_THEME_REQUEST} from "../actions";
import {getHistoryInstance} from "../../../../../history";
import {URL} from "../../../../../routes";
import ThemeService from "../../../../../services/ThemeService";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

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
      {
        type: SHOW_MESSAGE,
        payload: {message: "Theme deleted", success: true},
      },
    ];
  },
};

export const EpicDeleteTheme: Epic<any, any> = EpicUtils.Create(request);
