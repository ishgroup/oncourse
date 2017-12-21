import ReactDOM from "react-dom";
import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {LOG_OUT_FULFILLED, LOG_OUT_REQUEST} from "../actions";
import AuthService from "../../../services/AuthService";
import {User} from "../../../model";
import {CookieService} from "../../../services/CookieService";
import {DefaultConfig} from "../../../constants/Config";


const request: EpicUtils.Request<any, any> = {
  type: LOG_OUT_REQUEST,
  getData: (payload, state) => AuthService.logout(),
  processData: (user: User, state: any) => {

    // remove CMS Cookie and unmount CMS app
    ReactDOM.unmountComponentAtNode(document.getElementById(DefaultConfig.CONTAINER_ID));
    CookieService.delete(DefaultConfig.COOKIE_NAME);

    return [
      {
        type: LOG_OUT_FULFILLED,
        payload: user,
      },
    ];
  },
};

export const EpicLogout: Epic<any, any> = EpicUtils.Create(request);
