import * as SummaryActions from "../actions/Actions";
import {ADD_CONTACT_NODE_TO_STATE} from "../actions/Actions";
import {ContactNodeToState, State} from "./State";
import {RESET_CHECKOUT_STATE} from "../../../actions/Actions";
import * as L from "lodash";
import {ContactNode} from "../../../../model/checkout/ContactNode";

export const Reducer = (state: State = ContactNodeToState([]), action: { type: string, payload: State }): State => {
  const ns: State = L.cloneDeep(state);

  switch (action.type) {
    case  SummaryActions.UPDATE_ITEM:
    case SummaryActions.SELECT_ITEM:
      action.payload.result.forEach((id) => {
        const stateNode: ContactNode = ns.entities.contactNodes[id];
        const payloadNode: ContactNode = action.payload.entities.contactNodes[id];
        stateNode.enrolments = L.concat(stateNode.enrolments, payloadNode.enrolments);
        stateNode.applications = L.concat(stateNode.applications, payloadNode.applications);
        stateNode.memberships = L.concat(stateNode.memberships, payloadNode.memberships);
        stateNode.articles = L.concat(stateNode.articles, payloadNode.articles);
        stateNode.vouchers = L.concat(stateNode.vouchers, ...payloadNode.vouchers);
      });
      mergePurchases(ns, action.payload);
      return ns;
    case ADD_CONTACT_NODE_TO_STATE:
      ns.result = [...ns.result, ...action.payload.result];
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
