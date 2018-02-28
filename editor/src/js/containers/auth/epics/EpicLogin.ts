import {ActionsObservable, Epic} from "redux-observable";
import "rxjs";
import {Observable} from "rxjs/Observable";
import {MiddlewareAPI} from "redux";
import * as EpicUtils from "../../../epics/EpicUtils";
import {getUser, PAGE_RELOAD, pageReload, SUBMIT_LOGIN_FORM_FULFILLED, SUBMIT_LOGIN_FORM_REQUEST} from "../actions";
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
      pageReload(),
    ];
  },
};

export const EpicLogin: Epic<any, any> = EpicUtils.Create(request);


export const EpicLoggedIn: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(PAGE_RELOAD).flatMap(action => {
    setTimeout(() => document.location.reload(true), 300);
    return [];
  });
};

