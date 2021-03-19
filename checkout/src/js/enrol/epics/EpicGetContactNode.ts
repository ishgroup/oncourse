import "rxjs";
import * as Actions from "../containers/summary/actions/Actions";
import {IshState} from "../../services/IshState";
import {Create, Request} from "../../common/epics/EpicUtils";
import {Contact, ContactNode} from "../../model";
import {Epic} from "redux-observable";
import {getCheckoutModelFromBackend} from "../actions/Actions";
import CheckoutServiceV2 from "../services/CheckoutServiceV2";
import {ContactNodeService} from "../services/ContactNodeService";

const request: Request<ContactNode, IshState> = {
  type: Actions.GET_CONTACT_NODE_AND_MODEL_FROM_BACKEND,
  getData: (payload: {contact: Contact, uncheckItems?: boolean}, state: IshState) => CheckoutServiceV2.getContactNode(payload.contact, state.checkout.summary,  state.cart, state.checkout.payerId),
  processData: (value: ContactNode, state: IshState, payload) => {
    if (payload.uncheckItems) {
      // set selected to false for all items
      const itemTypes = ['enrolments', 'waitingLists', 'articles', 'vouchers', 'applications', 'memberships'];
      itemTypes.forEach(type => {
        if (value[type] && value[type].length) {
          value[type] = value[type].map(item => ({...item, selected: false}));
        }
      });
    }
    const relationsUpdateActions = ContactNodeService.getRelationsUpdateActions([value]);

    return [
      Actions.addContactNodeToState(value),
      getCheckoutModelFromBackend(),
      ...relationsUpdateActions
    ];
  },
};

export const GetContactNode: Epic<any, IshState> = Create(request);
