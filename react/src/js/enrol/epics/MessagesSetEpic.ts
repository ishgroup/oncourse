import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {MessagesSet, MessagesSetRequest} from "../actions/Actions";
import {stopSubmit} from "redux-form";
import {ValidationError} from "../../model/common/ValidationError";


const Epic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(MessagesSetRequest).flatMap((action) => {
    const data: ValidationError = action.payload.data;
    const form: string = action.meta.form;
    const _formErrors = {};
    data.fieldsErrors.forEach((e) => {
      _formErrors[e.name] = e.error;
    });
    return [stopSubmit(form, _formErrors), {type: MessagesSet, payload: data}]
  });
};

export default Epic;