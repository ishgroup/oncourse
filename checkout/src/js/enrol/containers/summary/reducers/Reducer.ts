import * as SummaryActions from "../actions/Actions";
import {ContactNodeStorage, ContactNodeToState, State} from "./State";
import {RESET_CHECKOUT_STATE, SHOW_MESSAGES} from "../../../actions/Actions";
import * as L from "lodash";
import {IAction} from "../../../../actions/IshAction";
import {FULFILLED} from "../../../../common/actions/ActionUtils";

export const Reducer = (state: State = ContactNodeToState([]), action: IAction<State>): State => {
  const ns: State = L.cloneDeep(state);

  switch (action.type) {

    case  SummaryActions.UPDATE_ITEM:
      action.payload.result.forEach(id => {
        const stateNode: ContactNodeStorage = ns.entities.contactNodes[id];
        const payloadNode: ContactNodeStorage = action.payload.entities.contactNodes[id];
        stateNode.enrolments = Array.from(new Set([...stateNode.enrolments || [], ...payloadNode.enrolments]));
        stateNode.applications = Array.from(new Set([...stateNode.applications || [], ...payloadNode.applications]));
        stateNode.memberships = Array.from(new Set([...stateNode.memberships || [], ...payloadNode.memberships]));
        stateNode.articles = Array.from(new Set([...stateNode.articles || [], ...payloadNode.articles]));
        stateNode.vouchers = Array.from(new Set([...stateNode.vouchers || [], ...payloadNode.vouchers]));
        stateNode.waitingLists = Array.from(new Set([...stateNode.waitingLists || [], ...payloadNode.waitingLists]));
      });
      mergePurchases(ns, action.payload, false);
      return ns;

    case SummaryActions.ADD_CONTACT_NODE_TO_STATE:
      ns.result = Array.from(new Set([...ns.result, ...action.payload.result]));
      ns.entities.contactNodes = {...ns.entities.contactNodes, ...action.payload.entities.contactNodes};
      mergePurchases(ns, action.payload, true);
      return ns;

    case SummaryActions.REWRITE_CONTACT_NODE_TO_STATE:
      ns.result = Array.from(new Set([...ns.result, ...action.payload.result]));
      ns.entities.contactNodes = {...ns.entities.contactNodes, ...action.payload.entities.contactNodes};
      mergePurchases(ns, action.payload, false);
      return ns;

    case SummaryActions.ItemsLoad:
      return action.payload;

    case RESET_CHECKOUT_STATE:
      return ContactNodeToState([]);

    case SummaryActions.SELECT_ITEM_REQUEST:
      return {
        ...state,
        fetching: true,
      };

    case FULFILLED(SummaryActions.SELECT_ITEM_REQUEST):
    case SHOW_MESSAGES:
      return {
        ...state,
        fetching: false,
      };

    default:
      return state;
  }
};

const mergePurchases = (ns: State, payload: State, leaveExisting: boolean): State => {
  ns.entities.enrolments = leaveExisting
    ? {...payload.entities.enrolments, ...ns.entities.enrolments}
    : {...ns.entities.enrolments, ...payload.entities.enrolments};
  ns.entities.applications = leaveExisting
    ? {...payload.entities.applications, ...ns.entities.applications}
    : {...ns.entities.applications, ...payload.entities.applications};
  ns.entities.memberships = leaveExisting
    ? {...payload.entities.memberships, ...ns.entities.memberships}
    : {...ns.entities.memberships, ...payload.entities.memberships};
  ns.entities.articles = leaveExisting
    ? {...payload.entities.articles, ...ns.entities.articles}
    : {...ns.entities.articles, ...payload.entities.articles};
  ns.entities.vouchers = leaveExisting
    ? {...payload.entities.vouchers, ...ns.entities.vouchers}
    : {...ns.entities.vouchers, ...payload.entities.vouchers};
  ns.entities.waitingLists = leaveExisting
    ? {...payload.entities.waitingLists, ...ns.entities.waitingLists}
    : {...ns.entities.waitingLists, ...payload.entities.waitingLists};
  return ns;
};
