import {ActionsObservable, Epic, StateObservable} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {PAGE_RELOAD, pageReload, SUBMIT_LOGIN_FORM_FULFILLED, SUBMIT_LOGIN_FORM_REQUEST} from "../actions";
import AuthService from "../../../services/AuthService";
import {CookieService} from "../../../services/CookieService";
import {DefaultConfig} from "../../../constants/Config";
import {mergeMap} from "rxjs/operators";
import {State} from "../../../reducers/state";
import {Observable} from "rxjs";

const request: EpicUtils.Request<any, any> = {
  type: SUBMIT_LOGIN_FORM_REQUEST,
  getData: (payload, state) => AuthService.login(payload),
  processData: (state: any, s, p) => {

    // add CMS Cookie
    CookieService.set(DefaultConfig.COOKIE_NAME, 'true');

    if (document.location.hash === "#editor") {
      history.replaceState("", document.title, window.location.pathname + window.location.search);
    }

    return [
      {
        type: SUBMIT_LOGIN_FORM_FULFILLED,
        payload: {
          credentials: p,
        },
      },
      pageReload(),
    ];
  },
};

export const EpicLogin: Epic<any, any> = EpicUtils.Create(request);


export const EpicLoggedIn: Epic<any, State> = (action$: ActionsObservable<any>, state$: StateObservable<State>): Observable<any> => {
  return action$.ofType(PAGE_RELOAD).pipe(
    mergeMap(action => {
      setTimeout(() => document.location.reload(true), 300);
      return [];
    })
  );
};

