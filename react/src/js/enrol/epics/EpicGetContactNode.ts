import "rxjs";
import * as Actions from "../containers/summary/actions/Actions";
import {addContactNodeToState} from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import {Create, Request} from "./EpicUtils";
import {Contact} from "../../model/web/Contact";
import {Epic} from "redux-observable";
import {ContactNode} from "../../model/checkout/ContactNode";
import {getAmount} from "../actions/Actions";

const request: Request<ContactNode, IshState> = {
  type: Actions.GET_CONTACT_NODE_FROM_BACKEND,
  getData: (contact: Contact, state: IshState) => CheckoutService.getContactNode(contact, state.cart),
  processData: (value: ContactNode, state: IshState) => {
    return [
      addContactNodeToState(value),
      getAmount(),
    ];
  },
};

export const GetContactNode: Epic<any, IshState> = Create(request);
