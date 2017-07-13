import {GET_CONCESSION_TYPES_REQUEST, GET_CONTACT_CONCESSIONS_REQUEST, UPDATE_CONTACT_CONCESSION} from "../actions/Actions";
import {FULFILLED} from "../../../../common/actions/ActionUtils";
import {IAction} from "../../../../actions/IshAction";

export interface ConcessionState {
  contactId: string;
  types?: any[];
  concessions?: {};
}

const defaultState = {
  contactId: null,
  types: [],
  concessions: {},
};

export const Reducer = (state: ConcessionState = defaultState, action: IAction<any>): ConcessionState => {
  switch (action.type) {
    case UPDATE_CONTACT_CONCESSION:
      return {...state, contactId: action.payload};

    case FULFILLED(GET_CONCESSION_TYPES_REQUEST):
      return {...state, types: action.payload};

    case FULFILLED(GET_CONTACT_CONCESSIONS_REQUEST):
      return {...state, concessions: action.payload || {}};

    default:
      return state;
  }
};
