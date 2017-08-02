import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "./EpicUtils";
import {SUBMIT_LOGIN_FORM} from "../containers/login/Actions/index";
import AuthService from "../services/AuthService";


const request: EpicUtils.Request<any, any> = {
  type: SUBMIT_LOGIN_FORM,
  getData: (payload, state) => AuthService.submitUser(payload),
  processData: (value: any, state: any) => {
    const result = [];
    return result;
  },
};

export const EpicLogin: Epic<any, any> = EpicUtils.Create(request);
