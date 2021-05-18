import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SAVE_THEME_REQUEST, SAVE_THEME_FULFILLED} from "../actions";
import {Theme} from "../../../../../model";
import ThemeService from "../../../../../services/ThemeService";
import {URL} from "../../../../../routes";
import {getHistoryInstance} from "../../../../../history";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: SAVE_THEME_REQUEST,
  getData: (props, state) => ThemeService.saveTheme(props, state),
  processData: (theme: Theme, state: any) => {

    getHistoryInstance().push(`${URL.THEMES}/${theme.id}`);

    return [
      {
        type: SHOW_MESSAGE,
        payload: {message: "Save success", success: true},
      },
      {
        payload: theme,
        type: SAVE_THEME_FULFILLED,
      },
    ];
  },
};

export const EpicSaveTheme: Epic<any, any> = EpicUtils.Create(request);
