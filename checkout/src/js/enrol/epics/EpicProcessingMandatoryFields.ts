import "rxjs";
import {Store} from "redux";
import {Observable} from "rxjs";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import {Phase} from "../reducers/State";
import {setFieldsToState} from "../containers/contact-edit/actions/Actions";
import {changePhase, updateContactAddProcess} from "../actions/Actions";
import {PROCESSING_MANDATORY_FIELDS} from "../containers/payment/actions/Actions";

/**
 *
 * Checking new mandatory fields for new enrolments for each contact before payment,
 * then redirecting to contact details or payment phase
 *
 * **/

export const EpicProcessingMandatoryFields = (action$, store: Store<IshState>): any => {
  return action$.ofType(PROCESSING_MANDATORY_FIELDS)
    .flatMap(actions => {
      const state = store.getState();

      if (!state.checkout.contacts.result.length) return [];

      const contacts = Object.values(state.checkout.contacts.entities.contact);

      return Observable.combineLatest(
        contacts
          .filter(contact => CheckoutService.haveContactSelectedItems(contact, state.checkout.summary))
          .map(contact => {
            return Observable
              .fromPromise(CheckoutService.loadFieldsForSelected(contact, state))
              .flatMap(data => Observable.of({data, contact}))
              .catch(data => {
                console.warn('Error: load fields for contact');
                console.warn(data);

                return [changePhase(Phase.Payment)];
              });
          }))
        .mergeMap((value: any) => {
          return Observable.of(value).switch().flatMap(val => {

            if (val.data.headings.length > 0) {
              return [
                setFieldsToState(val.data),
                updateContactAddProcess(val.contact, state.checkout.phase, null),
                changePhase(Phase.ComplementEditContact),
              ];
            }

            return [changePhase(Phase.Payment)];

          });

        });

    });
};

