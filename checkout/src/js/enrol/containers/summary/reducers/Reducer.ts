import * as SummaryActions from "../actions/Actions";
import {ContactNodeStorage, ContactNodeToState, State} from "./State";
import {RESET_CHECKOUT_STATE, SHOW_MESSAGES} from "../../../actions/Actions";
import * as L from "lodash";
import {IAction} from "../../../../actions/IshAction";
import {FULFILLED} from "../../../../common/actions/ActionUtils";
import {RESET_PAYMENT_STATE} from "../../payment/actions/Actions";

const ItemsKeys = [
  "enrolments",
  "applications",
  "memberships",
  "articles",
  "vouchers",
  "waitingLists",
]

export const Reducer = (state: State = ContactNodeToState([]), action: IAction<any>): State => {
  const ns: State = L.cloneDeep(state);

  switch (action.type) {
    case  SummaryActions.REPLACE_ITEM:
      action.payload.replace.result.forEach(id => {
        const stateNode: ContactNodeStorage = ns.entities.contactNodes[id];
        const replaceNode: ContactNodeStorage = action.payload.replace.entities.contactNodes[id];
        const replacementNode: ContactNodeStorage = action.payload.replacement.entities.contactNodes[id];
        ItemsKeys.forEach(key => {
          const index = stateNode[key].indexOf(replaceNode[key][0]);
          if (index !== -1) {
            delete ns.entities[key][Object.keys(action.payload.replace.entities[key])[0]];
            ns.entities[key] = {...ns.entities[key], ...action.payload.replacement.entities[key]};
            stateNode[key].splice(index,1,replacementNode[key][0]);
          }
        })
      });
      return ns;

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

    case SummaryActions.SET_RESULT_DETAILS_CONTACTS:
      return {
        ...state,
        resultDetails: {
          ...state.resultDetails,
          contacts: action.payload
        }
      }

    case SummaryActions.SET_RESULT_DETAILS_CORPORATE_PASS:
      return {
        ...state,
        resultDetails: {
          ...state.resultDetails,
          corporatePass: action.payload
        }
      }

    case RESET_CHECKOUT_STATE:
      return ContactNodeToState([]);

    case RESET_PAYMENT_STATE:
      return {
        ...state,
        resultDetails: {}
      }

    case SummaryActions.SELECT_ITEM_REQUEST:
      return {
        ...state,
        fetching: true,
      };

    case SummaryActions.REMOVE_ITEM_FROM_SUMMARY:
      const {type, id} = action.payload;
      if (!type && !id) return state;

      state.result.forEach(cid => {
        ns.entities[type] && delete ns.entities[type][`${cid}-${id}`];
        if(ns.entities.contactNodes[cid] && ns.entities.contactNodes[cid][type]) {
          ns.entities.contactNodes[cid][type] = ns.entities.contactNodes[cid][type].filter(eId => eId !== `${cid}-${id}`);
        }
      });

      Object.keys(ns.entities).forEach(entity => {
        Object.keys(ns.entities[entity]).forEach(key => {
          if (
            (ns.entities[entity][key].relatedClassId && ns.entities[entity][key].relatedClassId === id)
            || (ns.entities[entity][key].relatedProductId && ns.entities[entity][key].relatedProductId === id)
          ) {
            delete ns.entities[entity][key];
          }
        })
      })

      return ns;

    // Remove contact node and enrolments from summary state
    case SummaryActions.REMOVE_CONTACT_FROM_SUMMARY:
      const {contactId} = action.payload;

      ['enrolments', 'memberships', 'vouchers', 'articles', 'applications'].map(item =>
        ns.entities.contactNodes[contactId][item].map(id => delete ns.entities[item][id]),
      );
      ns.result = state.result.filter(id => id !== contactId);
      delete ns.entities.contactNodes[contactId];
      return ns;

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

  // Make sure that field headings and related ids get merged
  Object.keys(ns.entities.enrolments).forEach(key => {
    if(payload.entities.enrolments && payload.entities.enrolments[key] && ns.entities.enrolments && ns.entities.enrolments[key]) {
      if (!ns.entities.enrolments[key].fieldHeadings.length && payload.entities.enrolments[key].fieldHeadings.length) {
        ns.entities.enrolments[key].fieldHeadings = payload.entities.enrolments[key].fieldHeadings
      }
      if (!ns.entities.enrolments[key].relatedClassId && payload.entities.enrolments[key].relatedClassId) {
        ns.entities.enrolments[key].relatedClassId = payload.entities.enrolments[key].relatedClassId
      }
      if (!ns.entities.enrolments[key].relatedProductId && payload.entities.enrolments[key].relatedProductId) {
        ns.entities.enrolments[key].relatedProductId = payload.entities.enrolments[key].relatedProductId
      }
    }
  });

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
