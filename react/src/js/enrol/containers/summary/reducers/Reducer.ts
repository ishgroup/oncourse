import * as SummaryActions from "../actions/Actions";
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
  refresh
}

const getCase = (state: State, payload: State): Case => {
  const contactId: string = payload.result[0];

  if (state.result.length == 0) {
    return Case.refresh;
  } else if (state.result.indexOf(contactId) < 0) {
    return Case.addContact
  }
  
  const enrolments = payload.entities.contactNodes[contactId].enrolments;
  const applications = payload.entities.contactNodes[contactId].applications;
  
  if (enrolments) {
    if (L.isNil(state.entities.enrolments[enrolments[0]])) {
      return Case.addItem
    } else {
      return Case.updateItem
    }
  } else if (applications)  {
    if (L.isNil(state.entities.applications[applications[0]])) {
      return Case.addItem
    } else {
      return Case.updateItem
    }
  } else {
    return Case.refresh
  }
  
};

const merge = (state: State, payload: State): State => {
  const ns: State = L.cloneDeep(state);
  switch (getCase(state, payload)) {
    case Case.addContact:
      ns.result = [...ns.result, ...payload.result];
      ns.entities.enrolments = {...ns.entities.enrolments, ...payload.entities.enrolments};
      ns.entities.contactNodes = {...ns.entities.contactNodes, ...payload.entities.contactNodes};
      break;
    case Case.addItem:
      payload.result.forEach((id) => {
        const stateNode: ContactNode = ns.entities.contactNodes[id];
        const payloadNode: ContactNode = payload.entities.contactNodes[id];
        stateNode.enrolments = [...stateNode.enrolments, ...payloadNode.enrolments];
        stateNode.applications = [...stateNode.applications, ...payloadNode.applications];
      });
    case Case.updateItem:
      ns.entities.enrolments = {...ns.entities.enrolments, ...payload.entities.enrolments};
      ns.entities.applications = {...ns.entities.applications, ...payload.entities.applications};
      break;
    case Case.refresh:
      return payload;
    default:
      throw new Error();
  }
  return ns;
};