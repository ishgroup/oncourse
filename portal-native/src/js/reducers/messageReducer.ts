import { IAction } from '../model/IshAction';
import { FETCH_FAIL } from '../actions/FetchActions';
import { MessageState } from '../model/Message';
import { SET_MESSAGE } from '../actions/MessageActions';

const initial: MessageState = {
  message: null
};

export default (state = initial, action: IAction) => {
  switch (action.type) {
    case SET_MESSAGE:
    case FETCH_FAIL:
      return {
        ...state,
        ...action.payload
      };

    default:
      return state;
  }
};
