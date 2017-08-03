import "rxjs";
import * as Actions from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import {Create, Request} from "./EpicUtils";
import {Contact, ContactNode} from "../../model";
import {Epic} from "redux-observable";
import {getAmount, getCheckoutModelFromBackend} from "../actions/Actions";

const request: Request<ContactNode, IshState> = {
  type: Actions.GET_CONTACT_NODE_AND_MODEL_FROM_BACKEND,
  getData: (contact: Contact, state: IshState) => CheckoutService.getContactNode(contact, state.cart),
  processData: (value: ContactNode, state: IshState) => {
    return [
      Actions.addContactNodeToState(value),
      getCheckoutModelFromBackend(),
    ];
  },
};

export const GetContactNode: Epic<any, IshState> = Create(request);
