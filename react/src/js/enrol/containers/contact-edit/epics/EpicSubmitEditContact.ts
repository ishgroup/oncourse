import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import {resetFieldsState, SUBMIT_EDIT_CONTACT} from "../actions/Actions";
import {Contact} from "../../../../model/web/Contact";
import {addContactToSummary} from "../../summary/actions/Actions";
import {setParent} from "../../../actions/Actions";

export const SubmitEditContact: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(SUBMIT_EDIT_CONTACT).flatMap(action => {
    const contact: Contact = action.payload;
    const state = store.getState();
    const result = [];

    // if (contact.parentRequired && state.checkout.parentId === contact.id) {
    //   result.push(setParent(null));
    // }

    result.push(resetFieldsState());
    result.push(addContactToSummary(contact));

    return result;
  });
};

