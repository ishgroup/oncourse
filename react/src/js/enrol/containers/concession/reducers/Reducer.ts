import {GET_CONCESSION_TYPES_REQUEST, UPDATE_CONCESSION_CONTACT} from "../actions/Actions";
import {FULFILLED} from "../../../../common/actions/ActionUtils";

export interface ConcessionState {
  contactId: string;
}

const defaultState = {
  contactId: null,
  types: [],
  concessions: {},
};

export const Reducer = (state: ConcessionState = defaultState, action: {type: string, payload: any}): any => {
  switch (action.type) {
    case UPDATE_CONCESSION_CONTACT:
      return {...state, contactId: action.payload};
    case FULFILLED(GET_CONCESSION_TYPES_REQUEST):
      return {...state, types: action.payload};
    default:
      return state;
  }
};
