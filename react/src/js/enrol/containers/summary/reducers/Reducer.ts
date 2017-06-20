import * as SummaryActions from "../actions/Actions";
import {UPDATE_CONTACT_NODE} from "../actions/Actions";
import {ContactNodeToState, State} from "./State";
import {RESET_CHECKOUT_STATE} from "../../../actions/Actions";
import * as L from "lodash";
import {ContactNode} from "../../../../model/checkout/ContactNode";

export const Reducer = (state: State = ContactNodeToState([]), action: { type: string, payload: State }): State => {
  switch (action.type) {
    case SummaryActions.SELECT_ITEM:
      return merge(state, action.payload);
    case SummaryActions.ItemsLoad:
      return action.payload;
    case UPDATE_CONTACT_NODE:
      return action.payload;
    case RESET_CHECKOUT_STATE:
      return ContactNodeToState([]);
    default:
      return state;
  }
};

enum Case {
  addContact,
  addItem,
  updateItem,
  refresh,
}

const getCase = (state: State, payload: State): Case => {
  const contactId: string = payload.result[0];

  if (state.result.length == 0) {
    return Case.refresh;
  } else if (state.result.indexOf(contactId) < 0) {
    return Case.addContact;
  }
  
  const enrolments = payload.entities.contactNodes[contactId].enrolments;
  const applications = payload.entities.contactNodes[contactId].applications;
  const memberships = payload.entities.contactNodes[contactId].memberships;
  const articles = payload.entities.contactNodes[contactId].articles;

  if (enrolments.length > 0) {
    if (L.isNil(state.entities.enrolments[enrolments[0]])) {
      return Case.addItem;
    } else {
      return Case.updateItem;
    }
  } else if (applications.length > 0)  {
    if (L.isNil(state.entities.applications[applications[0]])) {
      return Case.addItem;
    } else {
      return Case.updateItem;
    }
  } else if (articles.length > 0)  {
    if (L.isNil(state.entities.articles[articles[0]])) {
      return Case.addItem;
    } else {
      return Case.updateItem;
    }
  } else if (memberships.length > 0)  {
    if (L.isNil(state.entities.memberships[memberships[0]])) {
      return Case.addItem;
    } else {
      return Case.updateItem;
    }
  } else {
    return Case.refresh;
  }
  
};

const merge = (state: State, payload: State): State => {
  const ns: State = L.cloneDeep(state);
  switch (getCase(ns, payload)) {
    case Case.addContact:
      mergePurchases(ns, payload);
      ns.result = [...ns.result, ...payload.result];
      ns.entities.contactNodes = {...ns.entities.contactNodes, ...payload.entities.contactNodes};
      break;
    case Case.addItem:
      payload.result.forEach((id) => {
        const stateNode: ContactNode = ns.entities.contactNodes[id];
        const payloadNode: ContactNode = payload.entities.contactNodes[id];
        stateNode.enrolments = L.concat(stateNode.enrolments, payloadNode.enrolments);
        stateNode.applications = L.concat(stateNode.applications, payloadNode.applications);
        stateNode.memberships = L.concat(stateNode.memberships, payloadNode.memberships);
        stateNode.articles = L.concat(stateNode.articles, payloadNode.articles);
        stateNode.vouchers = L.concat(stateNode.vouchers, ...payloadNode.vouchers);
      });
      mergePurchases(ns, payload);
      break;
    case Case.updateItem:
      mergePurchases(ns, payload);
      break;
    case Case.refresh:
      return payload;
    default:
      throw new Error();
  }
  return ns;
};

const mergePurchases = (ns: State, payload: State): State => {
  ns.entities.enrolments = {...ns.entities.enrolments, ...payload.entities.enrolments};
  ns.entities.applications = {...ns.entities.applications, ...payload.entities.applications};
  ns.entities.memberships = {...ns.entities.memberships, ...payload.entities.memberships};
  ns.entities.articles = {...ns.entities.articles, ...payload.entities.articles};
  ns.entities.vouchers = {...ns.entities.vouchers, ...payload.entities.vouchers};
  return ns;
};
