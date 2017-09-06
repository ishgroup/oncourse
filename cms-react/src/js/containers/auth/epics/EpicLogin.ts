import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {SUBMIT_LOGIN_FORM_FULFILLED, SUBMIT_LOGIN_FORM_REQUEST} from "../actions";
import AuthService from "../../../services/AuthService";
import {User} from "../../../model";


const request: EpicUtils.Request<any, any> = {
  type: SUBMIT_LOGIN_FORM_REQUEST,
  getData: (payload, state) => AuthService.submitUser(payload),
  processData: (user: User, state: any) => {
    return [
      {
        type: SUBMIT_LOGIN_FORM_FULFILLED,
        payload: user,
      },
    ];
  },
};

export const EpicLogin: Epic<any, any> = EpicUtils.Create(request);
