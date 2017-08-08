import {GET_CONCESSION_TYPES_REQUEST, GET_CONTACT_CONCESSIONS_AND_MEMBERSHIPS_REQUEST, UPDATE_CONTACT_CONCESSION} from "../actions/Actions";
import {FULFILLED} from "../../../../common/actions/ActionUtils";
import {IAction} from "../../../../actions/IshAction";
import {Concession, StudentMembership} from "../../../../model";

export interface ConcessionState {
  contactId: string;
  types?: any[];
  concessions?: Concession[];
  memberships?: StudentMembership[];
}

const defaultState = {
  contactId: null,
  types: [],
  concessions: [],
  memberships: [],
};

export const Reducer = (state: ConcessionState = defaultState, action: IAction<any>): ConcessionState => {
  switch (action.type) {
    case UPDATE_CONTACT_CONCESSION:
      return {...state, contactId: action.payload};

    case FULFILLED(GET_CONCESSION_TYPES_REQUEST):
      return {...state, types: action.payload};

    case FULFILLED(GET_CONTACT_CONCESSIONS_AND_MEMBERSHIPS_REQUEST):
      return {
        ...state,
        concessions: action.payload.concessions || [],
        memberships: action.payload.memberships || [],
      };

    default:
      return state;
  }
};
