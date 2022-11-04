import { change, untouch } from "redux-form";
import { FETCH_FAIL, SET_LOGIN_STATE } from "../../../common/actions/index";
import { IAction } from "../../../common/actions/IshAction";
import { LoginResponse, ValidationError } from "@api/model";
import { LoginState } from "../reducers/state";

interface loginResponse {
  data: LoginResponse;
  status: number;
}

let tokenRequiredFirst = true;

const LoginServiceErrorsHandler = (response: loginResponse, customMessage?: string): IAction<any>[] => {
  if (!response || !response.data || !response.status) {
    return [
      {
        type: FETCH_FAIL,
        payload: {message: customMessage || "Server is not available"}
      }
    ];
  }

  const { data, status } = response;

  let clearTokenFieldsAction;
  let hideError = false;
  let errorMessage = data.errorMessage || data.loginStatus || "Wrong Username / Password";
  const loginState: LoginState = {};

  switch (status) {
    case 400:
      const errorData = data as ValidationError;

      if (errorData.id === "complexPassword") {
        loginState.isNewPassword = true;
        loginState.isUpdatePassword = true;
        loginState.strongPasswordValidation = true;
      }

      if (errorData.id === "weakPassword") {
        loginState.isNewPassword = true;
        loginState.isUpdatePassword = true;
      }

      const clearConfirmFieldActions = [
        change("LoginForm", "newPasswordConfirm", ""),
        untouch("LoginForm", "newPasswordConfirm")
      ];

      return [
        {
          type: SET_LOGIN_STATE,
          payload: loginState
        },
        ...clearConfirmFieldActions,
        {
          type: FETCH_FAIL,
          payload: { message: errorData.errorMessage || "Something went wrong" }
        }
      ];

    case 401:
      switch (data.loginStatus) {
        case "Token required": {
          hideError = tokenRequiredFirst;
          tokenRequiredFirst = false;
          loginState.isTOTP = true;
          break;
        }
        case "Concurrent sessions found": {
          hideError = true;
          loginState.isKickOut = true;
          break;
        }
        case "Password outdated":
          loginState.passwordChangeMessage = "Your password is outdated.";
          if (data.passwordComlexity) {
            loginState.isNewPassword = true;
            loginState.strongPasswordValidation = true;
            break;
          }
          loginState.isNewPassword = true;
          break;
        case "Forced password update":
          loginState.passwordChangeMessage = "Password change is required by Admin.";
          if (data.passwordComlexity) {
            loginState.isNewPassword = true;
            loginState.strongPasswordValidation = true;
            break;
          }
          loginState.isNewPassword = true;
          break;
        case "Weak password": {
          if (data.passwordComlexity) {
            loginState.isNewPassword = true;
            loginState.strongPasswordValidation = true;
            break;
          }
          loginState.isNewPassword = true;
          break;
        }
        case "TFA required": {
          loginState.totpUrl = data && data.totpUrl;
          loginState.isEnableTOTP = true;
          break;
        }
        case "TFA optional": {
          errorMessage = null;
          loginState.totpUrl = data && data.totpUrl;
          loginState.isOptionalTOTP = true;
          break;
        }
        case "Invalid or expired token": {
          clearTokenFieldsAction = change("LoginForm", "authCodeDigits", Array.of("", "", "", "", "", ""));
          break;
        }
        case "Eula required": {
          hideError = true;
          loginState.eulaUrl = data.eulaUrl;
          loginState.isBasic = true;
        }
      }

      const errorResponse = {
        type: FETCH_FAIL,
        payload: {
          message: hideError ? "" : errorMessage
        }
      };

      hideError = false;

      const actions = clearTokenFieldsAction ? [clearTokenFieldsAction, errorResponse] : [errorResponse];

      return Object.keys(loginState).length === 0
        ? [...actions]
        : [
            {
              type: SET_LOGIN_STATE,
              payload: loginState
            },
            ...actions
          ];

    default:
      return [
        {
          type: FETCH_FAIL,
          payload: { message: customMessage || "Login failed" }
        }
      ];
  }
};

export default LoginServiceErrorsHandler;
