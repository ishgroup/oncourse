import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {ContactAdd, ContactAddRequest} from "../containers/contact-add/actions/Actions";
import {Phase} from "../reducers/State";
import {changePhaseRequest} from "../actions/Actions";
import {Actions as WebActions} from "../../web/actions/Actions";
import {FULFILLED} from "../../common/actions/ActionUtils";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
const ContactAddEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(ContactAddRequest).flatMap((action) => {
    const result = [
      {type: ContactAdd, payload: action.payload},
      {type: FULFILLED(WebActions.REQUEST_CONTACT), payload: action.payload},
      changePhaseRequest(Phase.EditContactDetails)
    ];
    return result;
  });
};

export default ContactAddEpic;