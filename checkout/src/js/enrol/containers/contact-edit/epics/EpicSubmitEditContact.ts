import "rxjs";
import {ActionsObservable, Epic} from "redux-observable";
import {IshState} from "../../../../services/IshState";
import {MiddlewareAPI} from "redux";
import {Observable} from "rxjs/Observable";
import {resetFieldsState, setFieldsToState, SUBMIT_EDIT_CONTACT} from "../actions/Actions";
import {Contact} from "../../../../model";
import {addContactToSummary} from "../../summary/actions/Actions";
import {changePhase, updateContactAddProcess} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";

export const SubmitEditContact: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any, IshState>): Observable<any> => {
  return action$.ofType(SUBMIT_EDIT_CONTACT).flatMap(action => {
    const state = store.getState();
    const contact: Contact = action.payload;
    const result = [];

    result.push(resetFieldsState());
    result.push(addContactToSummary(contact));

    const unfilled = state.checkout.fields.unfilled.filter(fields => fields.contactId !== action.payload.id);

    if (unfilled.length) {
      const nextFileds = unfilled[0];
      result.push(changePhase(Phase.Summary));
      result.push(setFieldsToState(nextFileds));
      result.push(updateContactAddProcess(state.checkout.contacts.entities.contact[nextFileds.contactId], Phase.Summary, null));
      result.push(changePhase(Phase.ComplementEditContact));
    }

    return result;
  });
};

