import * as SummaryActions from "../actions/Actions";
import {ContactNodeToState, State} from "./State";
import {RESET_CHECKOUT_STATE} from "../../../actions/Actions";
import * as L from "lodash";
import {ContactNode} from "../../../../model/checkout/ContactNode";

export const Reducer = (state: State = ContactNodeToState([]), action: { type: string, payload: State }): State => {
  const ns: State = L.cloneDeep(state);

  switch (action.type) {

    case  SummaryActions.UPDATE_ITEM:
      action.payload.result.forEach(id => {
        const stateNode: ContactNode = ns.entities.contactNodes[id];
        const payloadNode: ContactNode = action.payload.entities.contactNodes[id];
        stateNode.enrolments = Array.from(new Set([...stateNode.enrolments || [], ...payloadNode.enrolments]));
        stateNode.applications = Array.from(new Set([...stateNode.applications || [], ...payloadNode.applications]));
        stateNode.memberships = Array.from(new Set([...stateNode.memberships || [], ...payloadNode.memberships]));
        stateNode.articles = Array.from(new Set([...stateNode.articles || [], ...payloadNode.articles]));
        stateNode.vouchers = Array.from(new Set([...stateNode.vouchers || [], ...payloadNode.vouchers]));
      });
      mergePurchases(ns, action.payload);
      return ns;

    case SummaryActions.ADD_CONTACT_NODE_TO_STATE:
      ns.result = Array.from(new Set([...ns.result, ...action.payload.result]));
      ns.entities.contactNodes = {...ns.entities.contactNodes, ...action.payload.entities.contactNodes};
      mergePurchases(ns, action.payload);
      return ns;

    case SummaryActions.ItemsLoad:
      return action.payload;

    case RESET_CHECKOUT_STATE:
      return ContactNodeToState([]);

    default:
      return state;
  }
};

const mergePurchases = (ns: State, payload: State): State => {
  ns.entities.enrolments = {...ns.entities.enrolments, ...payload.entities.enrolments};
  ns.entities.applications = {...ns.entities.applications, ...payload.entities.applications};
  ns.entities.memberships = {...ns.entities.memberships, ...payload.entities.memberships};
  ns.entities.articles = {...ns.entities.articles, ...payload.entities.articles};
  ns.entities.vouchers = {...ns.entities.vouchers, ...payload.entities.vouchers};
  return ns;
};
