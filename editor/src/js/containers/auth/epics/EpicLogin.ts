import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {getUser, SUBMIT_LOGIN_FORM_FULFILLED, SUBMIT_LOGIN_FORM_REQUEST} from "../actions";
import AuthService from "../../../services/AuthService";
import {CookieService} from "../../../services/CookieService";
import {DefaultConfig} from "../../../constants/Config";


const request: EpicUtils.Request<any, any> = {
  type: SUBMIT_LOGIN_FORM_REQUEST,
  getData: (payload, state) => AuthService.login(payload),
  processData: (state: any) => {

    // add CMS Cookie
    CookieService.set(DefaultConfig.COOKIE_NAME, 'true');

    if (document.location.hash === "#editor") {
      history.replaceState("", document.title, window.location.pathname + window.location.search);
    }

    return [
      {
        type: SUBMIT_LOGIN_FORM_FULFILLED,
      }, 
      getUser(),
    ];
  },
};

export const EpicLogin: Epic<any, any> = EpicUtils.Create(request);
