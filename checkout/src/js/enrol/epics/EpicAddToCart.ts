import "rxjs";
import {Store} from "redux";
import {Observable} from "rxjs";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Phase} from "../reducers/State";
import {setFieldsToState} from "../containers/contact-edit/actions/Actions";
import {changePhase, updateContactAddProcess} from "../actions/Actions";
import {Actions as WebActions} from "../../web/actions/Actions";


export const EpicAddToCart = (action$, store: Store<IshState>): any => {
  return action$.ofType(
    FULFILLED(WebActions.ADD_WAITING_COURSE_TO_CART),
    FULFILLED(WebActions.ADD_CLASS_TO_CART),
    FULFILLED(WebActions.ADD_PRODUCT_TO_CART),
  )
    .flatMap(actions => {
      const state = store.getState();

      if (!state.checkout.contacts.result.length) return [];

      const contacts = Object.values(state.checkout.contacts.entities.contact);

      return Observable.combineLatest(contacts.map(contact => {
        return Observable
          .fromPromise(CheckoutService.loadFields(contact, state))
          .flatMap(data => Observable.of({data, contact}))
          .catch(data => {
            console.warn('Error: load fields for contacts');
            console.warn(data);
            return [];
          });
      })).mergeMap((value: any) => {
        return Observable.of(value).switch().flatMap(val => {

          if (val.data.headings.length > 0) {
            return [
              setFieldsToState(val.data),
              updateContactAddProcess(val.contact, state.checkout.phase, null),
              changePhase(Phase.EditContact),
            ];
          }

          return [];

        });

      });

    });
};

