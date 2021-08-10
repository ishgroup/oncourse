import { normalize } from 'normalizr';
import { sessionSchema, SessionState } from '../model/Session';
import { IAction } from '../model/IshAction';
import { GET_USER_SESSIONS_FULFILLED } from '../actions/SessionActions';

const initial: SessionState = {
  entities: {
    session: {}
  },
  result: []
};

export default (state = initial, action: IAction): SessionState => {
  switch (action.type) {
    case GET_USER_SESSIONS_FULFILLED:
      return {
        ...state,
        ...normalize(action.payload, sessionSchema)
      };

    default:
      return state;
  }
};
