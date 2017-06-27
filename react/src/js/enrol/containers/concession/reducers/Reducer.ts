import {UPDATE_CONCESSION_CONTACT} from "../actions/Actions";

export interface ConcessionState {
  contactId: string;
}

const defaultState = {
  contactId: null,
};

export const Reducer = (state: ConcessionState = defaultState, action: {type: string, payload: any}): any => {
  switch (action.type) {
    case UPDATE_CONCESSION_CONTACT:
      return {...state, contactId: action.payload};
    default:
      return state;
  }
};
