import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {SHOW_MESSAGES, SHOW_MESSAGES_REQUEST} from "../actions/Actions";
import {stopSubmit} from "redux-form";
import {ValidationError} from "../../model";
import {isCommonError, isPlainTextError, isValidationError, toValidationError} from "../../common/utils/ErrorUtils";


const MessagesShowEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(SHOW_MESSAGES_REQUEST).flatMap(action => {
    const messages: ValidationError = toValidationError(action.payload);
    const form: string = action.meta.form;
    const _formErrors = {};
    messages.fieldsErrors.forEach(e => {
      _formErrors[e.name] = e.error;
    });
    return [stopSubmit(form, _formErrors), {type: SHOW_MESSAGES, payload: messages}];
  });
};


export default MessagesShowEpic;
