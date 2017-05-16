import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";

import * as Actions from "../actions/Actions";
import {ContactAdd} from "../actions/Actions";
import {Phase} from "../reducers/State";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
const ContactAddEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(Actions.ContactAddRequest).flatMap((action) => {
    const result = [
        {type: ContactAdd, payload: action.payload},
        {type: Actions.PhaseChange, payload: Phase.EditContactDetails},
        {type: Actions.FieldsLoadRequest},
      ];
    return result;
  });
};

export default ContactAddEpic;